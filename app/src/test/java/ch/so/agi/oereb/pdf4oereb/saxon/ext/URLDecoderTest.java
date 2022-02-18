package ch.so.agi.oereb.pdf4oereb.saxon.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.tree.util.Orphan;
import net.sf.saxon.type.Type;

public class URLDecoderTest {
    Logger log = LoggerFactory.getLogger(URLDecoderTest.class);

    @Test
    public void decodeURL_Ok() throws SaxonApiException {
        Processor processor = new Processor(false);

        // classes can change over time
        Orphan node = new Orphan(processor.getUnderlyingConfiguration());
        node.setNodeKind(Type.TEXT);
        node.setStringValue("https%3A%2F%2Fgeoview.bl.ch%2Fmain%2Foereb%2Fmapservproxy%3FLAYERS%3DLandUsePlans%26STYLES%3Ddefault%26SERVICE%3DWMS%26FORMAT%3Dimage%252Fpng%26REQUEST%3DGetMap%26SRS%3DEPSG%253A2056%26HEIGHT%3D280%26WIDTH%3D493%26VERSION%3D1.1.1%26BBOX%3D2610008.27946%252C1264969.43081%252C2610087.26754%252C1265014.29219");
        XdmNode textNode = new XdmNode(node);
        
        XdmNode[] arguments = {textNode};
        URLDecoder decoder = new URLDecoder();
        XdmAtomicValue decodedURL = (XdmAtomicValue) decoder.call(arguments);

        String expectedURL = "https://geoview.bl.ch/main/oereb/mapservproxy?LAYERS=LandUsePlans&STYLES=default&SERVICE=WMS&FORMAT=image%2Fpng&REQUEST=GetMap&SRS=EPSG%3A2056&HEIGHT=280&WIDTH=493&VERSION=1.1.1&BBOX=2610008.27946%2C1264969.43081%2C2610087.26754%2C1265014.29219";
        
        assertEquals(expectedURL, decodedURL.getStringValue(), "URL is not decoded correctly.");
    }
}
