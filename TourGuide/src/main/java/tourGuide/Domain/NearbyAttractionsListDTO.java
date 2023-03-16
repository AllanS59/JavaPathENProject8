package tourGuide.Domain;

import java.util.List;
import gpsUtil.location.VisitedLocation;

public class NearbyAttractionsListDTO {

    private VisitedLocation userLocation;
    private List<NearbyAttractionDTO> attractions;


    public VisitedLocation getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(VisitedLocation userLocation) {
        this.userLocation = userLocation;
    }

    public List<NearbyAttractionDTO> getAttractions() {
        return attractions;
    }

    public void setAttractions(List<NearbyAttractionDTO> attractions) {
        this.attractions = attractions;
    }
}
