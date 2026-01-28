package view;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class SimulationPanel extends JPanel {
    private MainFrame frame;

    private JPanel seatGridPanel;
    private JPanel[][] seatVisuals;
    private JCheckBox chkSync;
    private JButton btnStart;
    private JLabel lblResult;
    private JProgressBar progressBar;

    private static final int ROWS = 30;
    private static final int COLS = 6;
    private static final int TOTAL_SEATS = 180;
    private static final int PASSENGER_COUNT = 90;

    private boolean[][] seatData;
    private int occupiedCount = 0;

    public SimulationPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ust panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));

        chkSync = new JCheckBox("Senkronize Calistir (Guvenli Mod)");
        chkSync.setSelected(true); // Varsayilan guvenli olsun
        chkSync.setFont(new Font("Arial", Font.BOLD, 14));

        btnStart = new JButton("Simulasyonu Başlat");
        btnStart.setPreferredSize(new Dimension(160, 30));
        btnStart.setBackground(new Color(60, 179, 113));
        btnStart.setForeground(Color.WHITE);

        JButton btnBack = new JButton("Geri Don");
        btnBack.addActionListener(e -> frame.showScreen("BOOKING"));

        topPanel.add(chkSync);
        topPanel.add(btnStart);
        topPanel.add(btnBack);
        add(topPanel, BorderLayout.NORTH);

        // koltuk duzeni
        seatGridPanel = new JPanel(new GridLayout(ROWS, COLS, 3, 3));
        seatVisuals = new JPanel[ROWS][COLS];
        seatData = new boolean[ROWS][COLS];

        initGrid();

        // Sigmadigi icin kaydirma
        JScrollPane scrollPane = new JScrollPane(seatGridPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Ucak Koltuk Durumu "));
        add(scrollPane, BorderLayout.CENTER);

        // Alt panel
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        progressBar = new JProgressBar(0, PASSENGER_COUNT);
        progressBar.setStringPainted(true);

        lblResult = new JLabel("Durum: Baslamaya Hazir | Senaryo: 90 Yolcu", SwingConstants.CENTER);
        lblResult.setFont(new Font("Arial", Font.BOLD, 16));

        bottomPanel.add(progressBar, BorderLayout.NORTH);
        bottomPanel.add(lblResult, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // buton islevi
        btnStart.addActionListener(e -> startSimulation());
    }

    // Izgarayi olusturur ve temizler
    private void initGrid() {
        seatGridPanel.removeAll();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                JPanel p = new JPanel();
                p.setBackground(new Color(144, 238, 144)); // Acik yesil(bos)
                p.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                // Mouse ustune gelince koltuk no
                p.setToolTipText("Koltuk: " + (i + 1) + "-" + (char) ('A' + j));

                seatVisuals[i][j] = p;
                seatGridPanel.add(p);
            }
        }
        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    private void startSimulation() {
        // Once reset ki tekrar tekrar calisabilsin
        resetSimulation();

        // Simulasyon tamamlanana kadar bekle
        btnStart.setEnabled(false);
        lblResult.setText("Simulasyon çalisiyor...");
        lblResult.setForeground(Color.BLACK);

        // threadleri calistir
        new Thread(() -> {
            boolean isSync = chkSync.isSelected();
            List<Thread> threads = new ArrayList<>();

            // 90 tane yolcu olustur
            for (int i = 0; i < PASSENGER_COUNT; i++) {
                Thread t = new Thread(new PassengerTask(isSync));
                threads.add(t);
                t.start();
            }

            // Hepsinin bitmesini bekle
            for (Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Tamamlaninca arayuzu guncelle
            SwingUtilities.invokeLater(() -> {

                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        if (seatData[r][c] == true) {
                            seatVisuals[r][c].setBackground(Color.RED);
                            progressBar.setValue(progressBar.getValue() + 1);
                            occupiedCount++;
                        }
                    }
                }

                double ratio = ((double) occupiedCount / TOTAL_SEATS) * 100;

                String resultText = String.format("Dolu Koltuk: %d / %d (Doluluk Orani: %f)",
                        occupiedCount, TOTAL_SEATS, ratio);
                lblResult.setText(resultText);

                // 90dan eksikse kirmizi
                if (occupiedCount != PASSENGER_COUNT) {
                    lblResult.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(this,
                            "Race Condition Olustu!\n" +
                                    "90 yolcu vardi ama " + occupiedCount + " koltuk doldu.\n" +
                                    "Asenkronizasyondan kaynakli threadler cakisti.",
                            "Test Sonucu", JOptionPane.WARNING_MESSAGE);
                } else {
                    lblResult.setForeground(new Color(0, 100, 0)); // Koyu yesil
                }

                btnStart.setEnabled(true);
            });

        }).start();
    }

    private void resetSimulation() {
        // Verileri sifirla
        occupiedCount = 0;
        seatData = new boolean[ROWS][COLS];
        progressBar.setValue(0);

        // Gorseli de sifirla
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                seatVisuals[i][j].setBackground(new Color(144, 238, 144)); // Yesil
            }
        }
    }

    // Yolcunun(threadin) gorevi
    class PassengerTask implements Runnable {
        private boolean useSync;
        private Random random = new Random();

        public PassengerTask(boolean useSync) {
            this.useSync = useSync;
        }

        @Override
        public void run() {
            if (useSync) {
                bookSeatSynchronized();
            } else {
                bookSeatUnsafe();
            }
        }

        // senkron yani guvenli
        private void bookSeatSynchronized() {//imza bu methoda degil de asagidaki kod blogunda
            synchronized (SimulationPanel.this) {//lock inner classa degil ana classa uygulanmali
                tryBookSeat();
            }

        }

        // senkron degil yani race condition olusturacak method
        private void bookSeatUnsafe() {
            tryBookSeat();
        }

        private void tryBookSeat() {
            boolean seated = false;
            // random koltuk sec
            while (!seated) {
                int r = random.nextInt(ROWS);
                int c = random.nextInt(COLS);

                if (!seatData[r][c]) {
                    // Hatayi tetiklemek icin yapay gecikme
                    if (!useSync) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                        }
                    }
                    seatData[r][c] = true; // koltugu kapma
                    seated = true;
                }
            }
        }
    }
}