package ch.so.agi.oereb.pdf4oereb.saxon.ext;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

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

import ch.so.agi.oereb.pdf4oereb.jts.util.AffinePointTransformation;

public class OverlayImage implements ExtensionFunction {
    Logger log = LoggerFactory.getLogger(OverlayImage.class);
    
    private final double referenceDpi = 300.0;
    private final String highlightingStrokeColor = "#e60000";
    private final float highlightingStrokeOpacity = 0.4F;    
    private final Color highlightingStrokeColorRgb = new Color(230/255F, 0F, 0F, highlightingStrokeOpacity);
    private final int highlightingStrokeWidth = (int) (5 * referenceDpi / 72.0); // heuristic
    private final String imageFormat = "png";
    private final double mapWidthMM = 174.0;
    private final double mapHeightMM = 99.0;
    private final int imageWidthPx = 2055; // not needed: it uses same sizes as provided land register map
    private final int imageHeightPx = 1169;

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
	 * Returns a base64 string as XdmValue of the overlay image (parcel, north arrow 
	 * and scale bar) which will be used for title page and the following restriction
	 * on landownership pages.
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
    	// TODO: Handle missing real estate geometry.
    	// Scale bar can be drawn (if information is available) 
    	// but not the highlighting stuff.
    	// -> unit test!
    	
    	// Calculate the real world bounding box of the map image from the xml file.
    	// Null-Envelope is handled properly in the createHighlightingImage method
    	// (at least it should be).
    	Envelope mapEnvelope = calculateBoundingBox(mapNode);
    	    	
    	// Create the overlay image. 
    	byte[] highlightingImage = null;
    	
    	try {
    		highlightingImage = createHighlightingImage(mapNode, mapEnvelope, realEstateDPRGeometry);			
        } catch (Exception e) {
			e.printStackTrace();
			throw new SaxonApiException(e.getMessage());
		}
        return new XdmAtomicValue(new net.sf.saxon.value.Base64BinaryValue(highlightingImage).asAtomic().getStringValue());
    }
	
	
	private byte[] createHighlightingImage(XdmNode node, Envelope worldEnvelope, MultiPolygon geometry) throws XPathException, SaxonApiException, IOException {
	    // Get the land register base image.
	    byte[] mapImageByteArray = getImageFromXdmNode(node);
	    InputStream mapImageInputStream = new ByteArrayInputStream(mapImageByteArray);
        BufferedImage mapBufferedImage = ImageIO.read(mapImageInputStream);
	    
        // Width and height is used several times.
        int imageWidthPx = mapBufferedImage.getWidth();
        int imageHeightPx = mapBufferedImage.getHeight();
        
        // Create the Graphics2D object which will held all images/layers we want to burn in 
        // (scalebar, north arrow and the highlighted parcel).
        Rectangle imageBounds = new Rectangle(imageWidthPx, imageHeightPx);
        BufferedImage hightlightingImage = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);

        Graphics2D gr = hightlightingImage.createGraphics();
        int type = AlphaComposite.SRC;
        gr.setComposite(AlphaComposite.getInstance(type));
        
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        gr.setRenderingHints(rh);

        // TODO: not quite sure how this works
        // e.g. http://www.informit.com/articles/article.aspx?p=26349&seqNum=5
        Color c = new Color(255, 255, 255, 0);
        gr.setBackground(Color.white);
        gr.setColor(c);
        gr.fillRect(0, 0, hightlightingImage.getWidth(), hightlightingImage.getHeight());
        gr.setComposite(AlphaComposite.getInstance(type));

        // Handle the case if the embedded image has no geographical information.
        // We cannot create an overlay image (north arrow would be possible, though).
        if (worldEnvelope == null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(hightlightingImage, imageFormat, baos); 
            baos.flush();
            byte[] highlightingImageByteArray = baos.toByteArray();
            baos.close();      
            
            return highlightingImageByteArray;
        }

        // This will create the highlighting image. It will not
        // create anything if there is no geometry in the xml file.
        if (!geometry.isEmpty()) {
            // Calculate the transformation between provided pixel image and real world coordinate system (parcel geometry).
            Envelope pixelEnvelope = new Envelope(new Coordinate(0, 0), new Coordinate(imageWidthPx, imageHeightPx));
            AffinePointTransformation transformation =  new AffinePointTransformation(pixelEnvelope, worldEnvelope);

            gr.setColor(highlightingStrokeColorRgb);
            gr.setStroke(new BasicStroke((float) highlightingStrokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));

            // TODO: ShapeWriter cannot handle multipolygons (?).
            // But respects holes.
            for (int i=0; i<geometry.getNumGeometries(); i++) {
                Polygon polygon = (Polygon) geometry.getGeometryN(i);
                
                ShapeWriter sw = new ShapeWriter(transformation);
                Shape polyShape = sw.toShape(polygon);
                
                gr.draw(polyShape);
            }
        }
        
        // The scalebar (all values are more or less heuristic)
        double actualDpi = (double) ((mapBufferedImage.getWidth() / mapWidthMM) * 25.4);        
        double dpiRatio = actualDpi / referenceDpi;

        ScalebarGenerator scalebarGenerator = new ScalebarGenerator();
        scalebarGenerator.setColorText(Color.BLACK);
        scalebarGenerator.setDrawScaleText(false);
        scalebarGenerator.setHeight(50);
        scalebarGenerator.setTopMargin(15);
        scalebarGenerator.setLrbMargin(25);
        scalebarGenerator.setNumberOfSegments(2);
        if (actualDpi < 100) {
            scalebarGenerator.setHeight(40);
            scalebarGenerator.setLrbMargin(20);
            Font textFont = new Font(Font.SANS_SERIF, Font.BOLD, (int) Math.round(20*0.5));
            scalebarGenerator.setTextFont(textFont);
        }
        double scale = worldEnvelope.getWidth() / (mapWidthMM / 1000.0);
        double scalebarWidthPx = 400 * dpiRatio;
        byte[] scalebarImageByteArray = scalebarGenerator.getImageAsByte(new Double(scale), scalebarWidthPx, actualDpi);
        InputStream insScalebarImage = new ByteArrayInputStream(scalebarImageByteArray);
        BufferedImage scalebarBufferedImage = ImageIO.read(insScalebarImage);

