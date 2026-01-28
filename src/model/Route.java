package model;
public class Route {
    private String depature;
    private String arrival;
    public Route(String depature, String arrival) {
        this.depature = depature;
        this.arrival = arrival;
    }
    public String getDepature() {
        return depature;
    }
    public void setDepature(String depature) {
        this.depature = depature;
    }
    public String getArrival() {
        return arrival;
    }
    public void setArrival(String arrival) {
        this.arrival = arrival;
    }
    @Override
    public String toString() {
        return depature + "-" + arrival ;
    }

    
    
}
