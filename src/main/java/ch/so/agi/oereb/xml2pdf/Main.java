package ch.so.agi.oereb.xml2pdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(Main.class);

        if (args.length < 2 ) {
            log.error("Missing parameters");
            return;
        }

        log.info(args[0]);
        log.info(args[1]);

        String xmlFileName = args[0];
        String outputDirectory = args[1];
        
        Xml2Pdf xml2pdf =  new Xml2Pdf();
        xml2pdf.run(xmlFileName, outputDirectory);

        

//        DM01Converter dm01Converter = new DM01Converter();
//        try {
//            dm01Converter.convert(inputFileName, outputPath, language);
//            log.info("File written to: " + outputPath);
//        } catch (IllegalArgumentException | IoxException | Ili2cException e) {
//            e.printStackTrace();
//        }
    }

}
