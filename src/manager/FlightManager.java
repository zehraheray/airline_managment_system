package manager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.*;

public class FlightManager {
    private static FlightManager instance;
    private List<Flight> flights;

    private FlightManager() {
        this.flights = FileManager.loadFlights();
        if (this.flights == null) {
            this.flights = new ArrayList<>();
        }
    }

    public static synchronized FlightManager getInstance() {
        if (instance == null) {
            instance = new FlightManager();
        }
        return instance;
    }


    public void createFlight(Flight flight) {
        flights.add(flight);
        FileManager.saveFlights(flights);
        //System.out.println("Ucus sisteme eklendi: " + flight.getFlightNum());
    }

   
    public void updateFlight(String flightNum, LocalDate date, LocalTime hour, String dep, String arr, int duration) {
    for (Flight f : flights) {
        if (f.getFlightNum().equals(flightNum)) {
            // Bilgileri guncelliyoruz
            f.setDate(date);
            f.setHour(hour);
            f.setRoute(new Route(dep, arr));
            f.setDeparturePlace(dep);
            f.setArrivalPlace(arr);
            f.setDuration(duration);

            FileManager.rewriteFlights(flights);
            System.out.println(flightNum + " ucus bilgileri basariyla guncellendi.");
            return;
            }
        }
    }


    public synchronized void deleteFlight(String flightNum) {
        Flight toRemove = null;

        for (Flight f : flights) {
            if (f.getFlightNum().equals(flightNum)) {
                toRemove = f;
                break;
            }
        }

        if (toRemove != null) {
            if(ReservationManager.getInstance().findReservation(flightNum)!=null){
                ReservationManager.getInstance().cancelReservationsByFlight(flightNum);
            }
            flights.remove(toRemove);
            FileManager.rewriteFlights(flights);
            
            System.out.println(flightNum + " basariyla silindi.");
        } else {
            System.out.println("Hata: " + flightNum + " bulunamadi.");
        }
    }

    //sadece ileri tarihteki ucaklar
    public List<Flight> searchFlights(String departureCity, String arrivalCity) {
        LocalDateTime now = LocalDateTime.now();

        return flights.stream()
            .filter(f -> f.getRoute().getDepature().equals(departureCity))
            .filter(f -> f.getRoute().getArrival().equals(arrivalCity))
            .filter(f -> LocalDateTime.of(f.getDate(), f.getHour()).isAfter(now))
            .collect(Collectors.toList());
    }

    public List<Flight> getAllFlights() {
        return flights;
    }
}