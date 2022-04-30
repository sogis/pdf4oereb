package ch.so.agi.oereb.pdf4oereb.utils;

import java.util.Iterator;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.trans.XPathException;

public class Utils {
    
    public static String extractMultilingualText(XdmNode node, String type) throws XPathException, SaxonApiException {
        Iterator<XdmNode> it = node.children(type).iterator();
        while(it.hasNext()) {
            XdmNode subNode = it.next();
            return subNode.getStringValue().trim();
        }
        return null;
    }

}
