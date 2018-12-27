package ch.so.agi.oereb.pdf4oereb.saxon.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.xml.transform.stream.StreamSource;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.tree.util.Orphan;
import net.sf.saxon.type.Type;

public class PlanForLandRegisterMainPageImageTest {
    Logger log = LoggerFactory.getLogger(PlanForLandRegisterMainPageImageTest.class);
       
    @Test
    public void createPlanForLandRegisterMainPageImage_embeddedImage_Ok() throws SaxonApiException, IOException {
		Processor processor = new Processor(false);

		XdmNode baseMapSource = processor.newDocumentBuilder().build(new StreamSource(new File("src/test/resources/PlanForLandRegisterMainPageImageTest/createPlanForLandRegisterMainPageImage_embeddedImage_Ok_baseMapNode.xml")));
        XdmNode baseMapNode = baseMapSource.children().iterator().next();
        
        // the overlay image is only a base64 string
        String overlayImageString = new String(Files.readAllBytes(new File("src/test/resources/PlanForLandRegisterMainPageImageTest/createPlanForLandRegisterMainPageImage_embeddedImage_Ok_overlayImageNode.xml").toPath()));
		Orphan node = new Orphan(processor.getUnderlyingConfiguration());
		node.setNodeKind(Type.TEXT);
		node.setStringValue(overlayImageString);
		XdmNode overlayImageNode = new XdmNode(node);

		XdmNode[] arguments = {baseMapNode, overlayImageNode};
		PlanForLandRegisterMainPageImage mainPageImage = new PlanForLandRegisterMainPageImage();
        XdmAtomicValue resultImage = (XdmAtomicValue) mainPageImage.call(arguments);

        String expectedResult = new String(Files.readAllBytes(new File("src/test/resources/PlanForLandRegisterMainPageImageTest/createPlanForLandRegisterMainPageImage_embeddedImage_Ok_expectedResult.txt").toPath()));

        assertEquals(expectedResult, resultImage.getStringValue(), "Main page image is not equal.");        
    }
    
    @Test
    @Tag("wms")
    public void createPlanForLandRegisterMainPageImage_wmsImage_Ok() throws SaxonApiException, IOException {
		Processor processor = new Processor(false);

    	XdmNode baseMapSource = processor.newDocumentBuilder().build(new StreamSource(new File("src/test/resources/PlanForLandRegisterMainPageImageTest/createPlanForLandRegisterMainPageImage_wmsImage_Ok_baseMapNode.xml")));
        XdmNode baseMapNode = baseMapSource.children().iterator().next();
        
        // the overlay image is only a base64 string
        String overlayImageString = new String(Files.readAllBytes(new File("src/test/resources/PlanForLandRegisterMainPageImageTest/createPlanForLandRegisterMainPageImage_wmsImage_Ok_overlayImageNode.xml").toPath()));
		Orphan node = new Orphan(processor.getUnderlyingConfiguration());
		node.setNodeKind(Type.TEXT);
		node.setStringValue(overlayImageString);
		XdmNode overlayImageNode = new XdmNode(node);
		
		XdmNode[] arguments = {baseMapNode, overlayImageNode};
		PlanForLandRegisterMainPageImage mainPageImage = new PlanForLandRegisterMainPageImage();
        XdmAtomicValue resultImage = (XdmAtomicValue) mainPageImage.call(arguments);

        String expectedResult = new String(Files.readAllBytes(new File("src/test/resources/PlanForLandRegisterMainPageImageTest/createPlanForLandRegisterMainPageImage_wmsImage_Ok_expectedResult.txt").toPath()));

        assertEquals(expectedResult, resultImage.getStringValue(), "Main page image is not equal.");        
    }
}
