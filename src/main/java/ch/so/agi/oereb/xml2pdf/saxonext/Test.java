package ch.so.agi.oereb.xml2pdf.saxonext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.so.agi.oereb.xml2pdf.Xml2Pdf;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.OccurrenceIndicator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.trans.XPathException;


public class Test implements ExtensionFunction {
    Logger log = LoggerFactory.getLogger(Test.class);

    @Override
    public QName getName() {
        return new QName("http://some.namespace.com", "test");
    }

    @Override
    public SequenceType getResultType() {
        return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE);
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[] { SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE) };
    }

    @Override
    public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
        log.info(arguments.toString());
        try {
            log.info(arguments[0].getUnderlyingValue().getStringValue().toString());
        } catch (XPathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String result = "Saxon is being extended correctly.";
        return new XdmAtomicValue(result);
    }

}
