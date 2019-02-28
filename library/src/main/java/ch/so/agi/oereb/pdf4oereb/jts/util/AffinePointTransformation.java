package ch.so.agi.oereb.pdf4oereb.jts.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.locationtech.jts.awt.PointTransformation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

public class AffinePointTransformation implements PointTransformation {
    Logger log = LoggerFactory.getLogger(AffinePointTransformation.class);

    private AffineTransform world2pixel = null;
    
    public AffinePointTransformation(Envelope pixelEnvelope, Envelope worldEnvelope) {
        int imageWidthPx = (int) pixelEnvelope.getWidth();
        int imageHeightPx = (int) pixelEnvelope.getHeight();
         
        // see: http://docs.geotools.org/stable/userguide/tutorial/affinetransform.html
        AffineTransform translate = AffineTransform.getTranslateInstance(-1*worldEnvelope.getMinX(), -1*worldEnvelope.getMinY());
        AffineTransform scale = AffineTransform.getScaleInstance(imageWidthPx / worldEnvelope.getWidth(), imageHeightPx /  worldEnvelope.getHeight());
        AffineTransform mirror_y = new AffineTransform(1, 0, 0, -1, 0, imageHeightPx);
        
        world2pixel = new AffineTransform(mirror_y);
        world2pixel.concatenate(scale);
        world2pixel.concatenate(translate);
        log.debug("World2Pixel:" + world2pixel.toString());

        Point2D p = null;
        p = new Point2D.Double(worldEnvelope.getMinX(), worldEnvelope.getMinY());
        log.debug("LLC: " + world2pixel.transform(p,null));
        p = new Point2D.Double(worldEnvelope.getMinX(), worldEnvelope.getMaxY());
        log.debug("ULC: " + world2pixel.transform(p,null));
        p = new Point2D.Double(worldEnvelope.getMaxX(), worldEnvelope.getMaxY());
        log.debug("URC: " + world2pixel.transform(p,null));
        p = new Point2D.Double(worldEnvelope.getMaxX(),worldEnvelope.getMinY());
        log.debug("LRC: " + world2pixel.transform(p,null));
        
        // Vergleich mit JTS. JTS-AffineTransformation kann nicht verwendet werden, da 
        // für ShapeWriter-Transformation das Interface PointTransformation implementiert
        // werden muss.
        /*
        Coordinate src0 = new Coordinate(worldEnvelope.getMinX(), worldEnvelope.getMinY());
        Coordinate src1 = new Coordinate(worldEnvelope.getMaxX(), worldEnvelope.getMinY());
        Coordinate dest0 = new Coordinate(0, pixelEnvelope.getHeight());
        Coordinate dest1 = new Coordinate(pixelEnvelope.getWidth(), pixelEnvelope.getHeight());
        
        world2pixel = AffineTransformationFactory.createFromBaseLines(src0, src1, dest0, dest1);
        
        Coordinate dest = new Coordinate();
        world2pixel.transform(src0, dest);
        */
    }
    
    @Override
    public void transform(Coordinate src, Point2D dest) {
        Point2D p = world2pixel.transform(new Point2D.Double(src.getX(), src.getY()), null);
        log.debug(p.toString());
        dest.setLocation(p);
    }
}
