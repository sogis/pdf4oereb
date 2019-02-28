package ch.so.agi.oereb.pdf4oereb.saxon.ext;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.trans.XPathException;

public class RestrictionOnLandownershipImage implements ExtensionFunction {
    Logger log = LoggerFactory.getLogger(RestrictionOnLandownershipImage.class);
    
    private final String imageFormat = "png";

    @Override
    public QName getName() {
        return new QName("http://oereb.agi.so.ch", "createRestrictionOnLandownershipImages");
    }

    @Override
    public SequenceType getResultType() {
        return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE);
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[] { SequenceType.makeSequenceType(ItemType.ANY_ITEM, OccurrenceIndicator.ZERO_OR_MORE),
                SequenceType.makeSequenceType(ItemType.ANY_ITEM, OccurrenceIndicator.ONE),
                SequenceType.makeSequenceType(ItemType.ANY_ITEM, OccurrenceIndicator.ONE)};
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
        // the restriction on landownership maps
        XdmValue restrictionOnLandownershipMaps = (XdmValue) arguments[0];

        // the background image (which will be put _over_ the restriction images...)
        XdmNode backgroundMapNode = (XdmNode) arguments[1];
        
        // the overlay image
        XdmNode overlayImageNode = (XdmNode) arguments[2];

        // the list that stores the image, the layer index and the opacity value
        List<MapImage> mapImageList = new ArrayList<MapImage>();
        
        Iterator<XdmItem> it = restrictionOnLandownershipMaps.iterator();
        int i=1;
        while(it.hasNext()) {
            XdmNode mapNode = (XdmNode) it.next();
            
            BufferedImage layerImage =  null;
            int layerIndex = 0;
            Double layerOpacity = 1.0;
            
            // grap the images
            // embedded
            Iterator<XdmNode> jt = mapNode.children("Image").iterator();
            while(jt.hasNext()) {
                XdmNode imageNode = jt.next();
                try {
                    byte[] imageByteArray = Base64.getDecoder().decode(imageNode.getUnderlyingValue().getStringValue());
                    InputStream imageInputStream = new ByteArrayInputStream(imageByteArray);
                    layerImage = ImageIO.read(imageInputStream);
                }  catch (IOException e) {
                    e.printStackTrace();
                    throw new SaxonApiException(e.getMessage());
                }
            }
            // wms only when not embedded
            if (layerImage == null) {
                jt = mapNode.children("ReferenceWMS").iterator();
                while(jt.hasNext()) {
                    XdmNode imageNode = (XdmNode) jt.next();
                    XdmValue referenceWmsXdmValue = imageNode.getTypedValue();
                    try {
                        byte[] imageByteArray = WebMapService.getMap(referenceWmsXdmValue.getUnderlyingValue().getStringValue());
                        InputStream imageInputStream = new ByteArrayInputStream(imageByteArray);
                        layerImage = ImageIO.read(imageInputStream);
                        
//                      ImageIO.write(layerImage, "png", new File("/Users/stefan/tmp/layerImage.png"));                 
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                        throw new SaxonApiException(e.getMessage());
                    }
                    break;
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
            
            MapImage mapImage = new MapImage(layerIndex, layerOpacity, layerImage);
            mapImageList.add(mapImage);
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
//              newImage = new BufferedImage(imageWidthPx, imageHeightPx, BufferedImage.TYPE_4BYTE_ABGR_PRE);
                // PDF/A-1 does not support transparency.
                newImage = new BufferedImage(imageWidthPx, imageHeightPx, BufferedImage.TYPE_INT_RGB);

                g = (Graphics2D) newImage.getGraphics();
                g.setBackground(Color.WHITE);
                g.clearRect(0, 0, imageWidthPx, imageHeightPx);
            }
            // TODO! Remember!!!
//          g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (mapImage.getLayerOpacity())));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g.drawImage(imageBufferedImage, 0, 0, null);
        }
        
        // merge background image
        try {
            byte[] backgroundImageByteArray = null;
            Iterator<XdmNode> lt = backgroundMapNode.children("Image").iterator();
            while(lt.hasNext()) {
                XdmNode imageNode = (XdmNode) lt.next();
                XdmValue mapImageXdmValue = imageNode.getTypedValue();
                backgroundImageByteArray = Base64.getDecoder().decode(mapImageXdmValue.getUnderlyingValue().getStringValue());
                break;
            }
            // Only get the image by a wms request if it was not embedded.
            if (backgroundImageByteArray == null) {
                lt = backgroundMapNode.children("ReferenceWMS").iterator();
                while(lt.hasNext()) {
                    XdmNode imageNode = (XdmNode) lt.next();
                    XdmValue referenceWmsXdmValue = imageNode.getTypedValue();
                    try {
                        backgroundImageByteArray = WebMapService.getMap(referenceWmsXdmValue.getUnderlyingValue().getStringValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                        throw new SaxonApiException(e.getMessage());
                    }
                    break;
                }
            }
            
            InputStream backgroundImageInputStream = new ByteArrayInputStream(backgroundImageByteArray);
            BufferedImage backgroundImageBufferedImage = ImageIO.read(backgroundImageInputStream);
            
            // if we want to use faaaancy blending modes
            //g.setComposite(BlendComposite.MULTIPLY_COMPOSITE); // still possible without geotools?
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g.drawImage(backgroundImageBufferedImage, 0, 0, null);
        } catch (IOException | XPathException e) {
            e.printStackTrace();
            throw new SaxonApiException(e.getMessage());
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
//          ImageIO.write(newImage, "png", new File("/Users/stefan/tmp/newImage.png"));
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
