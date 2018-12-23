package ch.so.agi.oereb.pdf4oereb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.s9api.SaxonApiException;

public class Main {

	public static void main(String[] args) throws SaxonApiException {
        Logger log = LoggerFactory.getLogger(Main.class);

        if (args.length < 2 ) {
            log.error("Missing parameters");
            return;
        }

        log.info("xml: " + args[0]);
        log.info("output directory: " + args[1]);

        String xmlFileName = args[0];
        String outputDirectory = args[1];
        
        Pdf4Oereb converter =  new Pdf4Oereb();
        converter.run(xmlFileName, outputDirectory);
	}
}
