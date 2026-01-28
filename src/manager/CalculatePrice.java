package manager;

import model.Seat.SeatClass;

public class CalculatePrice {

    public static final double BUSINESS_PRICE = 1000.0;
    public static final double ECONOMY_PRICE = 450.0;


    public static final int BUSINESS_BAGGAGE_LIMIT = 20; 
    public static final int ECONOMY_BAGGAGE_LIMIT = 15; 
    public static final double EXTRA_BAGGAGE_FEE = 50.0; 


    public double calculateTotalPrice(SeatClass seatClass, int baggageKg) {
        if (seatClass == null) {
            throw new IllegalArgumentException("Koltuk tipi bos olamaz!");
        }

        double basePrice;
        int baggageLimit;

        switch (seatClass) {
            case BUSINESS:
                basePrice = BUSINESS_PRICE;
                baggageLimit = BUSINESS_BAGGAGE_LIMIT;
                break;
            case ECONOMY:
                basePrice = ECONOMY_PRICE;
                baggageLimit = ECONOMY_BAGGAGE_LIMIT;
                break;
            default:
                throw new UnsupportedOperationException("Tanimlanmamis koltuk tipi: " + seatClass);
        }

        double extraFee = 0.0;
        if (baggageKg > baggageLimit) {
            int extraWeight = baggageKg - baggageLimit;
            extraFee = extraWeight * EXTRA_BAGGAGE_FEE;
        }

        return basePrice + extraFee;
    }
    
    public double getBasePrice(SeatClass seatClass) {
        return (seatClass == SeatClass.BUSINESS) ? BUSINESS_PRICE : ECONOMY_PRICE;
    }
}