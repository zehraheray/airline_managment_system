package model;

import java.time.LocalDate;
import java.time.LocalTime;


public class Flight {

    private String flightNum;
    
    private Plane plane; 
    private LocalDate date;
    private LocalTime hour;
    private String departurePlace;
    private String arrivalPlace;
    private Route route;
    private int duration;


    public Flight(String flightNum,Plane plane, LocalDate date, LocalTime hour,  Route route, String departurePlace, String arrivalPlace,int duration) {
        this.flightNum = flightNum;
        this.route = route;
        this.plane = plane;
        this.date = date;
        this.hour = hour;
        this.arrivalPlace = arrivalPlace;
        this.departurePlace = departurePlace;
        this.duration = duration;
    }



    public String getFlightNum() {
        return flightNum;
    }


    public void setFlightNum(String flightNum) {
        this.flightNum = flightNum;
    }


    public String getDeparturePlace() {
        return departurePlace;
    }


    public void setDeparturePlace(String departurePlace) {
        this.departurePlace = departurePlace;
    }


    public String getArrivalPlace() {
        return arrivalPlace;
    }


    public void setArrivalPlace(String arrivalPlace) {
        this.arrivalPlace = arrivalPlace;
    }


    public Route getRoute() {
        return route;
    }


    public void setRoute(Route route) {
        this.route = route;
    }


    public Plane getPlane() {
        return plane;
    }


    public void setPlane(Plane plane) {
        this.plane = plane;
    }


    public LocalDate getDate() {
        return date;
    }


    public void setDate(LocalDate date) {
        this.date = date;
    }


    public LocalTime getHour() {
        return hour;
    }


    public void setHour(LocalTime hour) {
        this.hour = hour;
    }


    public int getDuration() {
        return duration;
    }


    public void setDuration(int duration) {
        this.duration = duration;
    }

    
    
}


    

