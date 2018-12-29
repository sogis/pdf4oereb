package ch.so.agi.oereb.pdf4oereb.saxon.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;

import javax.xml.transform.stream.StreamSource;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;

public class OverlayImageTest {
    Logger log = LoggerFactory.getLogger(OverlayImageTest.class);
    
    @Test
    // BL
    public void createOverlayImage_Gml1_Ok() throws SaxonApiException, IOException {
		Processor processor = new Processor(false);

    	// namespaces are not considered in the extension function
        XdmNode limitSource = processor.newDocumentBuilder().build(new StreamSource(new File("src/test/resources/OverlayImageTest/createOverlayImage_Gml1_Ok_limitNode.xml")));
        XdmNode limitNode = limitSource.children().iterator().next();
        
        XdmNode mapSource = processor.newDocumentBuilder().build(new StreamSource(new File("src/test/resources/OverlayImageTest/createOverlayImage_Gml1_Ok_mapNode.xml")));
        XdmNode mapNode = mapSource.children().iterator().next();
        
		XdmNode[] arguments = {limitNode, mapNode};
        OverlayImage overlayImage = new OverlayImage();
        XdmAtomicValue resultImage = (XdmAtomicValue) overlayImage.call(arguments);
        
        String base64String = resultImage.getUnderlyingValue().getStringValue();
        
        // Comparing the exact base64 string does not work in different java environments? (e.g. travis/gitlab)
        //String expectedResult = new String(Files.readAllBytes(new File("src/test/resources/OverlayImageTest/createOverlayImage_Gml1_Ok_expectedResult.txt").toPath()));
    	//assertEquals(expectedResult, resultImage.getStringValue(), "Overlay image is not equal.");        

        int resultSize = base64String.length();
        
        assertTrue(resultSize > 6000, "Size of resulting image is too small.");        
    }
    
    @Test
    // NW, ZH
    public void createOverlayImage_Gml2_Ok() throws SaxonApiException, IOException {
		Processor processor = new Processor(false);
		
        XdmNode limitSource = processor.newDocumentBuilder().build(new StreamSource(new File("src/test/resources/OverlayImageTest/createOverlayImage_Gml2_Ok_limitNode.xml")));
        XdmNode limitNode = limitSource.children().iterator().next();
	
        XdmNode mapSource = processor.newDocumentBuilder().build(new StreamSource(new File("src/test/resources/OverlayImageTest/createOverlayImage_Gml2_Ok_mapNode.xml")));
        XdmNode mapNode = mapSource.children().iterator().next();

		XdmNode[] arguments = {limitNode, mapNode};
        OverlayImage overlayImage = new OverlayImage();
        XdmAtomicValue resultImage = (XdmAtomicValue) overlayImage.call(arguments);

        String base64String = resultImage.getUnderlyingValue().getStringValue();

        // Comparing the exact base64 string does not work in different java environments? (e.g. travis/gitlab)
        //String expectedResult = new String(Files.readAllBytes(new File("src/test/resources/OverlayImageTest/createOverlayImage_Gml2_Ok_expectedResult.txt").toPath()));
        //assertEquals(expectedResult, resultImage.getStringValue(), "Overlay image is not equal.");        

        int resultSize = base64String.length();
        
        assertTrue(resultSize > 50000, "Size of resulting image is too small.");        
    }
    
    @Test
    // If there is no georeferencing information of the map
    // we just create an empty (fully transparent) image.
    // (North arrow would be possible, though.)
    public void createEmptyOverlayImage_Ok() throws SaxonApiException, IOException {
		Processor processor = new Processor(false);
		
        XdmNode limitSource = processor.newDocumentBuilder().build(new StreamSource(new File("src/test/resources/OverlayImageTest/createEmptyOverlayImage_Ok_limitNode.xml")));
        XdmNode limitNode = limitSource.children().iterator().next();

        XdmNode mapSource = processor.newDocumentBuilder().build(new StreamSource(new File("src/test/resources/OverlayImageTest/createEmptyOverlayImage_Ok_mapNode.xml")));
        XdmNode mapNode = mapSource.children().iterator().next();

		XdmNode[] arguments = {limitNode, mapNode};
        OverlayImage overlayImage = new OverlayImage();
        XdmAtomicValue resultImage = (XdmAtomicValue) overlayImage.call(arguments);

        String expectedResult = new String(Files.readAllBytes(new File("src/test/resources/OverlayImageTest/createEmptyOverlayImage_Ok_expectedResult.txt").toPath()));

    	assertEquals(expectedResult, resultImage.getStringValue(), "Overlay image is not equal.");        
    }
}
