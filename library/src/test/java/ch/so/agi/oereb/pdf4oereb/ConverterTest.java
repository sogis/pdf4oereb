package ch.so.agi.oereb.pdf4oereb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

public class ConverterTest {
    @Test 
    @ExtendWith(TempDirectory.class)
    public void convertXml2Pdf_InputFileNotFound(@TempDir Path tempDir) {
    	Converter converter = new Converter();
    	try {
			converter.runXml2Pdf("fubar.xml", tempDir.toAbsolutePath().toString(), Locale.DE);
		} catch (ConverterException e) {
			assertTrue(e.getMessage().contains("fubar.xml (No such file or directory)"));
		}
    }
    
    @Test
    @ExtendWith(TempDirectory.class)
    public void convertXml2_CantonBl_Ok(@TempDir Path tempDir) throws ConverterException, IOException {
    	Converter converter = new Converter();
    	File resultFile = converter.runXml2Pdf("src/test/data/bl/CH567107399166_geometry_images.xml", tempDir.toAbsolutePath().toString(), Locale.DE);
//    	File resultFile = converter.runXml2Pdf("src/test/data/bl/CH567107399166_geometry_images.xml", "/Users/stefan/tmp/", Locale.DE);
        byte[] resultFileContent = Files.readAllBytes(resultFile.toPath());
        
        //File pdfFile = new File("src/test/data/bl/CH567107399166_geometry_images.pdf");
        //byte[] pdfFileContent = Files.readAllBytes(pdfFile.toPath());
    	//assertEquals(pdfFileContent.length, resultFileContent.length, "File content is not equal.");
        
        int resultSize = resultFileContent.length;

        assertTrue(resultSize > 550000, "Size of resulting pdf is too small.");        
    }
    
    @Test
    @ExtendWith(TempDirectory.class)
    public void convertXml2_CantonBl_NoNotConcernedThemes_Ok(@TempDir Path tempDir) throws ConverterException, IOException {
        Converter converter = new Converter();
        File resultFile = converter.runXml2Pdf("src/test/data/bl/CH567107399166_no_not_concerned_themes.xml", tempDir.toAbsolutePath().toString(), Locale.DE);
//        File resultFile = converter.runXml2Pdf("src/test/data/bl/CH567107399166_no_not_concerned_themes.xml", "/Users/stefan/tmp/", Locale.DE);
        byte[] resultFileContent = Files.readAllBytes(resultFile.toPath());
                
        int resultSize = resultFileContent.length;

        assertTrue(resultSize > 550000, "Size of resulting pdf is too small.");        
    }
    
    @Test
    @ExtendWith(TempDirectory.class)
    public void convertXml2_CantonBl_NoConcernedThemes_Ok(@TempDir Path tempDir) throws ConverterException, IOException {
        Converter converter = new Converter();
        File resultFile = converter.runXml2Pdf("src/test/data/bl/CH567107399166_no_not_concerned_themes.xml", tempDir.toAbsolutePath().toString(), Locale.DE);
//        File resultFile = converter.runXml2Pdf("src/test/data/bl/CH567107399166_no_concerned_themes.xml", "/Users/stefan/tmp/", Locale.DE);
        byte[] resultFileContent = Files.readAllBytes(resultFile.toPath());
                
        int resultSize = resultFileContent.length;

        assertTrue(resultSize > 310000, "Size of resulting pdf is too small.");        
    }    
    
    //@Test
    @Tag("wms")
    @ExtendWith(TempDirectory.class)
    public void convertXml1_CantonSo_Ok(@TempDir Path tempDir) throws ConverterException, IOException {
        Converter converter = new Converter();
        File resultFile = converter.runXml2Pdf("src/test/data/so/CH955832730623_geometry_wms.xml", tempDir.toAbsolutePath().toString(), Locale.DE);
        byte[] resultFileContent = Files.readAllBytes(resultFile.toPath());
        
        // We cannot compare the file as we would do with embedded images since
        // the content of the wms getmap request can change from time to time.
        int resultSize = resultFileContent.length;
        
        assertTrue(resultSize > 300000, "Size of resulting pdf is too small.");        
    }
}
