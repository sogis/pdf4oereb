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
    
    @Option(names = {"-m", "--html"}, required = false, description = "Perfom XML->HTML transformation.")
    private boolean htmlTransformation = false;

	public static void main(String[] args) throws Exception {
		CommandLine.call(new Main(), args);
	}

	@Override
	public Void call() throws Exception {
        Logger log = LoggerFactory.getLogger(Main.class);
        
        log.info("file to transform: " + xmlFileName);
        log.info("output directory: " + outputDirectory);

        Converter converter =  new Converter();
        File file = null;
        // xml->html transformation
        if (htmlTransformation) {
            if (xsltFileName != null) {
                log.info("custom xslt file will be used: " + xsltFileName);                
                file = converter.runXml2Html(xmlFileName, xsltFileName, outputDirectory, Locale.DE);
            } else {
                file = converter.runXml2Html(xmlFileName, outputDirectory, Locale.DE);
            }
        } 
        // xml->fo transformation only
        else if (foTransformation) {
        	if (xsltFileName != null) {
        		log.info("custom xslt file will be used: " + xsltFileName);
        		file = converter.runXml2Fo(xmlFileName, xsltFileName, outputDirectory, Locale.DE);
        	} else {
        		file = converter.runXml2Fo(xmlFileName, outputDirectory, Locale.DE);
        	}
        } 
        // xml->fo->pdf transformation
        else {
        	if (xsltFileName != null) {
        		log.info("custom xslt file will be used: " + xsltFileName);
        		file = converter.runXml2Pdf(xmlFileName, xsltFileName, outputDirectory, Locale.DE);
        	} else {
        	    file = converter.runXml2Pdf(xmlFileName, outputDirectory, Locale.DE);
        	}
        }     
        log.info("file written: " + file.getAbsolutePath());
  		return null;
	}
}
