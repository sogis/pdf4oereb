package ch.so.agi.oereb.xml2pdf.saxonext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.OccurrenceIndicator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.streams.XdmStream;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.tiny.TinyElementImpl;
import net.sf.saxon.s9api.streams.Predicates;

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
        return new SequenceType[] { SequenceType.makeSequenceType(ItemType.ANY_ITEM, OccurrenceIndicator.ONE) };
    }

    @Override
    public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
//        log.info(arguments.toString());
//        try {
//            log.info(arguments[0].getUnderlyingValue().getStringValue().toString());
          
    	        
//    	XdmNode limitNode = (XdmNode) arguments[0];
//    	Iterator it = limitNode.children(Predicates.hasLocalName("MultiSurface")).iterator();
//    	while (it.hasNext()) {
//    		log.info(it.next().toString());
//    	}
    	
    	XdmNode limitNode = (XdmNode) arguments[0];
    	Iterator it = limitNode.children("MultiSurface").iterator();
    	while(it.hasNext()) {
    		XdmNode multiSurfaceNode = (XdmNode) it.next();
    		Iterator jt = multiSurfaceNode.children("surfaceMember").iterator();
    		while(jt.hasNext()) {
    			XdmNode surfaceMember = (XdmNode) jt.next();
    			Iterator kt = surfaceMember.children("Polygon").iterator();
    			while(kt.hasNext()) {
    				XdmNode polygonNode = (XdmNode) kt.next();
    				
    				Iterator lt = polygonNode.children("exterior").iterator();
    
    				// function...
    				while(lt.hasNext()) {
    					XdmNode node = (XdmNode) lt.next();
    					Iterator mt = node.children("LinearRing").iterator();
    					while(mt.hasNext()) {
    						XdmNode linearRingNode = (XdmNode) mt.next();
    						Iterator nt = linearRingNode.children("posList").iterator();
    						while(nt.hasNext()) {
    							XdmNode posListNode = (XdmNode) nt.next();
    							log.info(posListNode.getUnderlyingNode().getStringValue());
    							String coordsString = posListNode.getUnderlyingNode().getStringValue();
    							String[] coordsArray = coordsString.split(" ");
//    							List<Coordinate> coordsList = new ArrayList<Coordinate>();
    							for(int i=0; i<coordsArray.length; i=i+2) {
    								log.info(coordsArray[i]);
    							}
    						}
    					}            				
    				}            			
    			}
    		}	
    	} 
            
            
            
            
//        } catch (XPathException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        String result = "Saxon is being extended correctly.";
        return new XdmAtomicValue(result);
    }

}
