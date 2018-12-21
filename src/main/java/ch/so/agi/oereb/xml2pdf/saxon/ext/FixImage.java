package ch.so.agi.oereb.xml2pdf.saxon.ext;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        return new QName("http://oereb.agi.so.ch", "fixImage");
    }

    @Override
    public SequenceType getResultType() {
        return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE);
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[] { SequenceType.makeSequenceType(ItemType.ANY_ITEM, OccurrenceIndicator.ONE)};
    }

    @Override
    public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
        XdmValue symbolValue = (XdmValue) arguments[0];
        try {
            log.info(symbolValue.getUnderlyingValue().getStringValue());
            
            byte[] imageByteArray = Base64.getDecoder().decode(symbolValue.getUnderlyingValue().getStringValue());
    
            InputStream imageInputStream = new ByteArrayInputStream(imageByteArray);
            BufferedImage imageBufferedImage = ImageIO.read(imageInputStream);
        
            int imageWidthPx = imageBufferedImage.getWidth();
            int imageHeightPx = imageBufferedImage.getHeight();
    
            BufferedImage fixedImage = new BufferedImage(imageWidthPx, imageHeightPx, BufferedImage.TYPE_4BYTE_ABGR_PRE);
            
            Graphics2D g = (Graphics2D) fixedImage.getGraphics();
//          g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//          g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
//          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawImage(imageBufferedImage, 0, 0, null);
                       
//          Path tmpDirectory = Files.createTempDirectory(Paths.get(System.getProperty("java.io.tmpdir")), "oereb_extract_images_");
//          Path outputfilePath = Paths.get(tmpDirectory.toString(), "mergedimage.png");
//          ImageIO.write(combinedImage, imageFormat, outputfilePath.toFile());            
//          byte[] combinedImageByteArray = Files.readAllBytes(outputfilePath);
//          log.info(outputfilePath.toString());
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(fixedImage, imageFormat, baos); 
            baos.flush();
            byte[] combinedImageByteArray = baos.toByteArray();
            baos.close();          

            return new XdmAtomicValue(new net.sf.saxon.value.Base64BinaryValue(combinedImageByteArray).asAtomic().getStringValue());
        } catch (XPathException | IOException e) {
            e.printStackTrace();
            throw new SaxonApiException(e.getMessage());
        }
    }

}
