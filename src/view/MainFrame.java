// MainFrame.java dosyanızın güncel hali
package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private SimulationPanel simulationPanel;  
    private LoginPanel loginPanel;
    private BookingPanel bookingPanel;
    private ReservationPanel reservationPanel;
    private AdminPanel adminPanel;

    private JProgressBar globalProgressBar;
    private JLabel lblGlobalStatus;

    public MainFrame() {
        setTitle("Ucak Rezervasyon Sistemi");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // paneller
        simulationPanel = new SimulationPanel(this);
        loginPanel = new LoginPanel(this);
        bookingPanel = new BookingPanel(this);
        reservationPanel = new ReservationPanel(this);
        adminPanel = new AdminPanel(this);

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(bookingPanel, "BOOKING");
        mainPanel.add(reservationPanel, "RESERVATION");
        mainPanel.add(adminPanel, "ADMIN");
        mainPanel.add(simulationPanel, "SIMULATION");

        // status bar
        JPanel statusBar = new JPanel(new BorderLayout(10, 0));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setPreferredSize(new Dimension(this.getWidth(), 30));

        lblGlobalStatus = new JLabel(" ");
        globalProgressBar = new JProgressBar(0, 100);
        globalProgressBar.setStringPainted(true);
        globalProgressBar.setPreferredSize(new Dimension(200, 20));
        globalProgressBar.setVisible(false); 

        statusBar.add(lblGlobalStatus, BorderLayout.CENTER);
        statusBar.add(globalProgressBar, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        //baslangic giris ekrani 
        showScreen("LOGIN");
    }

    public void setGlobalProgress(int value, boolean visible) {
        globalProgressBar.setValue(value);
        globalProgressBar.setVisible(visible);
    }

    public void setGlobalStatus(String text) {
        lblGlobalStatus.setText(" " + text);
        if (text.contains("Preparing report")) { 
            lblGlobalStatus.setForeground(Color.BLUE);
        } else {
            lblGlobalStatus.setForeground(Color.BLACK);
        }
    }

    public void showScreen(String screenName) {
        cardLayout.show(mainPanel, screenName);
    }

    
}