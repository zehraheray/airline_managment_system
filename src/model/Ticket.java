package model;

import manager.CalculatePrice; 
import model.Seat.SeatClass;

public class Ticket {
    private String ticketID;
    private double price;             
    private Reservation reservation;
    private double baggageAllowance;  
    private Baggage baggage;            
    
    public Ticket(String ticketID, Reservation reservation, Baggage baggage) {
        this.ticketID = ticketID;
        this.reservation = reservation;
        this.baggage = baggage;
        calculateDetails();
    }


    private void calculateDetails() {
        CalculatePrice calculator = new CalculatePrice();
        SeatClass seatClass = this.reservation.getSeat().getSeatClass();
        
        int currentWeight = (int) this.baggage.getWeight(); 

        this.price = calculator.calculateTotalPrice(seatClass, currentWeight);

        if (seatClass == SeatClass.BUSINESS) {
            this.baggageAllowance = CalculatePrice.BUSINESS_BAGGAGE_LIMIT;
        } else {
            this.baggageAllowance = CalculatePrice.ECONOMY_BAGGAGE_LIMIT;
        }
    }


    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }

    public double getBaggageAllowance() {
        return baggageAllowance;
    }

    public void setBaggageAllowance(double baggageAllowance) {
        this.baggageAllowance = baggageAllowance;
    }

    public Baggage getBaggage() {
        return baggage;
    }

    public void setBaggage(Baggage baggage) {
        this.baggage = baggage;
        calculateDetails(); 
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        calculateDetails();
    }
}