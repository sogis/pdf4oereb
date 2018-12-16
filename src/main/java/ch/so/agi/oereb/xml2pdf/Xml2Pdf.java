package ch.so.agi.oereb.xml2pdf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.fonts.FontSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import ch.so.agi.oereb.xml2pdf.saxon.ext.HighlightingImage;
import ch.so.agi.oereb.xml2pdf.saxon.ext.MergedImage;
import ch.so.agi.oereb.xml2pdf.saxon.ext.Test;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

public class Xml2Pdf {
    Logger log = LoggerFactory.getLogger(Xml2Pdf.class);
    
    private final String xlstFileName = "oereb_title_page.xslt";
    private final String fopxconfFileName = "fop.xconf";
    private static ArrayList<String> fonts = null;
    
    static {
        fonts = new ArrayList<String>();
        fonts.add("Cadastra.ttf");
        fonts.add("CadastraBd.ttf");
        fonts.add("CadastraBI.ttf");
        fonts.add("CadastraIt.ttf");
    }
    
    public void run(String xmlFileName, String outputDirectory) {
        try {
//            Path tempDir = Files.createTempDirectory(Paths.get(System.getProperty("java.io.tmpdir")), "oereb_extract_resources__");
            Path tempDir = Paths.get("/Users/stefan/tmp/");
            
            String baseFileName = FilenameUtils.getBaseName(xmlFileName);
            File foFile = new File(Paths.get(tempDir.toFile().getAbsolutePath(), baseFileName + ".fo").toFile().getAbsolutePath());
            File pdfFile = new File(Paths.get(tempDir.toFile().getAbsolutePath(), baseFileName + ".pdf").toFile().getAbsolutePath());
            log.info(foFile.getAbsolutePath());

            // copy xslt file from resources into temp directory
            File xsltFile = new File(Paths.get(tempDir.toFile().getAbsolutePath(), xlstFileName).toFile().getAbsolutePath());
            InputStream xsltFileInputStream = Xml2Pdf.class.getResourceAsStream("/"+xlstFileName); 
            Files.copy(xsltFileInputStream, xsltFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            xsltFileInputStream.close();
            log.info(xsltFile.getAbsolutePath());
            
            // copy fop.xconf file from resources into temp directory
            File fopxconfFile = new File(Paths.get(tempDir.toFile().getAbsolutePath(), fopxconfFileName).toFile().getAbsolutePath());
            InputStream fopxconfFileInputStream = Xml2Pdf.class.getResourceAsStream("/"+fopxconfFileName); 
            Files.copy(fopxconfFileInputStream, fopxconfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            fopxconfFileInputStream.close();
            log.info(fopxconfFile.getAbsolutePath());
            
            // copy fonts from resources into temp directory
            for (String fontName : fonts) {
                log.info(fontName);
                File fontFile = new File(Paths.get(tempDir.toFile().getAbsolutePath(), fontName).toFile().getAbsolutePath());
                InputStream is =  Xml2Pdf.class.getResourceAsStream("/"+fontName); 
                Files.copy(is, fontFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                is.close();
            }

            // create FO file
            // from examples in source code
            Processor proc = new Processor(false);
            
            ExtensionFunction highlightingImage = new HighlightingImage();
            proc.registerExtensionFunction(highlightingImage);
            ExtensionFunction mergedImage = new MergedImage();
            proc.registerExtensionFunction(mergedImage);
            
            XsltCompiler comp = proc.newXsltCompiler();
            XsltExecutable exp = comp.compile(new StreamSource(xsltFile));
            XdmNode source = proc.newDocumentBuilder().build(new StreamSource(new File(xmlFileName)));
            Serializer outFo = proc.newSerializer(foFile);
            XsltTransformer trans = exp.load();
            trans.setInitialContextNode(source);
            trans.setDestination(outFo);
            trans.transform();
                
            // create PDF
            // https://xmlgraphics.apache.org/fop/2.3/embedding.html
            FopFactory fopFactory = FopFactory.newInstance(fopxconfFile);
            OutputStream outPdf = new BufferedOutputStream(new FileOutputStream(pdfFile));        
            try {
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, outPdf);
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer(); 
                Source src = new StreamSource(foFile);
                Result res = new SAXResult(fop.getDefaultHandler());
                transformer.transform(src, res);
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            } catch (TransformerException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            } finally {
                log.info(pdfFile.getAbsolutePath());
                outPdf.close();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (SaxonApiException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (SAXException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
