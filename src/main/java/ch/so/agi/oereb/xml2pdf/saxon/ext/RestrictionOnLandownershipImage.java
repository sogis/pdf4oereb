package ch.so.agi.oereb.xml2pdf.saxon.ext;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Composite;
import org.geotools.renderer.composite.BlendComposite;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
        return new QName("http://oereb.agi.so.ch", "mergeRestrictionOnLandownershipImages");
	}

	@Override
	public SequenceType getResultType() {
        return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE);
	}

	@Override
	public SequenceType[] getArgumentTypes() {
        return new SequenceType[] { SequenceType.makeSequenceType(ItemType.ANY_ITEM, OccurrenceIndicator.ZERO_OR_MORE),
        		SequenceType.makeSequenceType(ItemType.ANY_ITEM, OccurrenceIndicator.ONE)};
	}

	@Override
	public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
		// the restriction on landownership maps
		XdmValue restrictionOnLandownershipMaps = (XdmValue) arguments[0];

		// the background image
		XdmValue backgroundImage = (XdmValue) arguments[1];

		// the list that stores the image, the layer index and the opacity value
		List<MapImage> mapImageList = new ArrayList<MapImage>();
		
		Iterator<XdmItem> it = restrictionOnLandownershipMaps.iterator();
		int i=1;
		while(it.hasNext()) {
			XdmNode mapNode = (XdmNode) it.next();
			
			BufferedImage layerImage =  null;
			int layerIndex = 0;
			int layerOpacity = 1;
			
			// grap the images
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
			// grap opacity value of image
			// TODO

			// grab the layer index of the images
			Iterator<XdmNode> kt = mapNode.children("layerIndex").iterator();
			while(kt.hasNext()) {
				XdmNode layerIndexNode = kt.next();
				layerIndex = Integer.valueOf(layerIndexNode.getUnderlyingNode().getStringValue());
			}
			
			MapImage mapImage = new MapImage(layerIndex, 1, layerImage);
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
				newImage = new BufferedImage(imageWidthPx, imageHeightPx, BufferedImage.TYPE_4BYTE_ABGR_PRE);
				g = (Graphics2D) newImage.getGraphics();
			}
			g.drawImage(imageBufferedImage, 0, 0, null);
		}
		
		// merge background image
		try {
			byte[] backgroundImageByteArray = Base64.getDecoder().decode(backgroundImage.getUnderlyingValue().getStringValue());
			InputStream backgroundImageInputStream = new ByteArrayInputStream(backgroundImageByteArray);
			BufferedImage backgroundImageBufferedImage = ImageIO.read(backgroundImageInputStream);
			
//			g.setComposite(BlendComposite.MULTIPLY_COMPOSITE);
			g.drawImage(backgroundImageBufferedImage, 0, 0, null);
		} catch (IOException | XPathException e) {
			e.printStackTrace();
			throw new SaxonApiException(e.getMessage());
		} 

		// write image
		byte[] newImageByteArray = null;
		try {
//	        Path outputfilePath = Paths.get("/Users/stefan/tmp/", "image"+String.valueOf(Math.random())+".png");
//	        ImageIO.write(newImage, imageFormat, outputfilePath.toFile());            
//	        log.info(outputfilePath.toString());
	        
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
