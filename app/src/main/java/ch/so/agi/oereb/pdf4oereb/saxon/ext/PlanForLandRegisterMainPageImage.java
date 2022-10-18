package ch.so.agi.oereb.pdf4oereb.saxon.ext;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Base64;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pngencoder.PngEncoder;

import ch.so.agi.oereb.pdf4oereb.utils.Utils;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.OccurrenceIndicator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmNodeKind;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.trans.XPathException;

public class PlanForLandRegisterMainPageImage implements ExtensionFunction {
    Logger log = LoggerFactory.getLogger(PlanForLandRegisterMainPageImage.class);
    
    private final double referenceDpi = 300.0;

    private final String imageFormat = "png";

    @Override
    public QName getName() {
        return new QName("http://oereb.so.ch", "createPlanForLandRegisterMainPageImage");
    }

    @Override
    public SequenceType getResultType() {
        return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE);
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[] { SequenceType.makeSequenceType(ItemType.ANY_NODE, OccurrenceIndicator.ONE), 
                SequenceType.makeSequenceType(ItemType.ANY_NODE, OccurrenceIndicator.ONE), 
                SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE)};
    }

    /**
     * Returns a base64 string as XdmValue of the merged image (plan for land register main page map
     * and the overlay image). 
     * 
     * @param arguments an array of XdmValues containing the land register main page node  
     *                  and the overlay image.
     * @return          the merged image as base64 string 
     */
    @Override
    public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
        XdmNode baseMapNode  = (XdmNode) arguments[0];
        XdmNode overlayImageNode = (XdmNode) arguments[1];
        String locale;
        try {
            locale = arguments[2].getUnderlyingValue().getStringValue();
        } catch (XPathException e) {
            throw new SaxonApiException(e.getMessage());
        }

        try {
            // Falls das Bild im XML eingebettet ist, wird dieses verwendet.            
            byte[] baseImageByteArray = null;
            String base64String = null;
            int j=0;
            
            Iterator<XdmNode> it = baseMapNode.children("Image").iterator();
            while(it.hasNext()) {
                XdmNode imageNode = (XdmNode) it.next();
                Iterator<XdmNode> jt = imageNode.children("LocalisedBlob").iterator();
                        
                while(jt.hasNext()) {
                    XdmNode subNode = (XdmNode) jt.next();
                    Iterator<XdmNode> kt = subNode.children().iterator();
                    while (kt.hasNext()) {
                        XdmNode subSubNode = (XdmNode) kt.next();
                        if (subSubNode.getNodeKind().equals(XdmNodeKind.ELEMENT)) {
                            if (subSubNode.getNodeName().getLocalName().equalsIgnoreCase("Language")) {
                                if (j == 0) {
                                    base64String = Utils.extractMultilingualText(subSubNode.getParent(), "Blob");
                                }
                                String language = subNode.getTypedValue().getUnderlyingValue().getStringValue().trim();
                                if (language.equalsIgnoreCase(locale)) {
                                    base64String = Utils.extractMultilingualText(subSubNode.getParent(), "Blob");
                                    break;
                                } 
                            } 
                        }
                    }
                    j++;
                }
            }
            if (base64String != null) {
                baseImageByteArray = Base64.getDecoder().decode(base64String);
            }

            // Das Bild wird nur vom WMS bezogen, falls kein eingebettetes Bild vorhanden ist.
            if (baseImageByteArray == null) {
                String requestString = null;
                int i=0;
                it = baseMapNode.children("ReferenceWMS").iterator();
                while(it.hasNext()) {
                    XdmNode wmsNode = (XdmNode) it.next();
                    Iterator<XdmNode> jt = wmsNode.children("LocalisedText").iterator();
                    while(jt.hasNext()) {
                        XdmNode localisedTextNode = (XdmNode) jt.next();
                        Iterator<XdmNode> kt = localisedTextNode.children().iterator();
                        while(kt.hasNext()) {
                            XdmNode subNode = (XdmNode) kt.next();
                            if (subNode.getNodeKind().equals(XdmNodeKind.ELEMENT)) {
                                if (subNode.getNodeName().getLocalName().toString().equalsIgnoreCase("Language")) {
                                    if (i == 0) {
                                        requestString = Utils.extractMultilingualText(subNode.getParent(), "Text");
                                    }
                                    
                                    String language = subNode.getTypedValue().getUnderlyingValue().getStringValue().trim();
                                    if (language.equalsIgnoreCase(locale)) {
                                        requestString = Utils.extractMultilingualText(subNode.getParent(), "Text");
                                        break;
                                    } 
                                }
                            }
                        }
                        i++;
                    }
                    requestString = Utils.fixGetMapRequest(requestString, referenceDpi);
                }
                
                try {
                    baseImageByteArray = ch.so.agi.oereb.pdf4oereb.utils.Image.getImage(requestString);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                    throw new SaxonApiException(e.getMessage());
                }
            }

            byte[] overlayImageByteArray = Base64.getDecoder().decode(overlayImageNode.getUnderlyingValue().getStringValue());
            
            InputStream baseImageInputStream = new ByteArrayInputStream(baseImageByteArray);
            BufferedImage baseImageBufferedImage = ImageIO.read(baseImageInputStream);
    
            InputStream overlayImageInputStream = new ByteArrayInputStream(overlayImageByteArray);
            BufferedImage overlayImageBufferedImage = ImageIO.read(overlayImageInputStream);
    
            int imageWidthPx = baseImageBufferedImage.getWidth();
            int imageHeightPx = baseImageBufferedImage.getHeight();
    
            // PDF/A-1 does not support transparency.
            BufferedImage combinedImage = new BufferedImage(imageWidthPx, imageHeightPx, BufferedImage.TYPE_INT_RGB);
            
            Graphics2D g = (Graphics2D) combinedImage.getGraphics();
//          g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//          g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
//          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g.setBackground(Color.WHITE);
            g.clearRect(0, 0, imageWidthPx, imageHeightPx);

            g.drawImage(baseImageBufferedImage, 0, 0, null);
            g.drawImage(overlayImageBufferedImage, 0, 0, null);
                                
            //ImageIO.write(combinedImage, "png", new File("/Users/stefan/tmp/combined_image.png"));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //ImageIO.write(combinedImage, imageFormat, baos); 
            //baos.flush();
            //byte[] combinedImageByteArray = baos.toByteArray();
            //baos.close();
            
            byte[] combinedImageByteArray = new PngEncoder().withBufferedImage(combinedImage).toBytes();

            return new XdmAtomicValue(new net.sf.saxon.value.Base64BinaryValue(combinedImageByteArray).asAtomic().getStringValue());
        } catch (Exception e) {
            e.printStackTrace();
            throw new SaxonApiException(e.getMessage());
        }
    }
}
