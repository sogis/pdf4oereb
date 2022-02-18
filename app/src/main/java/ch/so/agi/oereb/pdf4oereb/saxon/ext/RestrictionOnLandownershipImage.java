package ch.so.agi.oereb.pdf4oereb.saxon.ext;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmNodeKind;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.trans.XPathException;

public class RestrictionOnLandownershipImage implements ExtensionFunction {
    Logger log = LoggerFactory.getLogger(RestrictionOnLandownershipImage.class);
    
    private final String imageFormat = "png";

    @Override
    public QName getName() {
        return new QName("http://oereb.so.ch", "createRestrictionOnLandownershipImages");
    }

    @Override
    public SequenceType getResultType() {
        return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE);
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[] { 
                SequenceType.makeSequenceType(ItemType.ANY_NODE, OccurrenceIndicator.ONE),
                SequenceType.makeSequenceType(ItemType.ANY_NODE, OccurrenceIndicator.ONE),
                SequenceType.makeSequenceType(ItemType.ANY_NODE, OccurrenceIndicator.ONE)
            };
    }

    /**
     * Returns a base64 string as XdmValue of the merged image (plan for land register main page map
     * and the overlay image). 
     * 
     * @param arguments an array of XdmValues containing the restriction on landownership nodes,  
     *                  the plan for land register map node and the overlay image.
     * @return          the merged image as base64 string 
     */
    @Override
    public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
        XdmNode oerebMap = (XdmNode) arguments[0];
        XdmNode backgroundMapNode = (XdmNode) arguments[1];
        XdmNode overlayImageNode = (XdmNode) arguments[2];

        List<MapImage> mapImageList = new ArrayList<MapImage>();
                
