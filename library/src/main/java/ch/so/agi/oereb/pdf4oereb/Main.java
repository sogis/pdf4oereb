package ch.so.agi.oereb.pdf4oereb;

import java.io.File;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.s9api.SaxonApiException;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

class Main implements Callable<Void> {
	
	@Option(names = {"-i", "--xml"}, required = true, description = "The input xml file.")
    private String xmlFileName;

	@Option(names = {"-o", "--out"}, required = true, description = "The output directory.")
    private String outputDirectory;
	
	@Option(names = {"-s", "--xsl"}, required = false, description = "The xslt stylesheet.")
    private String xsltFileName;
	
	@Option(names = {"-f", "--fo"}, required = false, description = "Perfom XML->FO transformation only.")
    private boolean foTransformation = false;



	public static void main(String[] args) throws SaxonApiException {
		CommandLine.call(new Main(), args);
	}

	@Override
	public Void call() throws Exception {
        Logger log = LoggerFactory.getLogger(Main.class);
        
        log.info("file to transform: " + xmlFileName);
        log.info("output directory: " + outputDirectory);

        Converter converter =  new Converter();

        // xml->fo transformation only
        if (foTransformation) {
        	File file = null;
        	if (xsltFileName != null) {
        		log.info("custom xslt file will be used: " + xsltFileName);
        		file = converter.runXml2Fo(xmlFileName, xsltFileName, outputDirectory, Locale.FR);
        	} else {
        		file = converter.runXml2Fo(xmlFileName, outputDirectory, Locale.FR);
        	}
        	log.info("file written: " + file.getAbsolutePath());
        } 
        // xml->fo->pdf transformation
        else {
        	if (xsltFileName != null) {
        		log.info("custom xslt file will be used: " + xsltFileName);
            	converter.runXml2Pdf(xmlFileName, xsltFileName, outputDirectory, Locale.IT);
        	} else {
        		converter.runXml2Pdf(xmlFileName, outputDirectory, Locale.IT);
        	}
        }      
  		return null;
	}
}
