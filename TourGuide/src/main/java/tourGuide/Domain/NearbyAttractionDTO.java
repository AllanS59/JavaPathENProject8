package tourGuide.Domain;
import gpsUtil.location.Location;

public class NearbyAttractionDTO {

    private String attractionName;
    private Location attractionPosition;
    private double distanceFromUser;
    private int rewardPoints;


    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public Location getAttractionPosition() {
        return attractionPosition;
    }

    public void setAttractionPosition(Location attractionPosition) {
        this.attractionPosition = attractionPosition;
    }

    public double getDistanceFromUser() {
        return distanceFromUser;
    }

    public void setDistanceFromUser(double distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }


}
