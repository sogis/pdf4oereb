package ch.so.agi.oereb.xml2pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Xml2Pdf {
    Logger log = LoggerFactory.getLogger(Xml2Pdf.class);
    
    private final String xlstFileName = "oereb_title_page.xslt";
    
    public void run(String xmlFileName, String outputDirectory) {
        try {
            Path tempDir = Files.createTempDirectory("oereb_xml2pdf_");
            
            // Copy xslt file from resources and copy it in the temp directory.
            File xsltFile = new File(Paths.get(tempDir.toFile().getAbsolutePath(), xlstFileName).toFile().getAbsolutePath());
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream xsltFileInputStream = Xml2Pdf.class.getResourceAsStream("/"+xlstFileName); 
            Files.copy(xsltFileInputStream, xsltFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            xsltFileInputStream.close();
            log.info(xsltFile.getAbsolutePath());
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
