package tourGuide.domain;
import gpsUtil.GpsUtil;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


public class GpsUtilOptim extends GpsUtil {

    @Override
    public VisitedLocation getUserLocation(UUID userId) {
        double longitude = ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D);
        longitude = Double.parseDouble(String.format(Locale.ROOT,"%.6f", longitude));
        double latitude = ThreadLocalRandom.current().nextDouble(-85.05112878D, 85.05112878D);
        latitude = Double.parseDouble(String.format(Locale.ROOT,"%.6f", latitude));
        VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(latitude, longitude), new Date());
        return visitedLocation;
    }
}
