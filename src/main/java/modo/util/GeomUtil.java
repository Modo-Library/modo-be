package modo.util;

import lombok.extern.log4j.Log4j2;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@Log4j2
public class GeomUtil {
    public static GeomUtil geomUtil = new GeomUtil();

    public static Point createPoint(double lat, double lon) {
        GeometryFactory geometryFactory = new GeometryFactory();
        // Due to the MySQL Point : Point(longitude, latitude)
        // Point.getX() = longitude
        // Point.getY() = latitude
        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }

}
