package ch.so.agi.oereb.pdf4oereb.saxon.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.xml.transform.stream.StreamSource;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.tree.util.Orphan;
import net.sf.saxon.type.Type;

public class RestrictionOnLandownershipImageTest {
    Logger log = LoggerFactory.getLogger(RestrictionOnLandownershipImageTest.class);
    
    @Test
    public void createRestrictionOnLandownershipImage_Ok() throws SaxonApiException, IOException {
		Processor processor = new Processor(false);

		XdmNode rolMapSource = processor.newDocumentBuilder().build(new StreamSource(new File("src/test/resources/RestrictionOnLandownershipImageTest/createRestrictionOnLandownershipImage_Ok_restrictionOnLandownershipMapNode.xml")));
        XdmNode rolMapNode = rolMapSource.children().iterator().next();
        
        String baseImageString = new String(Files.readAllBytes(new File("src/test/resources/RestrictionOnLandownershipImageTest/createRestrictionOnLandownershipImage_Ok_baseImageNode.xml").toPath()));
		Orphan baseImageOrphanNode = new Orphan(processor.getUnderlyingConfiguration());
		baseImageOrphanNode.setNodeKind(Type.TEXT);
		baseImageOrphanNode.setStringValue(baseImageString);
		XdmNode baseImageNode = new XdmNode(baseImageOrphanNode);

        String overlayImageString = new String(Files.readAllBytes(new File("src/test/resources/RestrictionOnLandownershipImageTest/createRestrictionOnLandownershipImage_Ok_overlayImageNode.xml").toPath()));
		Orphan overlayImageOrphanNode = new Orphan(processor.getUnderlyingConfiguration());
		overlayImageOrphanNode.setNodeKind(Type.TEXT);
		overlayImageOrphanNode.setStringValue(overlayImageString);
		XdmNode overlayImageNode = new XdmNode(overlayImageOrphanNode);
		
		XdmNode[] arguments = {rolMapNode, baseImageNode, overlayImageNode};
		RestrictionOnLandownershipImage restrictionOnLandownershipImage = new RestrictionOnLandownershipImage();
        XdmAtomicValue resultImage = (XdmAtomicValue) restrictionOnLandownershipImage.call(arguments);

        String expectedResult = new String(Files.readAllBytes(new File("src/test/resources/RestrictionOnLandownershipImageTest/createRestrictionOnLandownershipImage_Ok_expectedResult.txt").toPath()));

        assertEquals(expectedResult, resultImage.getStringValue(), "RestrictonOnLandownership image is not equal.");        
    }

}
