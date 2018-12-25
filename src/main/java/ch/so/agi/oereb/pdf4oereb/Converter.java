package ch.so.agi.oereb.pdf4oereb;

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
import java.util.Date;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FilenameUtils;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.so.agi.oereb.pdf4oereb.saxon.ext.FixImage;
import ch.so.agi.oereb.pdf4oereb.saxon.ext.OverlayImage;
import ch.so.agi.oereb.pdf4oereb.saxon.ext.PlanForLandRegisterMainPageImage;
import ch.so.agi.oereb.pdf4oereb.saxon.ext.RestrictionOnLandownershipImage;
import ch.so.agi.oereb.pdf4oereb.saxon.ext.URLDecoder;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SAXDestination;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

public class Converter {
    Logger log = LoggerFactory.getLogger(Converter.class);
    
    private final String xsltFileName = "oereb_extract.xslt";
    private final String fopxconfFileName = "fop.xconf";
    private static ArrayList<String> fonts = null;
    
    static {
        fonts = new ArrayList<String>();
        fonts.add("Cadastra.ttf");
        fonts.add("CadastraBd.ttf");
        fonts.add("CadastraBI.ttf");
        fonts.add("CadastraIt.ttf");
    }
    
    public File runXml2Fo(String xmlFileName, String xsltFileName, String outputDirectory) throws SaxonApiException {
        try {
        	Path outputPath = Paths.get(outputDirectory);
          
        	String baseFileName = FilenameUtils.getBaseName(xmlFileName);
        	File foFile = new File(Paths.get(outputPath.toFile().getAbsolutePath(), baseFileName + ".fo").toFile().getAbsolutePath());

        	log.info("start saxon: " + new Date().toString());
        	Processor proc = new Processor(false);

        	ExtensionFunction highlightingImage = new OverlayImage();
        	proc.registerExtensionFunction(highlightingImage);
        	ExtensionFunction mergeImage = new PlanForLandRegisterMainPageImage();
        	proc.registerExtensionFunction(mergeImage);
        	ExtensionFunction rolImage = new RestrictionOnLandownershipImage();
        	proc.registerExtensionFunction(rolImage);
        	ExtensionFunction fixImage = new FixImage();
        	proc.registerExtensionFunction(fixImage);
        	ExtensionFunction decodeUrl = new URLDecoder();
        	proc.registerExtensionFunction(decodeUrl);

        	XsltCompiler comp = proc.newXsltCompiler();
        	XsltExecutable exp = comp.compile(new StreamSource(new File(xsltFileName)));
        	XdmNode source = proc.newDocumentBuilder().build(new StreamSource(new File(xmlFileName)));
        	Serializer outFo = proc.newSerializer(foFile);
        	XsltTransformer trans = exp.load();
        	trans.setInitialContextNode(source);
        	trans.setDestination(outFo);
        	trans.transform();
        	log.info("end saxon: " + new Date().toString());
        	
        	return foFile;
        } catch (Exception e) {
        	log.error(e.getMessage());
        	e.printStackTrace();
        	throw new SaxonApiException(e.getMessage());
        }
    }
    
