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
import java.io.File;
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

import ch.so.agi.oereb.pdf4oereb.utils.AffinePointTransformation;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.OccurrenceIndicator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmNodeKind;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.trans.XPathException;

public class OverlayImage implements ExtensionFunction {
    Logger log = LoggerFactory.getLogger(OverlayImage.class);
    
    private final double referenceDpi = 300.0;
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
        return new QName("http://oereb.so.ch", "createOverlayImage");
    }

    @Override
    public SequenceType getResultType() {
        return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE);
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[] { SequenceType.makeSequenceType(ItemType.ANY_NODE, OccurrenceIndicator.ONE), 
                SequenceType.makeSequenceType(ItemType.ANY_NODE, OccurrenceIndicator.ONE) };
    }
    
    /**
     * Returns a base64 string as XdmValue of the overlay image (parcel highlighting, north arrow 
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
        byte[] overlayImage = null;
        
        try {
            overlayImage = createOverlayImage(mapNode, mapEnvelope, realEstateDPRGeometry);           
        } catch (Exception e) {
            e.printStackTrace();
            throw new SaxonApiException(e.getMessage());
        }
        return new XdmAtomicValue(new net.sf.saxon.value.Base64BinaryValue(overlayImage).asAtomic().getStringValue());
    }
    
    
    private byte[] createOverlayImage(XdmNode node, Envelope worldEnvelope, MultiPolygon geometry) throws XPathException, SaxonApiException, IOException {
        // Get the land register base image.
        byte[] mapImageByteArray = getImageFromXdmNode(node);
        InputStream mapImageInputStream = new ByteArrayInputStream(mapImageByteArray);
        BufferedImage mapBufferedImage = ImageIO.read(mapImageInputStream);
        //ImageIO.write(mapBufferedImage, "png", new File("/Users/stefan/tmp/map_image.png"));
        
        // Width and height are used several times.
        int imageWidthPx = mapBufferedImage.getWidth();
        int imageHeightPx = mapBufferedImage.getHeight();
        
        // Create the Graphics2D object which will held all images/layers we want to burn in 
        // (scalebar, north arrow and the highlighted parcel).
        Rectangle imageBounds = new Rectangle(imageWidthPx, imageHeightPx);
        BufferedImage overlayImage = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
        //ImageIO.write(overlayImage, "png", new File("/Users/stefan/tmp/empty_overlay_image.png"));
        
        Graphics2D gr = overlayImage.createGraphics();
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
        gr.fillRect(0, 0, overlayImage.getWidth(), overlayImage.getHeight());
        gr.setComposite(AlphaComposite.getInstance(type));

        // Handle the case if the embedded image has no geographical information.
        // We cannot create an overlay image (north arrow would be possible, though).
        if (worldEnvelope == null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(overlayImage, imageFormat, baos); 
            baos.flush();
            byte[] overlayImageByteArray = baos.toByteArray();
            baos.close();      
            
            return overlayImageByteArray;
        }

        // This will create the highlighting image. It will not
        // create anything if there is no geometry in the xml file.
        if (!geometry.isEmpty()) {
            // Calculate the transformation between provided pixel image and real world coordinate system (parcel geometry).
            Envelope pixelEnvelope = new Envelope(new Coordinate(0, 0), new Coordinate(imageWidthPx, imageHeightPx));
            AffinePointTransformation transformation =  new AffinePointTransformation(pixelEnvelope, worldEnvelope);

            double actualDpi = (double) ((mapBufferedImage.getWidth() / mapWidthMM) * 25.4);     
            
            gr.setColor(highlightingStrokeColorRgb);
            gr.setStroke(new BasicStroke((float) (highlightingStrokeWidth/(referenceDpi/actualDpi)), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));

            // ShapeWriter cannot handle multipolygons (?).
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
        byte[] scalebarImageByteArray = scalebarGenerator.getImageAsByte(Double.valueOf(scale), scalebarWidthPx, actualDpi);
        InputStream insScalebarImage = new ByteArrayInputStream(scalebarImageByteArray);
        BufferedImage scalebarBufferedImage = ImageIO.read(insScalebarImage);
        //ImageIO.write(scalebarBufferedImage, "png", new File("/Users/stefan/tmp/scalebar_image.png"));
        
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
        ImageIO.write(overlayImage, imageFormat, baos); 
        baos.flush();
        byte[] highlightingImageByteArray = baos.toByteArray();
        baos.close();          

        //ImageIO.write(overlayImage, "png", new File("/Users/stefan/tmp/overlay_image.png"));        
        return highlightingImageByteArray;
    }
    
    /*
     * Creates a byte array of the image provided in the XdmNode (either as base64 string or
     * as wms getmap url).
     */
    private byte[] getImageFromXdmNode(XdmNode node) throws SaxonApiException, XPathException {
        // Falls das Bild im XML eingebettet ist, wird dieses verwendet.
        byte[] mapImageByteArray = null;
        Iterator<XdmNode> it = node.children("Image").iterator();
        
        while(it.hasNext()) {
            XdmNode imageNode = (XdmNode) it.next();
            Iterator<XdmNode> jt = imageNode.children().iterator();
            while(jt.hasNext()) {
                XdmNode subNode = (XdmNode) jt.next();
                if (subNode.getNodeKind().equals(XdmNodeKind.ELEMENT)) {
                    if (subNode.getNodeName().getLocalName().toString().equalsIgnoreCase("Language")) {
                        // Falls die Sprache berücksichtigt werden soll, muss dies
                        // hier implementiert werden. Achtung: Vielleicht müssen die
                        // Informationen dann gleichzeitig ausgelesen werden (Sprache 
                        // und Image) und nicht mehr sequentiell.
                        // Das Vorgehen jetzt entspricht dem Vorgehen in der XSLT-Transformation.
                        // Es wird das erste Element verwendet.
                    } else if(subNode.getNodeName().getLocalName().toString().equalsIgnoreCase("Image")) {
                        // trim(): Ist nicht ganz nachvollziehbar. Stimmt das Auslesen des Strings
                        // vom Node? Ggf. getMimeDecoder() verwenden.
                        String base64String = subNode.getTypedValue().getUnderlyingValue().getStringValue().trim();
                        mapImageByteArray = Base64.getDecoder().decode(base64String);
                        break;
                    }
                }
            }
        }
        
        // Das Bild wird nur vom WMS bezogen, falls kein eingebettetes Bild vorhanden ist.
        if (mapImageByteArray == null) {
            it = node.children("ReferenceWMS").iterator();
            while(it.hasNext()) {
                XdmNode wmsNode = (XdmNode) it.next();
                Iterator<XdmNode> jt = wmsNode.children("LocalisedText").iterator();
                while(jt.hasNext()) {
                    XdmNode localisedTextNode = (XdmNode) jt.next();
                    Iterator<XdmNode> kt = localisedTextNode.children().iterator();
                    while(kt.hasNext()) {
                        XdmNode subNode = (XdmNode) kt.next();
                        if (subNode.getNodeKind().equals(XdmNodeKind.ELEMENT)) {
                            // TODO: Sprache
                            if (subNode.getNodeName().getLocalName().toString().equalsIgnoreCase("Text")) {
                                String requestString = subNode.getTypedValue().getUnderlyingValue().getStringValue().trim();
                                try {
                                    mapImageByteArray = ch.so.agi.oereb.pdf4oereb.utils.Image.getImage(requestString);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    log.error(e.getMessage());
                                    throw new SaxonApiException(e.getMessage());
                                }
                                break;
                            }
                        }
                    }
                }
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
        Iterator<XdmNode> minIterator = node.children("min").iterator();
        while(minIterator.hasNext()) {
            XdmNode coordNode = (XdmNode) minIterator.next();
            Iterator<XdmNode> jt = coordNode.children().iterator();
            Double c1 = null;
            Double c2 = null;
            while(jt.hasNext()) {
                XdmNode cNode = (XdmNode) jt.next();
                if (cNode.getNodeKind().equals(XdmNodeKind.ELEMENT)) {
                    if (cNode.getNodeName().getLocalName().equalsIgnoreCase("c1")) {
                        c1 = Double.valueOf(cNode.getStringValue());
                    } else if (cNode.getNodeName().getLocalName().equalsIgnoreCase("c2")) {
                        c2 = Double.valueOf(cNode.getStringValue());
                    }
                }
            }
            if (c1 != null && c2 != null) {
                minCoord = new Coordinate(c1, c2);
            }
            break;
        }
        // max coord
        Iterator<XdmNode> maxIterator = node.children("max").iterator();
        while(maxIterator.hasNext()) {
            XdmNode coordNode = (XdmNode) maxIterator.next();
            Iterator<XdmNode> jt = coordNode.children().iterator();
            Double c1 = null;
            Double c2 = null;
            while(jt.hasNext()) {
                XdmNode cNode = (XdmNode) jt.next();
                if (cNode.getNodeKind().equals(XdmNodeKind.ELEMENT)) {
                    if (cNode.getNodeName().getLocalName().equalsIgnoreCase("c1")) {
                        c1 = Double.valueOf(cNode.getStringValue());
                    } else if (cNode.getNodeName().getLocalName().equalsIgnoreCase("c2")) {
                        c2 = Double.valueOf(cNode.getStringValue());
                    }
                }
            }
            if (c1 != null && c2 != null) {
                maxCoord = new Coordinate(c1, c2);
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
     * Creates a jts multipolygon from a ili 2.4 multisurface node.
     */
    private MultiPolygon multiSurface2JTS(XdmNode inputNode) {
        MultiPolygon multiPolygon = null;
        List<Polygon> polygonList = new ArrayList<Polygon>();
        GeometryFactory geometryFactory = new GeometryFactory();

        // Annahme: Allfällig mehrere Polylines sind zusammenhängend.
        // Sonst müsste man die Polyline einzeln speichern und 
        // anschliessend polygonieren.
       
        Iterator<XdmNode> it = inputNode.children("surface").iterator();
        while(it.hasNext()) {
            List<Coordinate> exteriorCoordsList = new ArrayList<Coordinate>();
            LinearRing shell = null;
            List<LinearRing> holes = new ArrayList<LinearRing>();            
            
            XdmNode surfaceNode = (XdmNode) it.next();
            Iterator<XdmNode> exteriorIterator = surfaceNode.children("exterior").iterator();            
            while(exteriorIterator.hasNext()) {
                XdmNode exteriorNode = (XdmNode) exteriorIterator.next();
                Iterator<XdmNode> kt = exteriorNode.children("polyline").iterator();
                while(kt.hasNext()) {
                    XdmNode polylineNode = (XdmNode) kt.next();
                    Iterator<XdmNode> lt = polylineNode.children("coord").iterator();
                    while(lt.hasNext()) {
                        XdmNode coordNode = (XdmNode) lt.next();
                        Iterator<XdmNode> mt = coordNode.children().iterator();
                        Double c1 = null;
                        Double c2 = null;
                        while(mt.hasNext()) {
                            XdmNode cNode = (XdmNode) mt.next();
                            if (cNode.getNodeKind().equals(XdmNodeKind.ELEMENT)) {
                                if (cNode.getNodeName().getLocalName().equalsIgnoreCase("c1")) {
                                    c1 = Double.valueOf(cNode.getStringValue());
                                } else if (cNode.getNodeName().getLocalName().equalsIgnoreCase("c2")) {
                                    c2 = Double.valueOf(cNode.getStringValue());
                                }
                            }
                        }
                        if (c1 != null && c2 != null) {
                            Coordinate coord = new Coordinate(c1, c2);
                            exteriorCoordsList.add(coord);
                        }
                    }
                }
            }
            shell = geometryFactory.createLinearRing(exteriorCoordsList.toArray(new Coordinate[0]));            
           
            Iterator<XdmNode> interiorIterator = surfaceNode.children("interior").iterator();
            while(interiorIterator.hasNext()) {
                List<Coordinate> interiorCoordsList = new ArrayList<Coordinate>();
                XdmNode interiorNode = (XdmNode) interiorIterator.next();
                Iterator<XdmNode> kt = interiorNode.children("polyline").iterator();
                while(kt.hasNext()) {
                    XdmNode polylineNode = (XdmNode) kt.next();
                    Iterator<XdmNode> lt = polylineNode.children("coord").iterator();
                    while(lt.hasNext()) {
                        XdmNode coordNode = (XdmNode) lt.next();
                        Iterator<XdmNode> mt = coordNode.children().iterator();
                        Double c1 = null;
                        Double c2 = null;
                        while(mt.hasNext()) {
                            XdmNode cNode = (XdmNode) mt.next();
                            if (cNode.getNodeKind().equals(XdmNodeKind.ELEMENT)) {
                                if (cNode.getNodeName().getLocalName().equalsIgnoreCase("c1")) {
                                    c1 = Double.valueOf(cNode.getStringValue());
                                } else if (cNode.getNodeName().getLocalName().equalsIgnoreCase("c2")) {
                                    c2 = Double.valueOf(cNode.getStringValue());
                                }
                            }
                        }
                        if (c1 != null && c2 != null) {
                            Coordinate coord = new Coordinate(c1, c2);
                            interiorCoordsList.add(coord);
                        }
                    }
                }
                LinearRing hole = geometryFactory.createLinearRing(interiorCoordsList.toArray(new Coordinate[0]));
                holes.add(hole);
            }
            Polygon polygon = geometryFactory.createPolygon(shell, holes.toArray(new LinearRing[0]));
            polygonList.add(polygon);                
        } 
        multiPolygon = geometryFactory.createMultiPolygon(polygonList.toArray(new Polygon[0]));
        multiPolygon.setSRID(2056);
        return multiPolygon;
    }
}
