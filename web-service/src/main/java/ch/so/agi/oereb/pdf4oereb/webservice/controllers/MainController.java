package ch.so.agi.oereb.pdf4oereb.webservice.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import ch.so.agi.oereb.pdf4oereb.Locale;
import ch.so.agi.oereb.pdf4oereb.webservice.services.ConvertService;

@Controller
public class MainController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ServletContext servletContext;
    
    @Autowired
    private ConvertService convertService;
    
    // Folder prefix
    private static String FOLDER_PREFIX = "pdf4oereb_";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> uploadFile(
            @RequestParam(name="file", required=true) MultipartFile uploadFile,
            @RequestParam(name="locale", required=false) String localeString) {	        
        // Need to use FilenameUtils.getName() since getOriginalFilename() returns absolute path for files sent with IE (on macOS only?)
        String filename = FilenameUtils.getName(uploadFile.getOriginalFilename());

        // If the upload button was pushed w/o choosing a file,
        // we just redirect to the starting page.
        if (uploadFile.getSize() == 0 
                || filename.trim().equalsIgnoreCase("")
                || filename == null) {
            log.warn("No file was uploaded. Redirecting to starting page.");
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", servletContext.getContextPath());    
            return new ResponseEntity<String>(headers, HttpStatus.FOUND);           
        }
        
        // Save uploaded file locally.
        String directory = System.getProperty("java.io.tmpdir");

        try {
            Path tmpDirectory = Files.createTempDirectory(Paths.get(directory), FOLDER_PREFIX);         
            Path uploadFilePath = Paths.get(tmpDirectory.toString(), filename);

            // Save the file locally.           
            byte[] bytes = uploadFile.getBytes();
            Files.write(uploadFilePath, bytes);
            log.debug("uploadFilePath: " + uploadFilePath);
            
            // Grab desired language of pdf output.
            Locale locale;
            if (localeString == null || localeString.equalsIgnoreCase("")) {
                locale = Locale.DE;
            } else if (localeString.equalsIgnoreCase("FR")) {
                locale = Locale.valueOf("FR");
            } else {
                locale = Locale.DE;
            }
            
            // Start the XML->PDF transformation.
            String xmlFileName = uploadFilePath.toString();
            File pdfFile = convertService.convertXml2Pdf(xmlFileName, tmpDirectory.toFile().getAbsolutePath(), locale);
            
            // Send pdf file back to client.
            InputStream is = new FileInputStream(pdfFile);
            return ResponseEntity
                    .ok().header("content-disposition", "attachment; filename=" + pdfFile.getName())
                    .contentLength(pdfFile.length())
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .body(new InputStreamResource(is));
            
        } catch (Exception e) { // TODO: See other spring boot apps for better exception handling if needed.
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.parseMediaType("text/plain"))
                    .body(e.getMessage());
        }
    }
}