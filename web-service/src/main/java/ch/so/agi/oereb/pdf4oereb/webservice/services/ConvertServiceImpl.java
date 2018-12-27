package ch.so.agi.oereb.pdf4oereb.webservice.services;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.so.agi.oereb.pdf4oereb.Converter;
import ch.so.agi.oereb.pdf4oereb.Locale;
import net.sf.saxon.s9api.SaxonApiException;

@Service
public class ConvertServiceImpl implements ConvertService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public File convertXml2Pdf(String xmlFileName, String outputDirectory, Locale locale) throws SaxonApiException {
        log.info(xmlFileName);
        log.info(locale.toString());
        Converter converter =  new Converter();
        File pdfFile = converter.runXml2Pdf(xmlFileName, outputDirectory, locale);
        
		return pdfFile;
	}

}