package ch.so.agi.oereb.pdf4oereb.saxon.ext;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.geotools.data.DataUtilities;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryImpl;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
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
import net.sf.saxon.trans.XPathException;

public class OverlayImage implements ExtensionFunction {
    Logger log = LoggerFactory.getLogger(OverlayImage.class);

    private final String highlightingStrokeColor = "#e60000";
    private final int highlightingStrokeWidth = 6;
    private final double highlightingStrokeOpacity = 0.4;
    private final int dpi = 300;
    private final String imageFormat = "png";

	@Override
	public QName getName() {
        return new QName("http://oereb.agi.so.ch", "getOverlayImage");
	}

	@Override
	public SequenceType getResultType() {
        return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE);
	}

	@Override
	public SequenceType[] getArgumentTypes() {
        return new SequenceType[] { SequenceType.makeSequenceType(ItemType.ANY_ITEM, OccurrenceIndicator.ONE), 
        		SequenceType.makeSequenceType(ItemType.ANY_ITEM, OccurrenceIndicator.ONE) };
	}
	
	/**
	 * Returns a base64 string as XdmValue of the overlay image (parcel, north arrow (TODO) 
	 * and scale bar (TODO)).
	 * 
	 * @param arguments an array of XdmValues containing the limit node (the parcels geometry)  
	 *                  and the map node containing the land registry map image. 
	 * @return          the overlay image as base64 string 
	 */
	@Override
	public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
    	XdmNode limitNode = (XdmNode) arguments[0];
    	XdmNode mapNode = (XdmNode) arguments[1];

    	// Create a jts geometry from gml geometry.
    	MultiPolygon realEstateDPRGeometry = multiSurface2JTS(limitNode);
    	
    	// Calculate the real world bounding box of the map image from the extract.
    	Envelope mapEnvelope = calculateBoundingBox(mapNode);

    	// Create the overlay image. 
    	byte[] highlightingImage = null;
    	try {
    		highlightingImage = createHighlightingImage(mapNode, mapEnvelope, realEstateDPRGeometry);			
		} catch (XPathException | IOException | SchemaException | FactoryException e) {
			e.printStackTrace();
			throw new SaxonApiException(e.getMessage());
		}

        return new XdmAtomicValue(new net.sf.saxon.value.Base64BinaryValue(highlightingImage).asAtomic().getStringValue());
	}
	
	/*
	 * Creates the highlighting image by rendering the parcel from the geometry.
	 * The provided node contains the land registry map image, which is needed to find out the
	 * image width and height.
	 */
    private byte[] createHighlightingImage(XdmNode node, Envelope envelope, Geometry geometry) throws SaxonApiException, XPathException, IOException, SchemaException, NoSuchAuthorityCodeException, FactoryException {
		byte[] mapImageByteArray = null;
    	Iterator<XdmNode> it = node.children("Image").iterator();
    	while(it.hasNext()) {
    		XdmNode imageNode = (XdmNode) it.next();
    		XdmValue mapImageXdmValue = imageNode.getTypedValue();
    		mapImageByteArray = Base64.getDecoder().decode(mapImageXdmValue.getUnderlyingValue().getStringValue());
    		break;
    	}
    	
    	InputStream mapImageInputStream = new ByteArrayInputStream(mapImageByteArray);
		BufferedImage mapBufferedImage = ImageIO.read(mapImageInputStream);

		int imageWidthPx = mapBufferedImage.getWidth();
		int imageHeightPx = mapBufferedImage.getHeight();
		
		// This will create the highlighting image:
		// Create the feature, feature collection and a feature layer that can
		// be added to a map content.
		FeatureCollection<SimpleFeatureType, SimpleFeature> collection = new DefaultFeatureCollection();
		SimpleFeatureType TYPE = DataUtilities.createType(
				"Parcel", "the_geom:MultiPolygon:srid=2056," 
						+ "egrid:String");
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);

		featureBuilder.add(geometry);
		featureBuilder.add("fubar");
		SimpleFeature feature = featureBuilder.buildFeature(null);
		((DefaultFeatureCollection)collection).add(feature);

		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
		StyleFactory sf = new StyleFactoryImpl();
		sf.stroke(ff.literal("#000000"), null, ff.literal(2.0), null, null, null, null);
		Style style = sf.createStyle();

		// This is a bitch...
		PolygonSymbolizer polygonSymbolizer = sf.createPolygonSymbolizer();
		Stroke stroke = sf.createStroke(ff.literal(highlightingStrokeColor), ff.literal(highlightingStrokeWidth));
		stroke.setOpacity(ff.literal(highlightingStrokeOpacity)); // opacity vs transparency?
		polygonSymbolizer.setStroke(stroke);
		Rule rl = sf.createRule();
		rl.symbolizers().add(polygonSymbolizer);
		FeatureTypeStyle ft = sf.createFeatureTypeStyle();
		ft.rules().add(rl);
		style.featureTypeStyles().add(ft);

		FeatureLayer fl = new FeatureLayer(collection, style);
		fl.setVisible(true);

		// Create the map content from which we export the image.
		MapContent map = new MapContent();
		MapViewport vp = new MapViewport();
		CoordinateReferenceSystem crs = CRS.decode("EPSG:2056");
		vp.setCoordinateReferenceSystem(crs);

		ReferencedEnvelope re = new ReferencedEnvelope(envelope, crs);
		vp.setBounds(re);
		map.setViewport(vp);            
		map.addLayer(fl);

		// We need a renderer for exporting the image.
		GTRenderer renderer = new StreamingRenderer();
		renderer.setMapContent(map);

		Rectangle imageBounds = new Rectangle(imageWidthPx, imageHeightPx);
		BufferedImage hightlightingImage = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);

		Graphics2D gr = hightlightingImage.createGraphics();
		int type = AlphaComposite.SRC;
		gr.setComposite(AlphaComposite.getInstance(type));

		// TODO: not quite sure how this works
		// e.g. http://www.informit.com/articles/article.aspx?p=26349&seqNum=5
		Color c = new Color(255, 255, 255, 0);
		gr.setBackground(Color.white);
		gr.setColor(c);
		gr.fillRect(0, 0, hightlightingImage.getWidth(), hightlightingImage.getHeight());
		gr.setComposite(AlphaComposite.getInstance(type));

		RenderingHints renderingHints = new Hints();
		renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		renderer.setJava2DHints(renderingHints);

		Map<Object, Object> rendererHints = new HashMap<Object, Object>();
		rendererHints.put(StreamingRenderer.DPI_KEY, dpi);
		renderer.setRendererHints(rendererHints);

		renderer.paint(gr, imageBounds, vp.getBounds());

