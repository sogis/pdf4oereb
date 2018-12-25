package ch.so.agi.oereb.pdf4oereb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

import net.sf.saxon.s9api.SaxonApiException;

public class ConverterTest {
    @Test 
    @ExtendWith(TempDirectory.class)
    public void convertXml2Pdf_InputFileNotFound(@TempDir Path tempDir) {
    	Converter converter = new Converter();
    	try {
			converter.runXml2Pdf("fubar.xml", tempDir.toAbsolutePath().toString());
		} catch (SaxonApiException e) {
			assertTrue(e.getMessage().contains("fubar.xml (No such file or directory)"));
		}
    }
    
    @Test
    @ExtendWith(TempDirectory.class)
    public void convertXml2_CantonBl_Ok(@TempDir Path tempDir) throws SaxonApiException, IOException {
    	Converter converter = new Converter();
    	File resultFile = converter.runXml2Pdf("src/test/data/bl/CH567107399166_geometry_images.xml", tempDir.toAbsolutePath().toString());
//    	File resultFile = converter.runXml2Pdf("src/test/data/bl/CH567107399166_geometry_images.xml", "/Users/stefan/tmp/");
        byte[] resultFileContent = Files.readAllBytes(resultFile.toPath());
        
        File pdfFile = new File("src/test/data/bl/CH567107399166_geometry_images.pdf");
        byte[] pdfFileContent = Files.readAllBytes(pdfFile.toPath());

    	assertEquals(pdfFileContent.length, resultFileContent.length, "File content is not equal.");        
    }
}
