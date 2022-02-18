package ch.so.agi.oereb.pdf4oereb.saxon.ext;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.OccurrenceIndicator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.trans.XPathException;

public class FixImage implements ExtensionFunction {
    Logger log = LoggerFactory.getLogger(FixImage.class);
    
    private final String imageFormat = "png";

    @Override
    public QName getName() {
        return new QName("http://oereb.so.ch", "fixImage");
    }

    @Override
    public SequenceType getResultType() {
        return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE);
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[] { SequenceType.makeSequenceType(ItemType.ANY_ITEM, OccurrenceIndicator.ONE)};
    }

	/**
     * Returns a base64 string as XdmValue of the merged image (plan for land register main page map
     * and the overlay image). 
     * 
     * @param arguments Reads any readable image from a base64 string and saves it as a 24bit image.
     *                  Solves the problem with 8bit (paletted) legend icons.
     * @return          the 24bit image as base64 string 
     */
    @Override
    public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
        XdmValue symbolValue = (XdmValue) arguments[0];
        XdmNode symbolNode = (XdmNode) symbolValue;

        try {  
            byte[] imageByteArray = null;
            
            if (symbolNode.getNodeName().toString().endsWith("Ref")) {
                String url = symbolValue.getUnderlyingValue().getStringValue();
                String decodedUrl = java.net.URLDecoder.decode(url.trim(), "UTF-8"); // trim fixes some illegal character exception
                imageByteArray = ch.so.agi.oereb.pdf4oereb.utils.Image.getImage(decodedUrl);
            } else {
                imageByteArray = Base64.getDecoder().decode(symbolValue.getUnderlyingValue().getStringValue());
            }
            
            InputStream imageInputStream = new ByteArrayInputStream(imageByteArray);
            BufferedImage imageBufferedImage = ImageIO.read(imageInputStream);
        
            int imageWidthPx = imageBufferedImage.getWidth();
            int imageHeightPx = imageBufferedImage.getHeight();
            
            // PDF/A-1 does not support transparency (TYPE_4BYTE_ABGR_PRE)
            BufferedImage fixedImage = new BufferedImage(imageWidthPx, imageHeightPx, BufferedImage.TYPE_INT_RGB);

            Graphics2D g = (Graphics2D) fixedImage.getGraphics();
            g.setBackground(Color.WHITE);
            g.clearRect(0, 0, imageWidthPx, imageHeightPx);
            
            g.drawImage(imageBufferedImage, 0, 0, null);
                                   
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(fixedImage, imageFormat, baos); 
            baos.flush();
            byte[] combinedImageByteArray = baos.toByteArray();
            baos.close();          

            return new XdmAtomicValue(new net.sf.saxon.value.Base64BinaryValue(combinedImageByteArray).asAtomic().getStringValue());
        } catch (Exception e) {
            e.printStackTrace();
            throw new SaxonApiException(e.getMessage());
        }
    }
}
