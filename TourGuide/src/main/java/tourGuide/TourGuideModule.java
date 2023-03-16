package tourGuide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tripPricer.TripPricer;
import tourGuide.service.RewardsService;
import tourGuide.Domain.GpsUtilOptim;
import tourGuide.Domain.RewardCentralOptim;
import tourGuide.Domain.TripPricerOptim;

@Configuration
public class TourGuideModule {
	
	@Bean
	public GpsUtil getGpsUtil() {
		return new GpsUtilOptim();
	}
	
	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService(getGpsUtil(), getRewardCentral());
	}
	
	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentralOptim();
	}

	@Bean
	public TripPricer getTripPricer() {
		return new TripPricerOptim();
	}
	
}
