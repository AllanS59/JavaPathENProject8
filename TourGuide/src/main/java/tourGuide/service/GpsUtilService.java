package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.TourGuideModule;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GpsUtilService {

    private GpsUtil gpsUtil;
    private TourGuideModule tourGuideModule = new TourGuideModule();
    public GpsUtilService() {
        gpsUtil = tourGuideModule.getGpsUtil();
    }

    private ExecutorService executor = Executors.newFixedThreadPool(10000);

    public List<Attraction> getAttractions() {
        return gpsUtil.getAttractions();
    }

    public VisitedLocation getUserLocation(UUID userId) throws ExecutionException, InterruptedException {
        CompletableFuture<VisitedLocation> future
                = CompletableFuture.supplyAsync(() -> gpsUtil.getUserLocation(userId));
       return future.get();
    }
}
