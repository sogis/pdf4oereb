package ch.so.agi.oereb.pdf4oereb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FilenameUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.xalan.transformer.TransformerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import ch.so.agi.oereb.pdf4oereb.saxon.ext.FixImage;
import ch.so.agi.oereb.pdf4oereb.saxon.ext.OverlayImage;
import ch.so.agi.oereb.pdf4oereb.saxon.ext.PlanForLandRegisterMainPageImage;
import ch.so.agi.oereb.pdf4oereb.saxon.ext.RestrictionOnLandownershipImage;
import ch.so.agi.oereb.pdf4oereb.saxon.ext.URLDecoder;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.Xslt30Transformer;
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

    public File run(String xmlFileName, String xsltFileName, String outputDirectory) throws SaxonApiException {
    	log.info(xmlFileName);
    	log.info(xsltFileName);
    	log.info(outputDirectory);
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

        	// create FO file
        	// from examples in source code
        	log.info("start saxon: " + new Date().toString());
        	TransformerFactory factory = TransformerFactory.newInstance();
            TransformerFactoryImpl tFactoryImpl = (TransformerFactoryImpl) factory;
            net.sf.saxon.Configuration saxonConfig = tFactoryImpl.getConfiguration();
            Processor proc = (Processor) saxonConfig.getProcessor();

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

            Source xslt = new StreamSource(new File(xsltFileName));
            Transformer transformer = factory.newTransformer(xslt);
            log.info(transformer.getClass().toString());
            transformer.reset();

            Source text = new StreamSource(new File(xmlFileName));
//            transformer.transform(text, new StreamResult(new File("result.xml")));

//        	Processor proc = new Processor(false);
//
//        	ExtensionFunction highlightingImage = new OverlayImage();
//        	proc.registerExtensionFunction(highlightingImage);
//        	ExtensionFunction mergeImage = new PlanForLandRegisterMainPageImage();
//        	proc.registerExtensionFunction(mergeImage);
//        	ExtensionFunction rolImage = new RestrictionOnLandownershipImage();
//        	proc.registerExtensionFunction(rolImage);
//        	ExtensionFunction fixImage = new FixImage();
//        	proc.registerExtensionFunction(fixImage);
//        	ExtensionFunction decodeUrl = new URLDecoder();
//        	proc.registerExtensionFunction(decodeUrl);
//
//        	XsltCompiler comp = proc.newXsltCompiler();
//        	XsltExecutable exp = comp.compile(new StreamSource(new File(xsltFileName)));
//        	XdmNode source = proc.newDocumentBuilder().build(new StreamSource(new File(xmlFileName)));
//        	Serializer outFo = proc.newSerializer(foFile);
//        	XsltTransformer trans = exp.load();
//        	trans.setInitialContextNode(source);
//        	trans.setDestination(outFo);
//        	trans.transform();
        	
//            XsltCompiler compiler = proc.newXsltCompiler();
//            XsltExecutable stylesheet = compiler.compile(new StreamSource(new File(xsltFileName)));
//            Serializer out = proc.newSerializer(foFile);
//            Xslt30Transformer trans = stylesheet.load30();
//            trans.transform(new StreamSource(new File(xmlFileName)), out);
//            log.info(trans.getClass().toString());
        	log.info("end saxon: " + new Date().toString());

        	// create PDF
        	// https://xmlgraphics.apache.org/fop/2.3/embedding.html
        	log.info("start fop: " + new Date().toString());
        	FopFactory fopFactory = FopFactory.newInstance(fopxconfFile);
        	OutputStream outPdf = new BufferedOutputStream(new FileOutputStream(pdfFile)); 
        	try {
        		Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, outPdf);
//        		TransformerFactory factory2 = TransformerFactory.newInstance();
//        		TransformerFactory factory = TransformerFactory.newInstance("org.apache.xalan.processor.TransformerFactoryImpl", null);
        		log.info(factory.getClass().toString());

//        		Transformer transformer2 =  factory.newTransformer(); 
//        		log.info(transformer2.getClass().toString());
//        		Source src = new StreamSource(foFile);
//        		Source src = new StreamSource("/Users/stefan/tmp/CH567107399166_test.fo");
        		Source src = new StreamSource(new File(xmlFileName));
        		Result res = new SAXResult(fop.getDefaultHandler());
        		
        		transformer.transform(src, res);
        		
        	} catch (TransformerConfigurationException e) {
        		e.printStackTrace();
        		log.error(e.getMessage());
        	} catch (TransformerException e) {
        		log.error(e.getMessage());
        		e.printStackTrace();
        	} finally {
        		log.info("pdf file: " + pdfFile.getAbsolutePath());
        		outPdf.close();
        	}
                    
          log.info("end fop: " + new Date().toString());
          return pdfFile;
        } catch (IOException e) {
        	log.error(e.getMessage());
        	e.printStackTrace();
        	throw new SaxonApiException(e.getMessage());
        } 
        catch (SAXException e) {
        	log.error(e.getMessage());
        	e.printStackTrace();
        	throw new SaxonApiException(e.getMessage());
        }
        catch (Exception e) {
        	log.error(e.getMessage());
        	e.printStackTrace();
        	throw new SaxonApiException(e.getMessage());
        }
    }
    
    public File run(String xmlFileName, String outputDirectory) throws SaxonApiException {
        try {
        	Path outputPath = Paths.get(outputDirectory);
            
            // copy xslt file from resources into temporary directory
            File xsltFile = new File(Paths.get(outputPath.toFile().getAbsolutePath(), xsltFileName).toFile().getAbsolutePath());
            InputStream xsltFileInputStream = Converter.class.getResourceAsStream("/"+xsltFileName); 
            Files.copy(xsltFileInputStream, xsltFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            xsltFileInputStream.close();
            
            File pdfFile = this.run(xmlFileName, xsltFile.getAbsolutePath(), outputDirectory);
            return pdfFile;
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new SaxonApiException(e.getMessage());
        } 
    }
}
