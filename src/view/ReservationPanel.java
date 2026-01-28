package view;

import javax.swing.*;
import java.awt.*;
import manager.ReservationManager;
import model.Reservation;
import manager.CalculatePrice;

public class ReservationPanel extends JPanel {
    private MainFrame frame;
    private ReservationManager reservationManager;

    private JTextField txtResCode;
    private JTextArea txtDetails;
    private JButton btnCancel;
    private Reservation currentFoundReservation;

    public ReservationPanel(MainFrame frame) {
        this.frame = frame;
        this.reservationManager = ReservationManager.getInstance();
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // arama kismi
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtResCode = new JTextField(15);
        JButton btnQuery = new JButton("Rezervasyon Sorgula");

        searchPanel.add(new JLabel("Bilet/Rezervasyon Kodu:"));
        searchPanel.add(txtResCode);
        searchPanel.add(btnQuery);

        add(searchPanel, BorderLayout.NORTH);

        // rezervasyon detaylarini goruntuleme
        txtDetails = new JTextArea();
        txtDetails.setEditable(false);
        txtDetails.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtDetails.setBorder(BorderFactory.createTitledBorder("Rezervasyon Bilgileri"));

        add(new JScrollPane(txtDetails), BorderLayout.CENTER);

        // iptal islemi
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCancel = new JButton("Rezervasyonu Iptal Et");
        btnCancel.setEnabled(false); // Sorgulama yapmadan iptal butonu aktif olmasin
        btnCancel.setBackground(Color.RED);
        btnCancel.setForeground(Color.WHITE);

        btnCancel.setOpaque(true);
        btnCancel.setBorderPainted(false);

        JButton btnBack = new JButton("Geri Don");

        actionPanel.add(btnBack);
        actionPanel.add(btnCancel);
        add(actionPanel, BorderLayout.SOUTH);

        // Sorgulama butonu
        btnQuery.addActionListener(e -> {
            String code = txtResCode.getText().trim();
            queryReservation(code);
        });

        // Iptal butonu
        btnCancel.addActionListener(e -> {
            if (currentFoundReservation == null)
                return;

            int confirm = JOptionPane.showConfirmDialog(this,
                    currentFoundReservation.getReservationCode()
                            + " kodlu rezervasyonu iptal etmek istediğinize emin misiniz?",
                    "Iptal Onayi", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Manager uzerinden iptal et
                reservationManager.cancelReservation(currentFoundReservation.getReservationCode());
                txtDetails.setText("Rezervasyon Basariyla Iptal Edildi.");
                // Ekrani temizle 
                btnCancel.setEnabled(false);
                currentFoundReservation = null;
                txtResCode.setText(""); 
            }
        });

        // Booking panele geri donus
        btnBack.addActionListener(e -> frame.showScreen("BOOKING"));
    }


    private void queryReservation(String code) {
        // Bos giris kontrolu
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lutfen bir rezervasyon kodu giriniz.");
            return;
        }

        // Gercek veriyi reservation managerden cekme
        currentFoundReservation = reservationManager.findReservation(code);
        CalculatePrice calculator = new CalculatePrice();
        if (currentFoundReservation != null) {

            // Bilgileri duzenli hale getirmek icin string builder
            StringBuilder info = new StringBuilder();
            info.append("=== REZERVASYON DETAYLARI ===\n\n");
            info.append("Rezervasyon Kodu : ").append(currentFoundReservation.getReservationCode()).append("\n");
            info.append("Islem Tarihi     : ").append(currentFoundReservation.getDateOfReservation()).append("\n");
            info.append("-----------------------------\n");
            info.append("Yolcu Id         : ").append(currentFoundReservation.getPassenger().getId()).append("\n");
            info.append("Yolcu Adi        : ").append(currentFoundReservation.getPassenger().getName()).append("\n");
            info.append("Yolcu Soyadi      : ").append(currentFoundReservation.getPassenger().getSurname())
                    .append("\n");
            info.append("Yolcu Iletisim Bilgisi: ").append(currentFoundReservation.getPassenger().getContactInfo())
                    .append("\n");
            info.append("-----------------------------\n");
            info.append("Ucus No          : ").append(currentFoundReservation.getFlight().getFlightNum()).append("\n");
            info.append("Rota             : ").append(currentFoundReservation.getFlight().getRoute().getDepature())
                    .append(" -> ").append(currentFoundReservation.getFlight().getRoute().getArrival()).append("\n");
            info.append("Tarih/Saat       : ").append(currentFoundReservation.getFlight().getDate())
                    .append(" | ").append(currentFoundReservation.getFlight().getHour()).append("\n");
            info.append("-----------------------------\n");
            info.append("Koltuk No        : ").append(currentFoundReservation.getSeat().getSeatNum()).append("\n");
            info.append("Sinif            : ").append(currentFoundReservation.getSeat().getSeatClass()).append("\n");
            info.append("Bagaj            : ").append(currentFoundReservation.getBaggage()).append(" kg\n");
            info.append("Odenen Tutari    : ").append(calculator.calculateTotalPrice(currentFoundReservation.getSeat().getSeatClass(), currentFoundReservation.getBaggage() )).append(" TL\n");
            //reseravsyon bilgilerini ekrana yazdir
            txtDetails.setText(info.toString());

            // Iptal butonunu aktiflestir 
            btnCancel.setEnabled(true);

        } else {
            // gecerli rezervasyon bulunamazsa
            txtDetails.setText("Sistemde '" + code + "' koduna ait bir rezervasyon bulunamadi.");
            btnCancel.setEnabled(false); // iptal butonu inaktif
            currentFoundReservation = null; 
        }
    }
}