//        BufferedImage layerImage =  null;
//        int layerIndex = 0;
//        Double layerOpacity = 0.6;
//        
//        String oerebMapString = oerebMap.getUnderlyingValue().getStringValue();
//        try {
//            if (oerebMapString.startsWith("http")) {
//                byte[] imageByteArray = ch.so.agi.oereb.pdf4oereb.utils.Image.getImage(oerebMapString);
//                InputStream imageInputStream = new ByteArrayInputStream(imageByteArray);
//                layerImage = ImageIO.read(imageInputStream);
//            } else {
//                byte[] imageByteArray = Base64.getDecoder().decode(oerebMap.getUnderlyingValue().getStringValue().trim());
//                InputStream imageInputStream = new ByteArrayInputStream(imageByteArray);
//                layerImage = ImageIO.read(imageInputStream);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new SaxonApiException(e.getMessage());
//        }
//
//        log.info(layerImage.toString());

        try {
            Iterator<XdmItem> it = oerebMap.iterator();
            while(it.hasNext()) {
                XdmNode mapNode = (XdmNode) it.next();
                
                BufferedImage layerImage =  null;
                int layerIndex = 0;
                Double layerOpacity = 0.6;
                
                // grap the images
                // embedded
                Iterator<XdmNode> jt = mapNode.children("Image").iterator();
                while(jt.hasNext()) {
                    XdmNode imageNode = jt.next();
                    Iterator<XdmNode> kt = imageNode.children().iterator();
                    while(kt.hasNext()) {
                        XdmNode subNode = (XdmNode) kt.next();
                        if (subNode.getNodeKind().equals(XdmNodeKind.ELEMENT)) {
                            // TODO: Sprache
                            if (subNode.getNodeName().getLocalName().toString().equalsIgnoreCase("Image")) {
                                byte[] imageByteArray = Base64.getDecoder().decode(subNode.getUnderlyingValue().getStringValue().trim());
                                InputStream imageInputStream = new ByteArrayInputStream(imageByteArray);
                                layerImage = ImageIO.read(imageInputStream);
                            }
                        }
                    }
                }
                // wms only when not embedded
                if (layerImage == null) {
                    jt = mapNode.children("ReferenceWMS").iterator();
                    while(jt.hasNext()) {
                        XdmNode wmsNode = (XdmNode) jt.next();
                        Iterator<XdmNode> kt = wmsNode.children("LocalisedText").iterator();
                        while(kt.hasNext()) {
                            XdmNode localisedTextNode = (XdmNode) kt.next();
                            Iterator<XdmNode> lt = localisedTextNode.children().iterator();
                            while(lt.hasNext()) {
                                XdmNode subNode = (XdmNode) lt.next();
                                if (subNode.getNodeKind().equals(XdmNodeKind.ELEMENT)) {
                                    // TODO: Sprache
                                    if (subNode.getNodeName().getLocalName().toString().equalsIgnoreCase("Text")) {
                                        String requestString = subNode.getTypedValue().getUnderlyingValue().getStringValue().trim();
                                        byte[] imageByteArray = ch.so.agi.oereb.pdf4oereb.utils.Image.getImage(requestString);
                                        InputStream imageInputStream = new ByteArrayInputStream(imageByteArray);
                                        layerImage = ImageIO.read(imageInputStream);
                                    }
                                }
                            }
                        }
                    }
                }
                                
                // grap opacity value of image
                Iterator<XdmNode> lm = mapNode.children("layerOpacity").iterator();
                while(lm.hasNext()) {
                    XdmNode layerOpacityNode = lm.next();
                    layerOpacity = Double.valueOf(layerOpacityNode.getUnderlyingNode().getStringValue());
                }

                // grab the layer index of the images
                Iterator<XdmNode> kt = mapNode.children("layerIndex").iterator();
                while(kt.hasNext()) {
                    XdmNode layerIndexNode = kt.next();
                    layerIndex = Integer.valueOf(layerIndexNode.getUnderlyingNode().getStringValue());
                }
                
                // Falls weisse Flächen nicht transparent sind.
//                final ImageFilter filter = new RGBImageFilter()
//                {
//                   // the color we are looking for (white)... Alpha bits are set to opaque
//                   public int markerRGB = Color.white.getRGB() | 0xFFFFFFFF;
//
//                   public final int filterRGB(final int x, final int y, final int rgb)
//                   {
//                      if ((rgb | 0xFF000000) == markerRGB)
//                      {
//                         // Mark the alpha bits as zero - transparent
//                         return 0x00FFFFFF & rgb;
//                      }
//                      else
//                      {
//                         // nothing to do
//                         return rgb;
//                      }
//                   }
//                };
//
//                final ImageProducer ip = new FilteredImageSource(layerImage.getSource(), filter);
//                Image img = Toolkit.getDefaultToolkit().createImage(ip);
//                BufferedImage transparentLayerImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
//                Graphics2D g2d = transparentLayerImage.createGraphics();
//                g2d.drawImage(img, 0, 0, null);
//                g2d.dispose();
//
//                MapImage mapImage = new MapImage(layerIndex, layerOpacity, transparentLayerImage);
                
                MapImage mapImage = new MapImage(layerIndex, layerOpacity, layerImage);
                mapImageList.add(mapImage);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new SaxonApiException(e.getMessage());
        }
        
        // PlanForLandRegister
        try {
            // grap the images
            // embedded
            byte[] backgroundImageByteArray = null;
            Iterator<XdmNode> jt = backgroundMapNode.children("Image").iterator();
            while(jt.hasNext()) {
                XdmNode imageNode = jt.next();
                Iterator<XdmNode> kt = imageNode.children().iterator();
                while(kt.hasNext()) {
                    XdmNode subNode = (XdmNode) kt.next();
                    if (subNode.getNodeKind().equals(XdmNodeKind.ELEMENT)) {
                        // TODO: Sprache
                        if (subNode.getNodeName().getLocalName().toString().equalsIgnoreCase("Image")) {
                            backgroundImageByteArray = Base64.getDecoder().decode(subNode.getUnderlyingValue().getStringValue().trim());
                        }
                    }
                }
            }
            // wms only when not embedded
            if (backgroundImageByteArray == null) {
                jt = backgroundMapNode.children("ReferenceWMS").iterator();
                while(jt.hasNext()) {
                    XdmNode wmsNode = (XdmNode) jt.next();
                    Iterator<XdmNode> kt = wmsNode.children("LocalisedText").iterator();
                    while(kt.hasNext()) {
                        XdmNode localisedTextNode = (XdmNode) kt.next();
                        Iterator<XdmNode> lt = localisedTextNode.children().iterator();
                        while(lt.hasNext()) {
                            XdmNode subNode = (XdmNode) lt.next();
                            if (subNode.getNodeKind().equals(XdmNodeKind.ELEMENT)) {
                                // TODO: Sprache
                                if (subNode.getNodeName().getLocalName().toString().equalsIgnoreCase("Text")) {
                                    String requestString = subNode.getTypedValue().getUnderlyingValue().getStringValue().trim();
                                    backgroundImageByteArray = ch.so.agi.oereb.pdf4oereb.utils.Image.getImage(requestString);
                                }
                            }
                        }
                    }
                }
            }
            
            InputStream backgroundImageInputStream = new ByteArrayInputStream(backgroundImageByteArray);
            BufferedImage backgroundImageBufferedImage = ImageIO.read(backgroundImageInputStream);
            
            // grap opacity and index value of image
            int layerIndex = 0;
            Double layerOpacity = 0.6;

            Iterator<XdmNode> lm = backgroundMapNode.children("layerOpacity").iterator();
            while(lm.hasNext()) {
                XdmNode layerOpacityNode = lm.next();
                layerOpacity = Double.valueOf(layerOpacityNode.getUnderlyingNode().getStringValue());
            }

            Iterator<XdmNode> kt = backgroundMapNode.children("layerIndex").iterator();
            while(kt.hasNext()) {
                XdmNode layerIndexNode = kt.next();
                layerIndex = Integer.valueOf(layerIndexNode.getUnderlyingNode().getStringValue());
            }

            MapImage mapImage = new MapImage(layerIndex, layerOpacity, backgroundImageBufferedImage);
            mapImageList.add(mapImage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SaxonApiException(e.getMessage());
        } 
        
        // sort list of images according their layer index value
        mapImageList.sort(Comparator.comparing(MapImage::getLayerIndex));
        
        // merge the images
        BufferedImage newImage = null;
        Graphics2D g = null;

        for(MapImage mapImage : mapImageList) {
            BufferedImage imageBufferedImage = mapImage.getLayerImage();
            
            int imageWidthPx = imageBufferedImage.getWidth();
            int imageHeightPx = imageBufferedImage.getHeight();
            
            if (newImage == null) {
                // PDF/A-1 does not support transparency (TYPE_4BYTE_ABGR_PRE).
                newImage = new BufferedImage(imageWidthPx, imageHeightPx, BufferedImage.TYPE_INT_RGB);

                g = (Graphics2D) newImage.getGraphics();
                g.setBackground(Color.WHITE);
                g.clearRect(0, 0, imageWidthPx, imageHeightPx);
            }
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (mapImage.getLayerOpacity())));
            //g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g.drawImage(imageBufferedImage, 0, 0, null);
        }
        
        // merge overlay image
        try {
            byte[] overlayImageByteArray = Base64.getDecoder().decode(overlayImageNode.getUnderlyingValue().getStringValue());
            InputStream overlayImageInputStream = new ByteArrayInputStream(overlayImageByteArray);
            BufferedImage overlayImageBufferedImage = ImageIO.read(overlayImageInputStream);
            
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g.drawImage(overlayImageBufferedImage, 0, 0, null);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SaxonApiException(e.getMessage());
        } 
        
        // write image
        byte[] newImageByteArray = null;
        try {           
            //ImageIO.write(newImage, "png", new File("/Users/stefan/tmp/new_image.png"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(newImage, imageFormat, baos);
            baos.flush();
            newImageByteArray = baos.toByteArray();
            baos.close();         
        } catch (IOException e) {
            e.printStackTrace();
            throw new SaxonApiException(e.getMessage());
        }
        return new XdmAtomicValue(new net.sf.saxon.value.Base64BinaryValue(newImageByteArray).asAtomic().getStringValue());
    }
}
