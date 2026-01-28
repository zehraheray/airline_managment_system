package manager;

import model.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String FLIGHT_FILE = "flights.txt";
    private static final String RESERVATION_FILE = "reservations.txt";
    private static final String STAFF_FILE = "staff.txt";

    public static void saveFlights(List<Flight> flights) {
        try (PrintWriter out = new PrintWriter(new FileWriter(FLIGHT_FILE))) {
            for (Flight f : flights) {
                // Code;PlaneId;PlaneModel;Date;Time;RouteDep;RouteArr;StrDep;StrArr;Duration
                out.println(f.getFlightNum() + ";" +
                        f.getPlane().getPlaneID() + ";" +
                        f.getPlane().getPlaneModel() + ";" +
                        f.getDate() + ";" +
                        f.getHour() + ";" +
                        f.getRoute().getDepature() + ";" +
                        f.getRoute().getArrival() + ";" +
                        f.getRoute().getDepature() + ";" +
                        f.getRoute().getArrival() + ";" +
                        f.getDuration());
            }
        } catch (IOException e) {
            System.err.println("Uçuşlar kaydedilirken hata: " + e.getMessage());
        }
    }

    public static List<Flight> loadFlights() {
        List<Flight> flights = new ArrayList<>();
        File file = new File(FLIGHT_FILE);
        if (!file.exists())
            return flights;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                String[] data = line.split(";");

                if (data.length >= 10) {
                    String code = data[0];
                    String planeId = data[1];
                    String planeModel = data[2];
                    LocalDate date = LocalDate.parse(data[3]);
                    LocalTime time = LocalTime.parse(data[4]);
                    String routeDep = data[5];
                    String routeArr = data[6];
                    int duration = Integer.parseInt(data[9]);

                    Plane plane = new Plane(planeId, planeModel);
                    Route route = new Route(routeDep, routeArr);

                    // new Flight(code, plane, date, time, route, depStr, arrStr, duration)
                    Flight f = new Flight(code, plane, date, time, route, routeDep, routeArr, duration);

                    flights.add(f);
                }
            }
        } catch (Exception e) {
            // Hata varsa
            System.err.println("Dosya okuma hatasi: " + e.getMessage());
            e.printStackTrace();
        }
        return flights;
    }

    public static void rewriteFlights(List<Flight> flights) {

        try (PrintWriter out = new PrintWriter(new FileWriter("flights.txt", false))) {

            for (Flight f : flights) {
                // flightNum, plane( planeID, planeModel), date, hour, route(dep, arr),
                // departurePlace, arrivalPlace, duration
                out.println(f.getFlightNum() + ";" + f.getPlane().getPlaneID() + ";" + f.getPlane().getPlaneModel()
                        + ";" + f.getDate() + ";" + f.getHour() + ";" + f.getRoute().getDepature() + ";"
                        + f.getRoute().getArrival() + ";" + f.getDeparturePlace() + ";" + f.getArrivalPlace() + ";"
                        + f.getDuration());
            }
            System.out.println("Ucus dosyasi guncellendi.");

        } catch (IOException e) {
            System.err.println("Ucuslar dosyaya yazilamadi: " + e.getMessage());
        }
    }


    public static List<Reservation> loadReservations(List<Flight> allFlights) {
        List<Reservation> loadedReservations = new ArrayList<>();
        File file = new File(RESERVATION_FILE);
        if (!file.exists())
            return loadedReservations;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");

                // Format: resCode;date;flightNum;seatNum;passengerId;baggage
                if (data.length >= 6) {
                    String resCode = data[0];
                    LocalDate date = LocalDate.parse(data[1]);
                    String flightNum = data[2];
                    String seatNum = data[3];
                    String id = data[4];
                    int weight = Integer.parseInt(data[5]);

                    // Ucusu bul
                    for (Flight f : allFlights) {
                        if (f.getFlightNum().equalsIgnoreCase(flightNum)) {

                            Seat[][] matrix = f.getPlane().getSeatMatrix();
                            for (int i = 0; i < matrix.length; i++) {
                                for (int j = 0; j < matrix[0].length; j++) {
                                    if (matrix[i][j] != null && matrix[i][j].getSeatNum().equalsIgnoreCase(seatNum)) {

                                        // Koltugu doldur
                                        matrix[i][j].setReserveStatus(true);

                                        Passenger p = new Passenger(id, "Yolcu", "Kayitli", "-");
                                        Reservation res = new Reservation(resCode, date, f, matrix[i][j], p, weight);

                                        loadedReservations.add(res);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Yukleme hatasi: " + e.getMessage());
        }
        return loadedReservations;
    }
    /* 
    public static void syncSeatOccupancy(List<Flight> allFlights) {
        File file = new File(RESERVATION_FILE);
        if (!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length >= 4) {
                    // resCode(0);date(1);flightNum(2);seatNum(3)...
                    String flightNum = data[2];
                    String seatNum = data[3];

                    for (Flight flight : allFlights) {
                        if (flight.getFlightNum().equals(flightNum)) {
                            markSeatAsOccupied(flight, seatNum);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Doluluk bilgisi senkronize edilemedi.");
        }
    }

    private static void markSeatAsOccupied(Flight flight, String seatNum) {
        Seat[][] matrix = flight.getPlane().getSeatMatrix();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != null && matrix[i][j].getSeatNum().equals(seatNum)) {
                    matrix[i][j].setReserveStatus(true);
                    return;
                }
            }
        }
    }

    */

    public static void rewriteReservations(List<Reservation> reservations) {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESERVATION_FILE))) {
            for (Reservation r : reservations) {
                // Format: resCode;date;flightNum;seatNum;passengerId;baggage
                out.println(r.getReservationCode() + ";" +
                        r.getDateOfReservation() + ";" +
                        r.getFlight().getFlightNum() + ";" +
                        r.getSeat().getSeatNum() + ";" +
                        r.getPassenger().getId() + ";" +
                        r.getBaggage());
            }
        } catch (IOException e) {
            System.err.println("Rezervasyonlar kaydedilemedi.");
        }
    }

    public static void saveReservation(Reservation res) {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESERVATION_FILE, true))) { // true => append mode
            // Format: resCode;date;flightNum;seatNum;passengerId;baggage
            out.println(res.getReservationCode() + ";" +
                    res.getDateOfReservation() + ";" +
                    res.getFlight().getFlightNum() + ";" +
                    res.getSeat().getSeatNum() + ";" +
                    res.getPassenger().getId() + ";" +
                    res.getBaggage());
        } catch (IOException e) {
            System.err.println("Dosyaya ekleme hatasi: " + e.getMessage());
        }
    }


    public static void saveStaff(List<Staff> staffList) {
        try (PrintWriter out = new PrintWriter(new FileWriter(STAFF_FILE))) {
            for (Staff s : staffList) {
                // Format: id;name;surname;contactInfo
                out.println(s.getId() + ";" + s.getName() + ";" + s.getSurname() + ";" + s.getContactInfo());
            }
        } catch (IOException e) {
            System.err.println("Personel kaydedilemedi: " + e.getMessage());
        }
    }

    public static List<Staff> loadStaff() {
        List<Staff> staffList = new ArrayList<>();
        File file = new File(STAFF_FILE);
        if (!file.exists())
            return staffList;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length >= 4) {
                    staffList.add(new Staff(data[0], data[1], data[2], data[3]));
                }
            }
        } catch (IOException e) {
            System.err.println("Personel yuklenirken hata: " + e.getMessage());
        }
        return staffList;
    }

}