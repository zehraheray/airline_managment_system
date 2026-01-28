package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import manager.FileManager;
import manager.FlightManager;
import manager.StaffManager;
import model.Flight;
import model.Plane;
import model.Route;
import model.Seat;
import model.Staff;

public class AdminPanel extends JPanel {
    private MainFrame frame;
    private FlightManager flightManager;
    private StaffManager staffManager;

    private DefaultTableModel flightTableModel;
    private DefaultTableModel staffTableModel;
    private JTable flightTable;
    private JTable staffTable;

    public AdminPanel(MainFrame frame) {
        this.frame = frame;
        this.flightManager = FlightManager.getInstance();
        this.staffManager = StaffManager.getInstance();

        setLayout(new BorderLayout(10, 10));

        // sekmeler
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Ucus Yonetimi", createFlightManagementPanel());
        tabbedPane.addTab("Personel Yonetimi", createStaffManagementPanel());
        tabbedPane.addTab("Sistem Raporu", createReportPanel()); 

        add(tabbedPane, BorderLayout.CENTER);

        // geri don butonu
        JButton btnBack = new JButton("Ana Menuye Don");
        btnBack.setFont(new Font("Arial", Font.BOLD, 12));
        btnBack.addActionListener(e -> frame.showScreen("LOGIN"));
        add(btnBack, BorderLayout.SOUTH);

        // panel her gorunduğunde tablolari refresh
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshFlightTable();
                refreshStaffTable();
            }
        });
    }

    // flight managment
    private JPanel createFlightManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Ucus Islemleri"));

        JTextField txtFlightNo = new JTextField();
        JTextField txtDate = new JTextField("2026-10-01");
        JTextField txtTime = new JTextField("14:30");
        JTextField txtDep = new JTextField();
        JTextField txtArr = new JTextField();
        JTextField txtDuration = new JTextField("60");

        formPanel.add(new JLabel("Ucus No:"));
        formPanel.add(txtFlightNo);
        formPanel.add(new JLabel("Tarih (YYYY-MM-DD):"));
        formPanel.add(txtDate);
        formPanel.add(new JLabel("Saat (HH:MM):"));
        formPanel.add(txtTime);
        formPanel.add(new JLabel("Kalkis:"));
        formPanel.add(txtDep);
        formPanel.add(new JLabel("Varis:"));
        formPanel.add(txtArr);
        formPanel.add(new JLabel("Sure (Dakika):"));
        formPanel.add(txtDuration);

        JButton btnAddFlight = new JButton("Yeni Kaydet");
        btnAddFlight.setBackground(new Color(50, 150, 50));
        btnAddFlight.setForeground(Color.WHITE);

        JButton btnUpdateFlight = new JButton("Guncelle");
        btnUpdateFlight.setEnabled(false);

        formPanel.add(btnAddFlight);
        formPanel.add(btnUpdateFlight);

        panel.add(formPanel, BorderLayout.NORTH);

        String[] columns = { "Ucus No", "Tarih", "Saat", "Kalkis", "Varis", "Sure" };
        flightTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        flightTable = new JTable(flightTableModel);
        panel.add(new JScrollPane(flightTable), BorderLayout.CENTER);

        JButton btnDeleteFlight = new JButton("Secili Ucusu Sil");
        btnDeleteFlight.setBackground(Color.RED);
        btnDeleteFlight.setForeground(Color.WHITE);
        panel.add(btnDeleteFlight, BorderLayout.SOUTH);

        flightTable.getSelectionModel().addListSelectionListener(e -> {
            int row = flightTable.getSelectedRow();
            if (row != -1 && !e.getValueIsAdjusting()) {
                txtFlightNo.setText(flightTableModel.getValueAt(row, 0).toString());
                txtDate.setText(flightTableModel.getValueAt(row, 1).toString());
                txtTime.setText(flightTableModel.getValueAt(row, 2).toString());
                txtDep.setText(flightTableModel.getValueAt(row, 3).toString());
                txtArr.setText(flightTableModel.getValueAt(row, 4).toString());
                txtDuration.setText(flightTableModel.getValueAt(row, 5).toString());

                txtFlightNo.setEditable(false);
                btnUpdateFlight.setEnabled(true);
            }
        });

        btnAddFlight.addActionListener(e -> {
            try {
                // ucusun tum bilgileri doldurulmali
                if (txtFlightNo.getText().trim().isEmpty() ||
                        txtDate.getText().trim().isEmpty() ||
                        txtTime.getText().trim().isEmpty() ||
                        txtDep.getText().trim().isEmpty() ||
                        txtArr.getText().trim().isEmpty() ||
                        txtDuration.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(this,
                            "Lutfen tum alanlari doldurunuz!",
                            "Eksik Bilgi",
                            JOptionPane.WARNING_MESSAGE);
                    return; // Islemi durdur
                }

                String newFlightNo = txtFlightNo.getText().trim();

                // unique id kontrolu
                boolean isDuplicate = false;
                for (Flight f : flightManager.getAllFlights()) {
                    if (f.getFlightNum().equalsIgnoreCase(newFlightNo)) {
                        isDuplicate = true;
                        break;
                    }
                }

                if (isDuplicate) {
                    JOptionPane.showMessageDialog(this,
                            "Bu Ucus Numarasi zaten kayitli!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean isPast = LocalDate.now().isAfter(LocalDate.parse(txtDate.getText()));

                if (isPast) {
                    JOptionPane.showMessageDialog(this,
                    "Geçmiş bir tarih için uçuş ekleyemezsiniz!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                Flight f = new Flight(newFlightNo, new Plane("P-GENEL", "B737"),
                        LocalDate.parse(txtDate.getText().trim()),
                        LocalTime.parse(txtTime.getText().trim()),
                        new Route(txtDep.getText().trim(), txtArr.getText().trim()),
                        txtDep.getText().trim(),
                        txtArr.getText().trim(),
                        Integer.parseInt(txtDuration.getText().trim()));

                flightManager.createFlight(f);
                refreshFlightTable();
                clearFlightFields(txtFlightNo, txtDate, txtTime, txtDep, txtArr, txtDuration, btnUpdateFlight);
                JOptionPane.showMessageDialog(this, "Ucus Eklendi.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata (Format veya Veri): " + ex.getMessage());
            }
        });

        // update infos except flightnum
        btnUpdateFlight.addActionListener(e -> {
            try {
                String fNum = txtFlightNo.getText();
                List<Flight> allFlights = flightManager.getAllFlights();
                for (Flight f : allFlights) {
                    if (f.getFlightNum().equals(fNum)) {
                        f.setDate(LocalDate.parse(txtDate.getText()));
                        f.setHour(LocalTime.parse(txtTime.getText()));
                        f.setRoute(new Route(txtDep.getText(), txtArr.getText()));
                        f.setDeparturePlace(txtDep.getText());
                        f.setArrivalPlace(txtArr.getText());
                        f.setDuration(Integer.parseInt(txtDuration.getText()));
                        break;
                    }
                }
                FileManager.rewriteFlights(allFlights);
                refreshFlightTable();
                clearFlightFields(txtFlightNo, txtDate, txtTime, txtDep, txtArr, txtDuration, btnUpdateFlight);
                JOptionPane.showMessageDialog(this, "Ucus Guncellendi.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        });

        btnDeleteFlight.addActionListener(e -> {
            int row = flightTable.getSelectedRow();
            if (row != -1) {
                flightManager.deleteFlight(flightTableModel.getValueAt(row, 0).toString());
                refreshFlightTable();
                clearFlightFields(txtFlightNo, txtDate, txtTime, txtDep, txtArr, txtDuration, btnUpdateFlight);
            }
        });

        return panel;
    }

    private JPanel createStaffManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Personel Islemleri"));

        JTextField txtId = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtSurname = new JTextField();
        JTextField txtContact = new JTextField();

        formPanel.add(new JLabel("ID:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Ad:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Soyad:"));
        formPanel.add(txtSurname);
        formPanel.add(new JLabel("Iletisim:"));
        formPanel.add(txtContact);

        JButton btnAdd = new JButton("Yeni Ekle");
        JButton btnUpdate = new JButton("Guncelle");
        btnUpdate.setEnabled(false);

        formPanel.add(btnAdd);
        formPanel.add(btnUpdate);
        panel.add(formPanel, BorderLayout.NORTH);

        String[] columns = { "ID", "Ad", "Soyad", "Iletisim" };
        staffTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        staffTable = new JTable(staffTableModel);
        panel.add(new JScrollPane(staffTable), BorderLayout.CENTER);

        JButton btnDelete = new JButton("Secili Personeli Sil");
        btnDelete.setBackground(Color.RED);
        btnDelete.setForeground(Color.WHITE);
        panel.add(btnDelete, BorderLayout.SOUTH);

        staffTable.getSelectionModel().addListSelectionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row != -1 && !e.getValueIsAdjusting()) {
                txtId.setText(staffTableModel.getValueAt(row, 0).toString());
                txtName.setText(staffTableModel.getValueAt(row, 1).toString());
                txtSurname.setText(staffTableModel.getValueAt(row, 2).toString());
                txtContact.setText(staffTableModel.getValueAt(row, 3).toString());
                txtId.setEditable(false);
                btnUpdate.setEnabled(true);
            }
        });

        btnAdd.addActionListener(e -> {
            try {
                
                if (txtId.getText().trim().isEmpty() ||
                        txtName.getText().trim().isEmpty() ||
                        txtSurname.getText().trim().isEmpty() ||
                        txtContact.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(this,
                            "Lutfen tum alanlari doldurunuz!",
                            "Eksik Bilgi",
                            JOptionPane.WARNING_MESSAGE);
                    return; // Islemi durdur
                }

                String newId = txtId.getText().trim();

                
                boolean isDuplicate = false;
                for (Staff s : staffManager.getAllStaff()) {
                    if (s.getId().equals(newId)) {
                        isDuplicate = true;
                        break;
                    }
                }

                if (isDuplicate) {
                    JOptionPane.showMessageDialog(this,
                            "Bu Personel ID zaten kayitli!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                staffManager.addStaff(new Staff(
                        newId,
                        txtName.getText().trim(),
                        txtSurname.getText().trim(),
                        txtContact.getText().trim()));

                refreshStaffTable();
                clearStaffFields(txtId, txtName, txtSurname, txtContact, btnUpdate);
                JOptionPane.showMessageDialog(this, "Personel Eklendi.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        });

        btnUpdate.addActionListener(e -> {
            Staff updated = new Staff(txtId.getText(), txtName.getText(), txtSurname.getText(), txtContact.getText());
            staffManager.updateStaff(txtId.getText(), updated);
            refreshStaffTable();
            clearStaffFields(txtId, txtName, txtSurname, txtContact, btnUpdate);
            JOptionPane.showMessageDialog(this, "Personel Guncellendi.");
        });

        btnDelete.addActionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row != -1) {
                staffManager.deleteStaff(staffTableModel.getValueAt(row, 0).toString());
                refreshStaffTable();
                clearStaffFields(txtId, txtName, txtSurname, txtContact, btnUpdate);
            }
        });

        return panel;
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnRunReport = new JButton("Sistem Doluluk Raporunu Baslat (Senaryo 2)");
        btnRunReport.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(btnRunReport, BorderLayout.NORTH);

        JTextArea txtReportArea = new JTextArea();
        txtReportArea.setEditable(false);
        txtReportArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtReportArea.setBorder(BorderFactory.createTitledBorder("Rapor Çiktisi"));
        panel.add(new JScrollPane(txtReportArea), BorderLayout.CENTER);

        btnRunReport.addActionListener(e -> {
            btnRunReport.setEnabled(false);
            txtReportArea.setText("Rapor hazirlaniyor... Lutfen bekleyiniz.\n");

            // Global statu barini guncelle
            frame.setGlobalStatus("Preparing report...");
            frame.setGlobalProgress(0, true);

            new Thread(() -> {
                StringBuilder sb = new StringBuilder("--- SISTEM DOLULUK ANALIZI ---\n\n");
                List<Flight> allFlights = flightManager.getAllFlights();

                for (int i = 0; i < allFlights.size(); i++) {
                    try {
                        Thread.sleep(1000); //islem uzun suruyomus gibi gozuksun diye gecikme
                        Flight f = allFlights.get(i);

                        int occupied = 0;
                        for (Seat[] row : f.getPlane().getSeatMatrix()) {
                            for (Seat s : row) {
                                if (s != null && s.isReserveStatus())
                                    occupied++;
                            }
                        }

                        double percent = (occupied / (double) f.getPlane().getCapacity()) * 100;
                        sb.append(String.format("Ucus No: %s | Doluluk: %%%.2f (%d/%d Koltuk)\n",
                                f.getFlightNum(), percent, occupied, f.getPlane().getCapacity()));

                        // Global ilerlemeyi guncelle
                        int currentProgress = ((i + 1) * 100) / allFlights.size();
                        SwingUtilities.invokeLater(() -> {
                            frame.setGlobalProgress(currentProgress, true);
                            frame.setGlobalStatus("Preparing report... (%" + currentProgress + ")");
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    txtReportArea.setText(sb.toString());
                    frame.setGlobalStatus("Rapor Hazir.");
                    frame.setGlobalProgress(100, false);
                    btnRunReport.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "Rapor basariyla olusturuldu!");
                });
            }).start();
        });

        return panel;
    }

    private void refreshFlightTable() {
        flightTableModel.setRowCount(0);
        for (Flight f : flightManager.getAllFlights()) {
            flightTableModel.addRow(new Object[] {
                    f.getFlightNum(), f.getDate(), f.getHour(),
                    f.getRoute().getDepature(), f.getRoute().getArrival(),
                    f.getDuration()
            });
        }
    }

    private void refreshStaffTable() {
        staffTableModel.setRowCount(0);
        for (Staff s : staffManager.getAllStaff()) {
            staffTableModel.addRow(new Object[] { s.getId(), s.getName(), s.getSurname(), s.getContactInfo() });
        }
    }

    private void clearFlightFields(JTextField f, JTextField d, JTextField t, JTextField dep, JTextField arr,
            JTextField dur, JButton up) {
        f.setText("");
        d.setText("2026-10-01");
        t.setText("14:30");
        dep.setText("");
        arr.setText("");
        dur.setText("60");
        f.setEditable(true);
        up.setEnabled(false);
    }

    private void clearStaffFields(JTextField id, JTextField n, JTextField s, JTextField c, JButton up) {
        id.setText("");
        n.setText("");
        s.setText("");
        c.setText("");
        id.setEditable(true);
        up.setEnabled(false);
    }
}