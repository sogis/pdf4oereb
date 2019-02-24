package ch.so.agi.oereb.pdf4oereb;

import java.io.File;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    static Logger log = LoggerFactory.getLogger(Main.class);

    private static String xmlFileName;
    private static String outputDirectory;
    private static String xsltFileName;
    private static boolean foTransformation = false;
    private static String language = "de";
    private static Locale locale;
    
    static public void main(String args[]) {
        int argi = 0;
        for(;argi<args.length;argi++) {
            String arg = args[argi];
            
            if(arg.equals("--xml")) {
                argi++;
                xmlFileName = args[argi];
            } else if (arg.equals("--out")) {
                argi++;
                outputDirectory = args[argi];
            } else if (arg.equals("--xsl")) {
                argi++;
                xsltFileName = args[argi];
            } else if (arg.equals("--fo")) {
                foTransformation = true;
            } else if (arg.equals("--lang")) {
                argi++;
                language = args[argi];
                locale = Locale.valueOf(language.toUpperCase());
            } else if (arg.equals("--help")) {
                System.err.println();
                System.err.println("--xml       The input xml file (required).");
                System.err.println("--out       The output directory (required).");
                System.err.println("--xsl       The custom xsl stylesheet (optional).");
                System.err.println("--fo        Perfom XML->FO transformation only (optional).");
                System.err.println("--lang      Used language in pdf file (default = german).");
                System.err.println();
                return;
            }
        }
        
        if (xmlFileName == null) {
            log.error("Input xml file is required.");
            System.exit(2);
        }
        
        if (outputDirectory == null) {
            log.error("Output directory is required.");
            System.exit(2);
        }
        
        Converter converter =  new Converter();
        File file = null;
        
        try {
            if (foTransformation) {
                if (xsltFileName != null) {
                    file = converter.runXml2Fo(xmlFileName, xsltFileName, outputDirectory, locale);
                } else {
                    file = converter.runXml2Fo(xmlFileName, outputDirectory, locale);
                }
            } else {
                if (xsltFileName != null) {
                  file = converter.runXml2Pdf(xmlFileName, xsltFileName, outputDirectory, locale);
              } else {
                  file = converter.runXml2Pdf(xmlFileName, outputDirectory, locale);
              }
            }  
            log.info("File written: " + file.getAbsolutePath());
            System.exit(0);
        } catch (Exception e) {
            log.error(e.getMessage());
            System.exit(3);
        }  
    }
}




//class Main implements Callable<Void> {
//	
//	@Option(names = {"-i", "--xml"}, required = true, description = "The input xml file.")
//    private String xmlFileName;
//
//	@Option(names = {"-o", "--out"}, required = true, description = "The output directory.")
//    private String outputDirectory;
//	
//	@Option(names = {"-s", "--xsl"}, required = false, description = "The xslt stylesheet.")
//    private String xsltFileName;
//	
//    @Option(names = {"-f", "--fo"}, required = false, description = "Perfom XML->FO transformation only.")
//    private boolean foTransformation = false;
//    
//    @Option(names = {"-m", "--html"}, required = false, description = "Perfom XML->HTML transformation.")
//    private boolean htmlTransformation = false;
//
//	public static void main(String[] args) throws Exception {
//		CommandLine.call(new Main(), args);
//	}
//
//	@Override
//	public Void call() throws Exception {
//        Logger log = LoggerFactory.getLogger(Main.class);
//        
//        log.info("file to transform: " + xmlFileName);
//        log.info("output directory: " + outputDirectory);
//
//        Converter converter =  new Converter();
//        File file = null;
//        // xml->html transformation
//        if (htmlTransformation) {
//            if (xsltFileName != null) {
//                log.info("custom xslt file will be used: " + xsltFileName);                
//                file = converter.runXml2Html(xmlFileName, xsltFileName, outputDirectory, Locale.DE);
//            } else {
//                file = converter.runXml2Html(xmlFileName, outputDirectory, Locale.DE);
//            }
//        } 
//        // xml->fo transformation only
//        else if (foTransformation) {
//        	if (xsltFileName != null) {
//        		log.info("custom xslt file will be used: " + xsltFileName);
//        		file = converter.runXml2Fo(xmlFileName, xsltFileName, outputDirectory, Locale.DE);
//        	} else {
//        		file = converter.runXml2Fo(xmlFileName, outputDirectory, Locale.DE);
//        	}
//        } 
//        // xml->fo->pdf transformation
//        else {
//        	if (xsltFileName != null) {
//        		log.info("custom xslt file will be used: " + xsltFileName);
//        		file = converter.runXml2Pdf(xmlFileName, xsltFileName, outputDirectory, Locale.DE);
//        	} else {
//        	    file = converter.runXml2Pdf(xmlFileName, outputDirectory, Locale.DE);
//        	}
//        }     
//        log.info("file written: " + file.getAbsolutePath());
//  		return null;
//	}
//}
