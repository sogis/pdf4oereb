package ch.so.agi.oereb.pdf4oereb.webservice.services;

import java.io.File;

import ch.so.agi.oereb.pdf4oereb.Locale;
import net.sf.saxon.s9api.SaxonApiException;

public interface ConvertService  {
    File convertXml2Pdf(String xmlFileName, String outputDirectory, Locale locale) throws SaxonApiException;
}