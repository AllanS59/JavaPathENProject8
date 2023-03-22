package tourGuide;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

public class TestRewardsService {

	@Test
	public void userGetRewards() throws ExecutionException, InterruptedException {
		TourGuideModule tourGuideModule = new TourGuideModule();
		GpsUtilService gpsUtilService = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(tourGuideModule.getRewardCentral(), gpsUtilService);

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtilService.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		tourGuideService.tracker.stopTracking();
		assertTrue(userRewards.size() == 1);
	}
	
	@Test
	public void isWithinAttractionProximity() {
		TourGuideModule tourGuideModule = new TourGuideModule();
		GpsUtilService gpsUtilService = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(tourGuideModule.getRewardCentral(), gpsUtilService);

		Attraction attraction = gpsUtilService.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}

	@Test
	public void nearAllAttractions() {
		TourGuideModule tourGuideModule = new TourGuideModule();
		GpsUtilService gpsUtilService = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(tourGuideModule.getRewardCentral(), gpsUtilService);

		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);
		
		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
		tourGuideService.tracker.stopTracking();

		assertEquals(gpsUtilService.getAttractions().size(), userRewards.size());
	}
	
}