//        ImageIO.write(scalebarBufferedImage, "png", new File("/Users/stefan/tmp/scalebar.png"));
        
        // add scalebar to graphic
        gr.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); 
        gr.drawImage(scalebarBufferedImage, imageWidthPx / 20, (int) Math.round(imageHeightPx - (imageHeightPx / 20) - scalebarBufferedImage.getHeight()), null); 
        
        // The north arrow
        InputStream northArrowFileInputStream = OverlayImage.class.getResourceAsStream("/oereb_north_arrow_small.png"); 
        BufferedImage northArrowBufferedImage = ImageIO.read(northArrowFileInputStream);
        int scaledNorthArrowWidthPx = (int) (northArrowBufferedImage.getWidth() * dpiRatio);
        int scaledNorthArrowHeightPx = (int) (northArrowBufferedImage.getWidth() * dpiRatio);
        Image tmpNorthArrowImage = northArrowBufferedImage.getScaledInstance(scaledNorthArrowWidthPx, scaledNorthArrowHeightPx, Image.SCALE_SMOOTH);
                
        // add north arrow to graphic
        gr.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); 
        int leftMargin = imageWidthPx / 20 + (scalebarBufferedImage.getWidth() / 2) - (scaledNorthArrowWidthPx / 2);
        gr.drawImage(tmpNorthArrowImage,  leftMargin, (int) Math.round(imageHeightPx - (imageHeightPx / 20) - scalebarBufferedImage.getHeight() - scaledNorthArrowHeightPx), null); 
        
        // write image to byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(hightlightingImage, imageFormat, baos); 
        baos.flush();
        byte[] highlightingImageByteArray = baos.toByteArray();
        baos.close();          

        //ImageIO.write(hightlightingImage, "png", new File("/Users/stefan/tmp/nogeotools.png"));        
	    return highlightingImageByteArray;
	}
	
	/*
	 * Creates a byte array of the image provided in the XdmNode (either as base64 string or
	 * as wms getmap url).
	 */
	private byte[] getImageFromXdmNode(XdmNode node) throws SaxonApiException, XPathException {
        // If the images is embedded, use this.
        byte[] mapImageByteArray = null;
        Iterator<XdmNode> it = node.children("Image").iterator();
        while(it.hasNext()) {
            XdmNode imageNode = (XdmNode) it.next();
            XdmValue mapImageXdmValue = imageNode.getTypedValue();
            mapImageByteArray = Base64.getDecoder().decode(mapImageXdmValue.getUnderlyingValue().getStringValue());
            break;
        }
        
        // Only get the image by a wms request if it was not embedded.
        if (mapImageByteArray == null) {
            it = node.children("ReferenceWMS").iterator();
            while(it.hasNext()) {
                XdmNode imageNode = (XdmNode) it.next();
                XdmValue referenceWmsXdmValue = imageNode.getTypedValue();
                try {
                    mapImageByteArray = WebMapService.getMap(referenceWmsXdmValue.getUnderlyingValue().getStringValue());
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                    throw new SaxonApiException(e.getMessage());
                }
                break;
            }
        }
        return mapImageByteArray;
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
    	if (minCoord == null || maxCoord == null) {
    		return null;
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
