package model;
public class Seat {

    public enum SeatClass {
        ECONOMY,
        BUSINESS
    }
    
    private String seatNum;
    private SeatClass seatClass; 
    private double price;
    private boolean reserveStatus;

    

    
    public Seat(String seatNum, SeatClass seatClass, boolean reserveStatus) {
        this.seatNum = seatNum;
        this.seatClass = seatClass;
        this.reserveStatus = reserveStatus;

        if (seatClass == SeatClass.BUSINESS) { 
            this.price = 1000.0;
        } else {
            this.price = 450.0;
        }
    }

    
    public String getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(String seatNum) {
        this.seatNum = seatNum;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(SeatClass seatClass) {
        this.seatClass = seatClass;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isReserveStatus() {
        return reserveStatus;
    }

    public void setReserveStatus(boolean reserveStatus) {
        this.reserveStatus = reserveStatus;
    }
}