    public File runXml2Fo(String xmlFileName, String outputDirectory) throws SaxonApiException {
        try {
        	Path outputPath = Paths.get(outputDirectory);
            
            // copy xslt file from resources into temporary directory
            File xsltFile = new File(Paths.get(outputPath.toFile().getAbsolutePath(), xsltFileName).toFile().getAbsolutePath());
            InputStream xsltFileInputStream = Converter.class.getResourceAsStream("/"+xsltFileName); 
            Files.copy(xsltFileInputStream, xsltFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            xsltFileInputStream.close();
            
            File foFile = this.runXml2Fo(xmlFileName, xsltFile.getAbsolutePath(), outputDirectory);
            return foFile;
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new SaxonApiException(e.getMessage());
        } 
    }

    public File runXml2Pdf(String xmlFileName, String xsltFileName, String outputDirectory) throws SaxonApiException {
        try {
        	Path outputPath = Paths.get(outputDirectory);
          
        	String baseFileName = FilenameUtils.getBaseName(xmlFileName);
        	File foFile = new File(Paths.get(outputPath.toFile().getAbsolutePath(), baseFileName + ".fo").toFile().getAbsolutePath());
        	File pdfFile = new File(Paths.get(outputPath.toFile().getAbsolutePath(), baseFileName + ".pdf").toFile().getAbsolutePath());

        	// copy fop.xconf file from resources into temporary directory
        	File fopxconfFile = new File(Paths.get(outputPath.toFile().getAbsolutePath(), fopxconfFileName).toFile().getAbsolutePath());
        	InputStream fopxconfFileInputStream = Converter.class.getResourceAsStream("/"+fopxconfFileName); 
        	Files.copy(fopxconfFileInputStream, fopxconfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        	fopxconfFileInputStream.close();

        	// copy fonts from resources into temporary directory
        	for (String fontName : fonts) {
        		File fontFile = new File(Paths.get(outputPath.toFile().getAbsolutePath(), fontName).toFile().getAbsolutePath());
        		InputStream is =  Converter.class.getResourceAsStream("/"+fontName); 
        		Files.copy(is, fontFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        		is.close();
        	}
        	
        	// for a better performance (still a mystery) we combine the two transformation steps
        	// https://stackoverflow.com/questions/53918638/slow-apache-fop-transformation-after-saxon-xslt-transformation
        	log.info("start transformation: " + new Date().toString());
        	// the saxon part
        	Processor proc = new Processor(false);

        	ExtensionFunction highlightingImage = new OverlayImage();
        	proc.registerExtensionFunction(highlightingImage);
        	ExtensionFunction mergeImage = new PlanForLandRegisterMainPageImage();
        	proc.registerExtensionFunction(mergeImage);
        	ExtensionFunction rolImage = new RestrictionOnLandownershipImage();
        	proc.registerExtensionFunction(rolImage);
        	ExtensionFunction fixImage = new FixImage();
        	proc.registerExtensionFunction(fixImage);
        	ExtensionFunction decodeUrl = new URLDecoder();
        	proc.registerExtensionFunction(decodeUrl);

        	XsltCompiler comp = proc.newXsltCompiler();
        	XsltExecutable exp = comp.compile(new StreamSource(new File(xsltFileName)));
        	XdmNode source = proc.newDocumentBuilder().build(new StreamSource(new File(xmlFileName)));
        	Serializer outFo = proc.newSerializer(foFile);
        	XsltTransformer trans = exp.load();
        	trans.setInitialContextNode(source);
        	
        	// the fop part
            FopFactory fopFactory = FopFactory.newInstance(fopxconfFile);

            OutputStream outPdf = new BufferedOutputStream(new FileOutputStream(pdfFile)); 
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, outPdf);

            trans.setDestination(new SAXDestination(fop.getDefaultHandler()));
            trans.transform();
            outPdf.close();
        	log.info("end transformation: " + new Date().toString());

        	return pdfFile;
        } catch (Exception e) {
        	log.error(e.getMessage());
        	e.printStackTrace();
        	throw new SaxonApiException(e.getMessage());
        }
    }
    
    public File runXml2Pdf(String xmlFileName, String outputDirectory) throws SaxonApiException {
        try {
        	Path outputPath = Paths.get(outputDirectory);
            
            // copy xslt file from resources into temporary directory
            File xsltFile = new File(Paths.get(outputPath.toFile().getAbsolutePath(), xsltFileName).toFile().getAbsolutePath());
            InputStream xsltFileInputStream = Converter.class.getResourceAsStream("/"+xsltFileName); 
            Files.copy(xsltFileInputStream, xsltFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            xsltFileInputStream.close();
            
            File pdfFile = this.runXml2Pdf(xmlFileName, xsltFile.getAbsolutePath(), outputDirectory);
            return pdfFile;
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new SaxonApiException(e.getMessage());
        } 
    }
}
