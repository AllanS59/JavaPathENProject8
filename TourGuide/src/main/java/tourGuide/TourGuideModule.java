package tourGuide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tripPricer.TripPricer;
import tourGuide.domain.GpsUtilOptim;
import tourGuide.domain.RewardCentralOptim;
import tourGuide.domain.TripPricerOptim;

@Configuration
public class TourGuideModule {

	//GpsUtilService gpsUtilService = new GpsUtilService();

	@Bean
	public GpsUtil getGpsUtil() {
		return new GpsUtilOptim();
	}
	
	//@Bean
	//public RewardsService getRewardsService() {
	//	return new RewardsService(getRewardCentral(), gpsUtilService);
	//}
	
	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentralOptim();
	}

	@Bean
	public TripPricer getTripPricer() {
		return new TripPricerOptim();
	}
	
}
