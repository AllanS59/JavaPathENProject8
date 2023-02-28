package tourGuide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//import gpsUtil.GpsUtil;
import tourGuide.Domain.GpsUtilOptim;
//import rewardCentral.RewardCentral;
import tourGuide.Domain.RewardCentralOptim;
import tourGuide.service.RewardsService;

@Configuration
public class TourGuideModule {
	
	@Bean
	public GpsUtilOptim getGpsUtil() {
		return new GpsUtilOptim();
	}
	
	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService(getGpsUtil(), getRewardCentral());
	}
	
	@Bean
	public RewardCentralOptim getRewardCentral() {
		return new RewardCentralOptim();
	}
	
}
