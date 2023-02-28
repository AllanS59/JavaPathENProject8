package tourGuide.Domain;

import rewardCentral.RewardCentral;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RewardCentralOptim extends RewardCentral {

    public int getAttractionRewardPoints(UUID attractionId, UUID userId) {

        int randomInt = ThreadLocalRandom.current().nextInt(1, 1000);
        return randomInt;

    }
}
