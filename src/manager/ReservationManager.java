package manager;

import model.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDate;

public class ReservationManager {

    private static ReservationManager instance;

    private List<Reservation> reservations;

    private ReservationManager() {
        this.reservations = Collections.synchronizedList(new ArrayList<>());//thread safe list
        List<Flight> allFlights = FlightManager.getInstance().getAllFlights();
        List<Reservation> loaded = FileManager.loadReservations(allFlights);
        this.reservations.addAll(loaded);
    
        System.out.println("[SISTEM] " + loaded.size() + " adet eski rezervasyon yüklendi.");
    }

    public static synchronized ReservationManager getInstance() {
        if (instance == null) {
            instance = new ReservationManager();
        }
        return instance;
    }

    public synchronized void makeReservation(String code, Flight flight, Seat seat, Passenger passenger,int bagage) {

        if (seat.isReserveStatus()) {
            System.out.println("Hata: " + passenger.getName() + " maalesef " + seat.getSeatNum()
                    + " koltuk coktan rezerve edildi!");
            return;
        }

        Reservation newReservation = new Reservation(code, LocalDate.now(), flight, seat, passenger, bagage);
        seat.setReserveStatus(true);
        reservations.add(newReservation);

        FileManager.saveReservation(newReservation);
    }

    public synchronized void cancelReservation(String reservationCode) {

        Reservation toRemove = null;
        for (Reservation r : reservations) {
            if (r.getReservationCode().equals(reservationCode)) {
                toRemove = r;
                break;
            }
        }
        if (toRemove != null) {

            FlightManager flightManager = FlightManager.getInstance();

            for (Flight realFlight : flightManager.getAllFlights()) {
                if (realFlight.getFlightNum().equalsIgnoreCase(toRemove.getFlight().getFlightNum())) {
                
                // Koltuk matrisinde koltugu bul
                    Seat[][] matrix = realFlight.getPlane().getSeatMatrix();
                    String targetSeatNum = toRemove.getSeat().getSeatNum();
                
                    for (int i = 0; i < matrix.length; i++) {
                        for (int j = 0; j < matrix[0].length; j++) {
                            if (matrix[i][j] != null && matrix[i][j].getSeatNum().equalsIgnoreCase(targetSeatNum)) {
                                // Koltugu bosa cikar
                                matrix[i][j].setReserveStatus(false);
                            }
                        }
                    }
                }
            }
            toRemove.getSeat().setReserveStatus(false);
            reservations.remove(toRemove);

            FileManager.rewriteReservations(reservations);
            System.out.println("Bilgi: " + reservationCode + " iptal edildi.");

        } else {
            System.out.println("Hata: Rezervasyon bulunamadi.");
        }
    }

    public synchronized Reservation findReservation(String code) {
    if (code == null || code.isEmpty()) {
        return null;
    }

    for (Reservation r : reservations) {

        if (r.getReservationCode().equals(code)) {
            return r;
        }
    }
    return null; 
}

public synchronized void cancelReservationsByFlight(String flightNum) {
    List<Reservation> toRemove = new ArrayList<>();

    //  O ucusa ait rezervasyonları bul
    for (Reservation r : reservations) {
        if (r.getFlight().getFlightNum().equalsIgnoreCase(flightNum)) {
            toRemove.add(r);
        }
    }

    // Listeden sil ve dosyayi guncelle
    if (!toRemove.isEmpty()) {
        reservations.removeAll(toRemove); // Listeden toplu silme
        FileManager.rewriteReservations(reservations); // Dosyayi guncelle
        System.out.println("Bilgi: " + flightNum + " uçuşuna ait " + toRemove.size() + " adet rezervasyon otomatik silindi.");
    }
}

}