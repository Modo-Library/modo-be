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
        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }

    public static double calculateDistance(Point point, double lat2, double lon2) {

        double lat1 = point.getY();
        double lon1 = point.getX();

        log.info(lat1);
        log.info(lon1);

        log.info(lat2);
        log.info(lon2);

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return dist * 1609.344;
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
