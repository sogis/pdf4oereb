package ch.so.agi.oereb.pdf4oereb.saxon.ext;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

public class PlanForLandRegisterMainPageImage implements ExtensionFunction {
    Logger log = LoggerFactory.getLogger(PlanForLandRegisterMainPageImage.class);
    
    private final String imageFormat = "png";

	@Override
	public QName getName() {
        return new QName("http://oereb.agi.so.ch", "createPlanForLandRegisterMainPageImage");
	}

	@Override
	public SequenceType getResultType() {
        return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE);
	}

	@Override
	public SequenceType[] getArgumentTypes() {
        return new SequenceType[] { SequenceType.makeSequenceType(ItemType.ANY_ITEM, OccurrenceIndicator.ONE), 
        		SequenceType.makeSequenceType(ItemType.ANY_ITEM, OccurrenceIndicator.ONE)};
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
		XdmNode baseImageNode  = (XdmNode) arguments[0];
		XdmNode overlayImageNode = (XdmNode) arguments[1];
		try {
			byte[] baseImageByteArray = Base64.getDecoder().decode(baseImageNode.getUnderlyingValue().getStringValue());
			byte[] overlayImageByteArray = Base64.getDecoder().decode(overlayImageNode.getUnderlyingValue().getStringValue());
	
	    	InputStream baseImageInputStream = new ByteArrayInputStream(baseImageByteArray);
			BufferedImage baseImageBufferedImage = ImageIO.read(baseImageInputStream);
	
	    	InputStream overlayImageInputStream = new ByteArrayInputStream(overlayImageByteArray);
			BufferedImage overlayImageBufferedImage = ImageIO.read(overlayImageInputStream);
	
			int imageWidthPx = baseImageBufferedImage.getWidth();
			int imageHeightPx = baseImageBufferedImage.getHeight();
	
	        BufferedImage combinedImage = new BufferedImage(imageWidthPx, imageHeightPx, BufferedImage.TYPE_4BYTE_ABGR_PRE);
	        
	        Graphics2D g = (Graphics2D) combinedImage.getGraphics();
//	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//	        g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
//	        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

	        g.drawImage(baseImageBufferedImage, 0, 0, null);
	        g.drawImage(overlayImageBufferedImage, 0, 0, null);
	                   
//	        Path tmpDirectory = Files.createTempDirectory(Paths.get(System.getProperty("java.io.tmpdir")), "oereb_extract_images_");
//	        Path outputfilePath = Paths.get(tmpDirectory.toString(), "mergedimage.png");
//	        ImageIO.write(combinedImage, imageFormat, outputfilePath.toFile());            
//	        byte[] combinedImageByteArray = Files.readAllBytes(outputfilePath);
//	        log.info(outputfilePath.toString());
	        
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(combinedImage, imageFormat, baos); 
			baos.flush();
			byte[] combinedImageByteArray = baos.toByteArray();
			baos.close();          

	        return new XdmAtomicValue(new net.sf.saxon.value.Base64BinaryValue(combinedImageByteArray).asAtomic().getStringValue());
		} catch (IOException e) {
			e.printStackTrace();
			throw new SaxonApiException(e.getMessage());
		}
	}
}
