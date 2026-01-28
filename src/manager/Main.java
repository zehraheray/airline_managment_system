package manager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


import model.*;
import view.MainFrame;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   UCUS REZERVASYON SISTEMI BAŞLATILIYOR");
        System.out.println("===========================================");

        FlightManager flightManager = FlightManager.getInstance();
        ReservationManager resManager = ReservationManager.getInstance();

        List<Flight> existingFlights = flightManager.getAllFlights();

        //initializeDummyData(flightManager);

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());//macosda renkli butonlar sorunlu oldugu icin
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[BILGI] GUI baslatiliyor...");
        
        SwingUtilities.invokeLater(() -> {
            MainFrame gui = new MainFrame();
            gui.setVisible(true);
        });
    }

    private static void initializeDummyData(FlightManager fm) {
        Plane boeing737 = new Plane("P-737", "Boeing 737");
        Plane airbusA320 = new Plane("P-320", "Airbus A320");

        // Ucus 1: Istanbul -> Ankara (Yarin)
        Route route1 = new Route("Istanbul", "Ankara");
        //Flight f = new Flight(data[0], plane, LocalDate.parse(data[3]), LocalTime.parse(data[4]), route, data[7], data[8], Integer.parseInt(data[9]), );
                
        Flight f1 = new Flight("TK-101", boeing737, LocalDate.now().plusDays(1), 
                               LocalTime.of(10, 30), route1, route1.getDepature(), route1.getArrival(), 60);

        // Ucus 2: Istanbul -> Izmir (3 Gun Sonra)
        Route route2 = new Route("Istanbul", "Izmir");
        Flight f2 = new Flight("TK-202", airbusA320, LocalDate.now().plusDays(3), 
                               LocalTime.of(14, 0), route2,route2.getDepature() ,route2.getArrival(),45 );

        // Ucus 3: Ankara -> Antalya (Bugun - Aksam)
        Route route3 = new Route("Ankara", "Antalya");
        Flight f3 = new Flight("AJet-06", boeing737, LocalDate.now(), 
                               LocalTime.of(20, 0), route3,route3.getDepature(),route3.getArrival() ,50);

        fm.createFlight(f1);
        fm.createFlight(f2);
        fm.createFlight(f3);

        System.out.println("[BASARILI] 3 adet ornek ucus 'flights.txt' dosyasina kaydedildi.");
    }
}