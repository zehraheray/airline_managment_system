package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import manager.*;
import model.*;

public class BookingPanel extends JPanel {
    private MainFrame frame;
    private FlightManager flightManager;
    private ReservationManager reservationManager;
    private JTable flightTable;
    private DefaultTableModel tableModel;
    private JPanel seatGridPanel;
    private List<Flight> currentFlightList;
    private Flight selectedFlight;

    private JTextField txtDep;
    private JTextField txtArr;

    public BookingPanel(MainFrame frame) {
        this.frame = frame;
        this.flightManager = FlightManager.getInstance();
        this.reservationManager = ReservationManager.getInstance();
        this.currentFlightList = new ArrayList<>();
        this.reservationManager = ReservationManager.getInstance();
        this.txtDep = new JTextField(10);
        this.txtArr = new JTextField(10);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));

        // navigasyon paneli
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnMyReservations = new JButton("Bilet Sorgula / Iptal Et");

        JButton btnSim = new JButton("Eszamanli Koltuk Rezervasyonu");
        btnSim.setBackground(new Color(173, 216, 230));
        btnSim.addActionListener(e -> frame.showScreen("SIMULATION"));
        // rezervasyona gecis
        btnMyReservations.addActionListener(e -> {
            frame.showScreen("RESERVATION");
        });

        navPanel.add(btnSim);
        navPanel.add(btnMyReservations);

        // arama
        JPanel searchPanel = new JPanel(new FlowLayout());
        // JTextField txtDep = new JTextField(10);
        // JTextField txtArr = new JTextField(10);
        JButton btnSearch = new JButton("Ucus Ara");

        searchPanel.add(new JLabel("Nereden:"));
        searchPanel.add(txtDep);
        searchPanel.add(new JLabel("Nereye:"));
        searchPanel.add(txtArr);
        searchPanel.add(btnSearch);

        topPanel.add(navPanel);
        topPanel.add(searchPanel);

        add(topPanel, BorderLayout.NORTH);

        // ucuslarin tablosu
        String[] columns = { "Ucus No", "Tarih", "Saat", "Rota" };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(flightTable);
        tableScroll.setPreferredSize(new Dimension(400, 200));

        add(tableScroll, BorderLayout.WEST);

        // koltuk duzeni
        seatGridPanel = new JPanel();
        seatGridPanel.setBorder(BorderFactory.createTitledBorder("Koltuk Secimi"));
        JScrollPane seatScroll = new JScrollPane(seatGridPanel);
        add(seatScroll, BorderLayout.CENTER);

        // -----buton ve tablolar------

        // ucus filtreleme
        btnSearch.addActionListener(e -> {
            String dep = txtDep.getText().trim();
            String arr = txtArr.getText().trim();

            List<Flight> filteredList = flightManager.searchFlights(dep, arr);
            if(filteredList.isEmpty()){
                JOptionPane.showMessageDialog(this, "Aradiginiz kriterlere uygun ucus bulunamadi.","Sonuc Yok", JOptionPane.INFORMATION_MESSAGE);
            }
            // Bulunanlari tabloya ve hafizaya yaz
            updateFlightTable(filteredList);
        });

        // Tablodan ucus secimi
        flightTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && flightTable.getSelectedRow() != -1) {
                int selectedRow = flightTable.getSelectedRow();

                selectedFlight = currentFlightList.get(selectedRow);

                displaySeatGrid(selectedFlight);
            }
        });
        // Ana menuye geri donus
        JButton btnBack = new JButton("Ana Menuye Don");
        btnBack.addActionListener(e -> frame.showScreen("LOGIN"));
        add(btnBack, BorderLayout.SOUTH);

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                // ekrani freshlemek icin
                resetPanel();
                System.out.println("Booking ekrani sifirlandi.");
            }
        });

    }

    private void updateFlightTable(List<Flight> list) {
        this.currentFlightList = list;

        tableModel.setRowCount(0);
        for (Flight f : list) {
            tableModel.addRow(new String[] { f.getFlightNum(), f.getDate().toString(), f.getHour().toString(),
                    f.getRoute().toString() });
        }
        seatGridPanel.removeAll();
        seatGridPanel.revalidate();
        seatGridPanel.repaint();
        selectedFlight = null;

    }

    // koltuk matrisi
    private void displaySeatGrid(Flight selectedFlight) {
        seatGridPanel.removeAll();
        Seat[][] matrix = selectedFlight.getPlane().getSeatMatrix();
        if (matrix == null)
            return;
        int rows = matrix.length;
        int cols = matrix[0].length;
        seatGridPanel.setLayout(new GridLayout(rows, cols, 5, 5));

        for (int i = 0; i < rows; i++) {
            char rowChar = (char) ('A' + i);
            for (int j = 0; j < cols; j++) {
                Seat seat = matrix[i][j];

                if (seat == null) {
                    seatGridPanel.add(new JPanel()); // Bosluk
                    continue;
                }
                JButton seatBtn = new JButton(seat.getSeatNum());
                seatBtn.setPreferredSize(new Dimension(50, 40));

                if (seat.isReserveStatus()) {
                    // Dolu koltuk icin kirmizi + islevsiz
                    seatBtn.setBackground(Color.RED);
                    seatBtn.setEnabled(false);
                } else {
                    // Sinifina gore renk
                    if (seat.getSeatClass() == Seat.SeatClass.BUSINESS) {
                        seatBtn.setBackground(Color.MAGENTA);
                    } else {
                        seatBtn.setBackground(Color.GREEN);
                    }

                    // tiklama islevi
                    seatBtn.addActionListener(e -> {
                        initiateBookingProcess(seat, seatBtn);
                    });
                }

                seatGridPanel.add(seatBtn);
            }
        }
        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    // koltuk rezerve etme
    private void initiateBookingProcess(Seat seat, JButton btn) {
        JTextField txtId = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtSurname = new JTextField();
        JTextField txtInfo = new JTextField();
        JTextField txtBagage = new JTextField();
        boolean isBusiness = seat.getSeatClass() == Seat.SeatClass.BUSINESS ? true : false;
        int basePrice = isBusiness ? 1000 : 450;
        int baggageLimit = isBusiness ? 20 : 15;

        Object[] message = {
                "Secilen Koltuk: " + seat.getSeatNum() + " (" + seat.getSeatClass() + ")",
                "Taban Fiyat: " + basePrice + " TL",
                "Bagaj Limiti: " + baggageLimit + " kg",
                "-------------------------",
                "Yolcu Id No:", txtId,
                "Yolcu Adi :", txtName,
                "Yolcu Soyadi:", txtSurname,
                "Yolcu Iletisim Bilgisi:", txtInfo,
                "Bagaj (kg):", txtBagage
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Rezervasyon Bilgileri",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                String id = txtId.getText().trim();
                String name = txtName.getText().trim();
                String surname = txtSurname.getText().trim();
                String info = txtInfo.getText().trim();
                String baggageText = txtBagage.getText().trim();

                // doldurulmasi zorunlu alanlar
                if (id.isEmpty() || name.isEmpty() || baggageText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lutfen zorunlu alanlari ve bagaj kilosunu giriniz!",
                            "Eksik Bilgi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                

                int baggage = Integer.parseInt(baggageText);

                if (baggage<0) {
                    JOptionPane.showMessageDialog(this, "Bagaj kilosu negatif olamaz!",
                            "Hatali Bilgi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int totalPrice = basePrice;

                if (baggage > baggageLimit) {
                    int extra = baggage - baggageLimit;
                    int extraFee = extra * 50;
                    totalPrice += extraFee;
                }
                // guncel tutar gorunteleme
                String confirmMsg = "Bilet Tutari: " + basePrice + " TL\n" +
                        "Bagaj (" + baggage + " kg): " + (totalPrice - basePrice) + " TL Ekstra\n" +
                        "--------------------------\n" +
                        "TOPLAM TUTAR: " + totalPrice + " TL\n\n" +
                        "Onayliyor musunuz?";

                int paymentOption = JOptionPane.showConfirmDialog(this, confirmMsg, "Odeme Onayi",
                        JOptionPane.YES_NO_OPTION);

                if (paymentOption == JOptionPane.YES_OPTION) {

                    Passenger passenger = new Passenger(id, name, surname, info);
                    // random rescode
                    String resCode = "PNR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

                    reservationManager.makeReservation(resCode, selectedFlight, seat, passenger, baggage);
                    JOptionPane.showMessageDialog(this, "Rezervasyon Basarili!\nKodunuz: " + resCode);

                    btn.setBackground(Color.RED);
                    btn.setEnabled(false);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lutfen bagaj kismina sadece sayi giriniz!", "Hatali Giris",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Bir hata olustu: " + ex.getMessage());
            }
        }
    }

    public void resetPanel() {
        // Arama kutularini temizle
        txtDep.setText("");
        txtArr.setText("");

        // Tabloyu ve hafizadaki listeyi temizle
        if (tableModel != null) {
            tableModel.setRowCount(0);
        }
        if (currentFlightList != null) {
            currentFlightList.clear();
        }

        // Koltuk panelini temizle
        if (seatGridPanel != null) {
            seatGridPanel.removeAll();
            seatGridPanel.revalidate();
            seatGridPanel.repaint();
        }

        // Secili ucusu sifirla
        selectedFlight = null;
    }

}