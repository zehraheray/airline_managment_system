package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import manager.FlightManager;
import model.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

class FlightSearchTest {

    private FlightManager flightManager;
    private Flight validFlight;
    private Flight pastFlight;
    private Flight wrongRouteFlight;

    @BeforeEach
    void setUp() {
        flightManager =  flightManager.getInstance();
        Route istanbulAnkara = new Route("Istanbul", "Ankara");
        Plane plane = new Plane("PL-1", "A320");
        flightManager = FlightManager.getInstance();

        validFlight = new Flight("FL-1", plane, LocalDate.now().plusDays(1), LocalTime.of(10, 0), istanbulAnkara, "Istanbul", "Ankara",60);

        pastFlight = new Flight("FL-2", plane,LocalDate.now().minusDays(1), LocalTime.of(10, 0), istanbulAnkara,"Istanbul", "Ankara",60);
        
        Route istanbulIzmir = new Route("Istanbul", "Izmir");
        wrongRouteFlight = new Flight("FL-3", plane, LocalDate.now().plusDays(1), LocalTime.of(15, 0), istanbulIzmir,"Istanbul", "Izmir",60);

        List<Flight> allFlights = Arrays.asList(validFlight, pastFlight, wrongRouteFlight);
        for(Flight f:allFlights){
            flightManager.createFlight(f);
        }
    }

    @Test
    @DisplayName("Test: Sehir bazli filtreleme dogru calismali")
    void testFilterByCities() {

        List<Flight> results = flightManager.searchFlights("Istanbul", "Ankara");

        assertTrue(results.contains(validFlight), "Dogru rota listede olmali.");
        assertFalse(results.contains(wrongRouteFlight), "Yanlis varis sehri listede olmamali.");
    }

    @Test
    @DisplayName("Test: Zamani gecmis ucuslar elenmeli")
    void testEliminatePastFlights() {
        List<Flight> results = flightManager.searchFlights("Istanbul", "Ankara");

        assertFalse(results.contains(pastFlight), "Kalkis zamani gecmis ucuslar sonuclarda gorünmemeli.");
    }
}