//		ImageIO.write(hightlightingImage, imageFormat, new File("/Users/stefan/tmp/fubar3.png"));

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(hightlightingImage, imageFormat, baos); 
		baos.flush();
		byte[] highlightingImageByteArray = baos.toByteArray();
		baos.close();          
		map.dispose();

		return highlightingImageByteArray;
    }

    /*
     * Calculates the Bounding Box of the map image from the extract.
     * This is needed to georeference the new highlighting image.
     * Min/max coords are children of the <Map> element.
     */
    private Envelope calculateBoundingBox(XdmNode node) {
    	Coordinate minCoord = null;
    	Coordinate maxCoord = null;
    	// min coord
    	Iterator<XdmNode> it = node.children("min_NS95").iterator();
    	while(it.hasNext()) {
    		XdmNode minNode = (XdmNode) it.next();
    		Iterator<XdmNode> jt = minNode.children("Point").iterator();
    		while(jt.hasNext()) {
        		XdmNode pointNode = (XdmNode) jt.next();
        		Iterator<XdmNode> kt = pointNode.children("pos").iterator();
        		while(kt.hasNext()) {
        			XdmNode posNode = (XdmNode) kt.next();
            		String[] coords = posNode.getUnderlyingNode().getStringValue().split(" ");
            		minCoord = new Coordinate(Double.valueOf(coords[0]), Double.valueOf(coords[1]));
        		}
    		}
    		break;
    	}
    	// max coord
    	Iterator<XdmNode> lt = node.children("max_NS95").iterator();
    	while(lt.hasNext()) {
    		XdmNode minNode = (XdmNode) lt.next();
    		Iterator<XdmNode> jt = minNode.children("Point").iterator();
    		while(jt.hasNext()) {
        		XdmNode pointNode = (XdmNode) jt.next();
        		Iterator<XdmNode> kt = pointNode.children("pos").iterator();
        		while(kt.hasNext()) {
        			XdmNode posNode = (XdmNode) kt.next();
            		String[] coords = posNode.getUnderlyingNode().getStringValue().split(" ");
            		maxCoord = new Coordinate(Double.valueOf(coords[0]), Double.valueOf(coords[1]));
        		}
    		}
    		break;
    	}
        Envelope envelope = new Envelope(minCoord, maxCoord);
		return envelope;
    }
	
    /*
     * Creates a jts multipolygon from a gml multisurface node.
     * 
     * Following encodings are supported:
     * 
     * <gml:LinearRing>
     *   <gml:posList>2669946.002 1201975.174 2669957.242 1201968.811 ...</gml:posList>
     * </gml:LinearRing>
     * 
     * <gml:LinearRing>
     *   <gml:pos>2669946.002 1201975.174</gml:pos> <gml:pos>2669957.242 1201968.811</gml:pos> ...
     * </gml:LinearRing>
     *
     */
    private MultiPolygon multiSurface2JTS(XdmNode inputNode) {
		MultiPolygon multiPolygon = null;
		List<Polygon> polygonList = new ArrayList<Polygon>();
		GeometryFactory geometryFactory = new GeometryFactory();

    	Iterator<XdmNode> it = inputNode.children("MultiSurface").iterator();
    	while(it.hasNext()) {
    		XdmNode multiSurfaceNode = (XdmNode) it.next();
    		Iterator<XdmNode> jt = multiSurfaceNode.children("surfaceMember").iterator();
    		while(jt.hasNext()) {
    			XdmNode surfaceMember = (XdmNode) jt.next();
    			Iterator<XdmNode> kt = surfaceMember.children("Polygon").iterator();
    			
    			LinearRing shell = null;
    			List<LinearRing> holes = new ArrayList<LinearRing>();
    			
    			while(kt.hasNext()) {
    				XdmNode polygonNode = (XdmNode) kt.next();
    				// exterior 
    				Iterator<XdmNode> lt = polygonNode.children("exterior").iterator();
    				while(lt.hasNext()) {
    					XdmNode node = (XdmNode) lt.next();
    					Iterator<XdmNode> mt = node.children("LinearRing").iterator();
    					while(mt.hasNext()) {
    						XdmNode linearRingNode = (XdmNode) mt.next();
    						Iterator<XdmNode> nt = linearRingNode.children("posList").iterator();
    						
    						// gml-posList-encoding (ZH, NW)
    						if (nt.hasNext()) {
	    						while(nt.hasNext()) {
	    							XdmNode posListNode = (XdmNode) nt.next();
	    							String coordsString = posListNode.getUnderlyingNode().getStringValue();
	    							String[] coordsArray = coordsString.split(" ");
	    							List<Coordinate> coordsList = new ArrayList<Coordinate>();		
	    							for(int i=0; i<coordsArray.length; i=i+2) {
	    								Coordinate coord = new Coordinate(Double.valueOf(coordsArray[i]), Double.valueOf(coordsArray[i+1]));
	    								coordsList.add(coord);
	    							}
	    							shell = geometryFactory.createLinearRing(coordsList.toArray(new Coordinate[0]));
	    						}
    						} 
    						// gml-pos-encoding (BL)
    						else {
    							nt = linearRingNode.children("pos").iterator();
    							List<Coordinate> coordsList = new ArrayList<Coordinate>();		
    							while(nt.hasNext()) {
    								XdmNode posNode = (XdmNode) nt.next();
    								String coordString = posNode.getUnderlyingNode().getStringValue();
    								String[] coordArray = coordString.split(" ");
    								Coordinate coord = new Coordinate(Double.valueOf(coordArray[0]), Double.valueOf(coordArray[1]));
    								coordsList.add(coord);
    							}
    							shell = geometryFactory.createLinearRing(coordsList.toArray(new Coordinate[0]));
    						}
    					}            				
    				}
    				// interior
    				Iterator<XdmNode> ot = polygonNode.children("interior").iterator();
    				while(ot.hasNext()) {
    					XdmNode node = (XdmNode) ot.next();
    					Iterator<XdmNode> mt = node.children("LinearRing").iterator();
    					
    					while(mt.hasNext()) {
    						XdmNode linearRingNode = (XdmNode) mt.next();
    						Iterator<XdmNode> nt = linearRingNode.children("posList").iterator();
    						
    						// gml-posList-encoding (ZH, NW)
    						if (nt.hasNext()) {
	    						while(nt.hasNext()) {
	    							XdmNode posListNode = (XdmNode) nt.next();
	    							String coordsString = posListNode.getUnderlyingNode().getStringValue();
	    							String[] coordsArray = coordsString.split(" ");
	    							List<Coordinate> coordsList = new ArrayList<Coordinate>();		
	    							for(int i=0; i<coordsArray.length; i=i+2) {
	    								Coordinate coord = new Coordinate(Double.valueOf(coordsArray[i]), Double.valueOf(coordsArray[i+1]));
	    								coordsList.add(coord);
	    							}
	    							LinearRing hole = geometryFactory.createLinearRing(coordsList.toArray(new Coordinate[0]));
	    							holes.add(hole);
	    						}
    						} 
    						// gml-pos-encoding (BL)
    						else {
    							nt = linearRingNode.children("pos").iterator();
    							List<Coordinate> coordsList = new ArrayList<Coordinate>();	
    							if (nt.hasNext()) {
        							while(nt.hasNext()) {
        								XdmNode posNode = (XdmNode) nt.next();
        								String coordString = posNode.getUnderlyingNode().getStringValue();
        								String[] coordArray = coordString.split(" ");
        								Coordinate coord = new Coordinate(Double.valueOf(coordArray[0]), Double.valueOf(coordArray[1]));
        								coordsList.add(coord);
        							}
        							LinearRing hole = geometryFactory.createLinearRing(coordsList.toArray(new Coordinate[0]));
        							holes.add(hole);
    							}
    						}
    					}            				
    				}
    			}
    			// create polygon
    			Polygon polygon = geometryFactory.createPolygon(shell, holes.toArray(new LinearRing[0]));
    			polygonList.add(polygon);
    		}	
    	} 
    	multiPolygon = geometryFactory.createMultiPolygon(polygonList.toArray(new Polygon[0]));
    	multiPolygon.setSRID(2056);
		return multiPolygon;
    }
}
