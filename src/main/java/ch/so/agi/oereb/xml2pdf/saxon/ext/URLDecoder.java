package ch.so.agi.oereb.xml2pdf.saxon.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.OccurrenceIndicator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmValue;

public class URLDecoder implements ExtensionFunction {
    Logger log = LoggerFactory.getLogger(URLDecoder.class);

	@Override
	public QName getName() {
        return new QName("http://oereb.agi.so.ch", "decodeURL");
	}

	@Override
	public SequenceType getResultType() {
        return SequenceType.makeSequenceType(ItemType.ANY_URI, OccurrenceIndicator.ONE);
	}

	@Override
	public SequenceType[] getArgumentTypes() {
        return new SequenceType[] { SequenceType.makeSequenceType(ItemType.ANY_ITEM, OccurrenceIndicator.ONE)};
	}

	@Override
	public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
		// TODO Auto-generated method stub
		return null;
	}

}
