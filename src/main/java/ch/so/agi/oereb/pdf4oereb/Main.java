package ch.so.agi.oereb.pdf4oereb;

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
	
	@Option(names = {"-s", "--xsl"}, required = false, description = "The xslt style sheet.")
    private String xsltFileName;


	public static void main(String[] args) throws SaxonApiException {
        
        CommandLine.call(new Main(), args);

//        if (args.length < 2 ) {
//            log.error("Missing parameters");
//            return;
//        }

	}

	@Override
	public Void call() throws Exception {
        Logger log = LoggerFactory.getLogger(Main.class);

//        log.info("xml: " + args[0]);
//        log.info("output directory: " + args[1]);

//        String xmlFileName = args[0];
//        String outputDirectory = args[1];
        
        String xmlFileName = "aaa";
        String outputDirectory = "bbb";
        
        Converter converter =  new Converter();
        converter.run(xmlFileName, outputDirectory);
		return null;
	}
}
