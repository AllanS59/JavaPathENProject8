package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.domain.NearbyAttractionDTO;
import tourGuide.domain.NearbyAttractionsListDTO;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;
import tourGuide.TourGuideModule;


@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private GpsUtilService gpsUtilService;
	private RewardCentral rewardCentral;
	private final RewardsService rewardsService;
	private final TourGuideModule tourGuideModule = new TourGuideModule();
	private final TripPricer tripPricer = tourGuideModule.getTripPricer();
	public final Tracker tracker;
	boolean testMode = true;

	public TourGuideService(GpsUtilService gpsUtilService, RewardsService rewardsService) {
		this.gpsUtilService = gpsUtilService;
		this.rewardsService = rewardsService;

		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public VisitedLocation getUserLocation(User user) throws ExecutionException, InterruptedException {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
		return visitedLocation;
	}

	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}

	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	public VisitedLocation trackUserLocation(User user) throws ExecutionException, InterruptedException {
		VisitedLocation visitedLocation = gpsUtilService.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		List<Attraction> nearbyAttractions = new ArrayList<>();
		Map<Attraction, Double > map = new HashMap<>();

		for(Attraction attraction : gpsUtilService.getAttractions()) {
			map.put(attraction, rewardsService.getDistance(attraction, visitedLocation.location));
		}
		Map<Attraction, Double> mapSortedByDistance = map.entrySet().stream()
				.sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		mapSortedByDistance.forEach((k,v)->{
			if (nearbyAttractions.size()< 5){
				nearbyAttractions.add(k);
			}
		});

		return nearbyAttractions;
	}


	public NearbyAttractionsListDTO getNearByAttractionsWithInfos (String userName) throws ExecutionException, InterruptedException {

		NearbyAttractionsListDTO nearbyAttractionsListDTO = new NearbyAttractionsListDTO();

		VisitedLocation visitedLocation = getUserLocation(getUser(userName));
		nearbyAttractionsListDTO.setUserLocation(visitedLocation);

		List<NearbyAttractionDTO> listAttractionsDTO  =  new ArrayList();
		List<Attraction> listAttractions = getNearByAttractions(visitedLocation);
		listAttractions.forEach(attraction -> {
			NearbyAttractionDTO attractionToAdd = new NearbyAttractionDTO();
			attractionToAdd.setAttractionName(attraction.attractionName);
			attractionToAdd.setAttractionPosition(new Location(attraction.latitude,attraction.longitude));
			attractionToAdd.setDistanceFromUser(rewardsService.getDistance(attraction, visitedLocation.location));
			attractionToAdd.setRewardPoints(rewardCentral.getAttractionRewardPoints(attraction.attractionId, getUser(userName).getUserId()));
			listAttractionsDTO.add(attractionToAdd);
		});
		nearbyAttractionsListDTO.setAttractions(listAttractionsDTO);

		return nearbyAttractionsListDTO;
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		      public void run() {
		        tracker.stopTracking();
		      }
		    });
	}



	public Map<UUID, VisitedLocation> getAllCurrentLocations (){

		Map<UUID, VisitedLocation> mapAllCurrentLocations = new HashMap<>();


		List<User> allUsers = getAllUsers();
		allUsers.forEach(user -> mapAllCurrentLocations.put(user.getUserId(), user.getLastVisitedLocation()));

		return mapAllCurrentLocations;
	}


	/**********************************************************************************
	 *
	 * Methods Below: For Internal Testing
	 *
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();
	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);

			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}

	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}

	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

}
