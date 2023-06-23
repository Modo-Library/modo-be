package modo.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class GeomUtil {
    public static GeomUtil geomUtil = new GeomUtil();

    public static Point createPoint(double lat, double lon) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(lat, lon));
    }
}
