package ch.so.agi.oereb.pdf4oereb.utils;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;

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

    /**
     * Repariert den GetMap-Request: 
     * - Korrigiert den WIDTH- und HEIGHT-Wert, so dass die Pixelzahl 300dpi entspricht.
     * - Fügt für die drei bekannten WMS-Server den vendorspezifischen DPI-Parameter hinzu.
     * - Fordert die Bilder transparent an.
     * 
     * @param requestString
     * @param referenceDpi
     * @return
     * @throws URISyntaxException
     */
    public static String fixGetMapRequest(String requestString, double referenceDpi) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(requestString);
        List<NameValuePair> queryParams = builder.getQueryParams();
        List<NameValuePair> correctedQueryParams = new ArrayList<>();
        boolean hasTransparent = false;
        for (NameValuePair queryParam : queryParams) {
            if (queryParam.getName().equalsIgnoreCase("WIDTH") || queryParam.getName().equalsIgnoreCase("HEIGHT")) {
                String name = queryParam.getName();
                int correctedValue;
                if (name.equalsIgnoreCase("WIDTH")) {
                    correctedValue = Double.valueOf(referenceDpi * 17.4 / 2.54).intValue();
                } else {
                    correctedValue = Double.valueOf(referenceDpi * 9.9 / 2.54).intValue();
                }
                NameValuePair nvp = new BasicNameValuePair(name, String.valueOf(correctedValue));
                correctedQueryParams.add(nvp);
            } else if (queryParam.getName().equalsIgnoreCase("TRANSPARENT")) {
                String name = queryParam.getName();
                String value = "true";
                NameValuePair nvp = new BasicNameValuePair(name, String.valueOf(value));
                correctedQueryParams.add(nvp);
                hasTransparent = true;
            } else {
                correctedQueryParams.add(queryParam);
            }
            
            
        }
        String dpi = String.valueOf(referenceDpi);
        Map<String, String> dpis = Map.of("DPI", dpi, "MAP_RESOLUTION", dpi, "FORMAT_OPTIONS", "dpi:"+dpi);
        for (Map.Entry<String, String> entry : dpis.entrySet()) {
            NameValuePair nvp = new BasicNameValuePair(entry.getKey(), entry.getValue());
            correctedQueryParams.add(nvp);
        }
        
        if (!hasTransparent) {
            NameValuePair nvp = new BasicNameValuePair("TRANSPARENT", "true");
            correctedQueryParams.add(nvp);            
        }

        builder.removeQuery();
        builder.addParameters(correctedQueryParams);
        
        return builder.build().toString();
    } 
}
