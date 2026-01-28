package model;
import java.time.LocalDate;


public class Reservation {
    private String reservationCode;
    private LocalDate dateOfReservation;
    private Flight flight;
    private Seat seat;
    private Passenger passenger;
    private int baggage;
    
    public Reservation(String reservationCode, LocalDate dateOfReservation, Flight flight, Seat seat, Passenger passenger, int baggage) {
        this.reservationCode = reservationCode;
        this.dateOfReservation = dateOfReservation;
        this.flight = flight;
        this.seat = seat;
        this.passenger = passenger;
        this.baggage = baggage;
    }

 
    public String getReservationCode() { return reservationCode; }
    public void setReservationCode(String reservationCode) { this.reservationCode = reservationCode; }

    public LocalDate getDateOfReservation() { return dateOfReservation; }
    public void setDateOfReservation(LocalDate dateOfReservation) { this.dateOfReservation = dateOfReservation; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }

    public Seat getSeat() { return seat; }
    public void setSeat(Seat seat) { this.seat = seat; }

    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }

    public int getBaggage() { return baggage; }
    public void setBaggage(int baggage) { this.baggage = baggage;}
    

    
}
