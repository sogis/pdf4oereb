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
	
	@Option(names = {"-s", "--xsl"}, required = false, description = "The xslt style sheet. (not yet implemented)")
    private String xsltFileName;


	public static void main(String[] args) throws SaxonApiException {
        CommandLine.call(new Main(), args);

	}

	@Override
	public Void call() throws Exception {
        Logger log = LoggerFactory.getLogger(Main.class);

        Converter converter =  new Converter();
        converter.run(xmlFileName, outputDirectory);
        
//        String[] arguments = {"-v", "-fo", "/Users/stefan/tmp/CH567107399166_test.fo", "-pdf", "/Users/stefan/tmp/CH567107399166_test.pdf"};
//        org.apache.fop.cli.Main.startFOP(arguments);
		return null;
	}
}
