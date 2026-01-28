package manager;

import java.util.ArrayList;
import java.util.List;

import model.*;

public class SeatManager {
    private List<Seat> seats;

    public SeatManager() {
        this.seats = new ArrayList<>();
    }

    public void createSeats() {

        for (int i = 0; i < 5; i++) {
            char rowLetter = (char) ('A' + i);
            for (int col = 1; col <= 6; col++) {
                String seatNum = "" + rowLetter + col;
                seats.add(new Seat(seatNum, Seat.SeatClass.BUSINESS, false));
            }
        }

        for (int i = 5; i < 30; i++) {
            char rowLetter = (char) ('A' + i);
            for (int col = 1; col <= 6; col++) {
                String seatNum = "" + rowLetter + col;
                seats.add(new Seat(seatNum, Seat.SeatClass.ECONOMY, false));
            }
        }

        System.out.println("5x6 business ve 25x6 economy koltuklar basariyla oluşturuldu.");
    }

    public int getAvailableSeatCount() {
        int count = 0;
        for (Seat seat : seats) {
            if (!seat.isReserveStatus()) {
                count++;
            }
        }
        return count;
    }

    public void reserveSeat(String seatNum) {
        for (Seat seat : seats) {
            if (seat.getSeatNum().equals(seatNum)) {
                if (!seat.isReserveStatus()) {
                    seat.setReserveStatus(true);
                    System.out.println(seatNum + " numarali koltuk rezerve edildi.");
                } else {
                    System.out.println(seatNum + " zaten rezerve edilmis!");
                }
                return;
            }
        }

        throw new IllegalArgumentException("Hata: " + seatNum + " numarali koltuk sistemde mevcut degil.");
    }
}