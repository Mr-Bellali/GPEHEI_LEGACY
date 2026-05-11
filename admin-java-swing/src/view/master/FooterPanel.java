package view.master;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FooterPanel extends JPanel {

    private JLabel statusLabel;
    private JLabel timeLabel;
    private Timer timer;

    public FooterPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(189, 195, 199));
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        setPreferredSize(new Dimension(0, 30));

        // Left side - Status
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Center - Copyright
        JLabel copyrightLabel = new JLabel("©G321 2026 GPEHEI - All rights reserved");
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Right side - Time
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        updateTime();

        // Update time every second
        timer = new Timer(1000, e -> updateTime());
        timer.start();

        add(statusLabel, BorderLayout.WEST);
        add(copyrightLabel, BorderLayout.CENTER);
        add(timeLabel, BorderLayout.EAST);
    }

    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        timeLabel.setText(now.format(formatter));
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }
}