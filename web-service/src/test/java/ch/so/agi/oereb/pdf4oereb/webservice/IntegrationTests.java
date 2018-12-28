package ch.so.agi.oereb.pdf4oereb.webservice;

import static io.restassured.RestAssured.given;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IntegrationTests {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @LocalServerPort
    int randomServerPort;
    
    @BeforeEach
    public void setPort() {
        RestAssured.port = randomServerPort;
    }
    
    @Test
    public void indexPageTest() {               
        given().
        when().
            get("/pdf4oereb/").
        then().
            statusCode(200).
            body("html.head.title", equalTo("pdf4oereb web service"));
    }
    
    @Test
    @ExtendWith(TempDirectory.class)    
    /*
     * Sending an empty file is like pushing the upload
     * button without selecting a file first.
     */
    public void emptyFileUploadTest(@TempDir Path tempDir) throws IOException {
        File file = Paths.get(tempDir.toString(), "tempFile.txt").toFile();
        file.createNewFile();

        given().
            multiPart("file", file).
        when().
            post("/pdf4oereb/").
        then().
            statusCode(302);
    }
    
    @Test
    /*
     * Upload a non-XML file.
     */
    public void nonXMLFileUploadTest() {
        File file = new File("src/test/data/2765.xml");

        given().
            multiPart("file", file).
        when().
            post("/pdf4oereb/").
        then().
            statusCode(400).
            body(containsString("org.xml.sax.SAXParseException"));        
    }   

    @Test
    /*
     * Upload a non-DATA-Extract-XML file that cannot be transformed.
     */     
    public void nonDataExtractXmlFileUploadTest() {
        File file = new File("src/test/data/nonsense.xml");

        given().
            multiPart("file", file).
        when().
            post("/pdf4oereb/").
        then().
            statusCode(400).
            body(containsString("Document is empty (something might be wrong with your XSLT stylesheet)"));
    }

    @Test
    /*
     * Upload a DATA-Extract file and a PDF will
     * be returned (= successful conversion).
     */
    public void successfulConversionTest() {
        File file = new File("src/test/data/CH567107399166_geometry_images.xml");
        
        given().
            multiPart("file", file).
        when().
            post("/pdf4oereb/").
        then().
            statusCode(200).
            contentType("application/pdf").
            //header("Content-Type", "application/pdf").
            header("content-disposition", "attachment; filename=CH567107399166_geometry_images.pdf");
    }
}