package view.auth;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    // ── Color Palette ──────────────────────────────────────
    private static final Color PRIMARY    = new Color(0x3D, 0x34, 0x8B); // #3D348B
    private static final Color SECONDARY  = new Color(0x76, 0x78, 0xED); // #7678ED
    private static final Color WHITE      = new Color(0xFF, 0xFF, 0xFF); // #FFFFFF
    private static final Color LIGHT_GRAY = new Color(0xD1, 0xD1, 0xD1); // #D1D1D1

    private JTextField     emailField;
    private JPasswordField passwordField;
    private JButton        loginButton;
    private JCheckBox      rememberMe;
    private JLabel         forgotPassword;

    public LoginFrame() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("GPEHEI - Login");
        setSize(860, 520);                        // fixed compact size
        setMinimumSize(new Dimension(860, 520));
        setMaximumSize(new Dimension(860, 520));  // prevent stretching
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PRIMARY);
        root.add(buildLeftPanel(),  BorderLayout.WEST);
        root.add(buildRightPanel(), BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── LEFT decorative panel ──────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel p = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(118, 120, 237, 55));
                g2.fillRect(90, 140, 14, 180);   // vertical bar of "L"
                g2.fillRect(90, 306, 90, 14);    // horizontal bar of "L"
                g2.dispose();
            }
        };
        p.setPreferredSize(new Dimension(340, 520));
        p.setBackground(PRIMARY);

        JLabel brand = new JLabel("EHEI");
        brand.setFont(new Font("Georgia", Font.BOLD, 20));
        brand.setForeground(WHITE);
        brand.setBounds(20, 18, 120, 28);
        p.add(brand);

        JLabel copy = new JLabel("\u00A9G321 2026 All rights reserved");
        copy.setFont(new Font("Arial", Font.PLAIN, 10));
        copy.setForeground(LIGHT_GRAY);
        copy.setBounds(20, 494, 240, 16);
        p.add(copy);

        return p;
    }

    // ── RIGHT form panel ───────────────────────────────────
    private JPanel buildRightPanel() {
        JPanel p = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(SECONDARY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Title
        JLabel title = new JLabel("Login");
        title.setFont(new Font("Georgia", Font.PLAIN, 38));
        title.setForeground(WHITE);
        title.setBounds(50, 60, 220, 50);
        p.add(title);

        // ── Email ─────────────────────────── ROW 1
        JLabel emailLbl = new JLabel("Email");
        emailLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        emailLbl.setForeground(WHITE);
        emailLbl.setBounds(50, 138, 200, 20);
        p.add(emailLbl);

        emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBounds(50, 162, 410, 34);   // full width
        styleField(emailField);
        p.add(emailField);

        // ── Password ─────────────────────── ROW 2  (below email, NOT beside it)
        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        passLbl.setForeground(WHITE);
        passLbl.setBounds(50, 218, 200, 20);
        p.add(passLbl);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBounds(50, 242, 372, 34);
        styleField(passwordField);
        p.add(passwordField);

        // Eye toggle button
        JButton eyeBtn = new JButton("\uD83D\uDC41");
        eyeBtn.setBounds(418, 244, 42, 30);
        eyeBtn.setContentAreaFilled(false);
        eyeBtn.setBorderPainted(false);
        eyeBtn.setFocusPainted(false);
        eyeBtn.setForeground(WHITE);
        eyeBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeBtn.addActionListener(e ->
                passwordField.setEchoChar(
                        passwordField.getEchoChar() != 0 ? (char) 0 : '\u2022'));
        p.add(eyeBtn);

        // ── Remember me ───────────────────────────────────
        rememberMe = new JCheckBox("Remember me");
        rememberMe.setFont(new Font("Arial", Font.PLAIN, 13));
        rememberMe.setForeground(WHITE);
        rememberMe.setOpaque(false);
        rememberMe.setSelected(true);
        rememberMe.setBounds(50, 296, 160, 26);
        p.add(rememberMe);

        // ── Sign in button ────────────────────────────────
        loginButton = new JButton("Sign in") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? LIGHT_GRAY : WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        loginButton.setFont(new Font("Arial", Font.BOLD, 15));
        loginButton.setForeground(PRIMARY);
        loginButton.setBounds(248, 410, 212, 44);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.add(loginButton);

        // ── Forgot password ───────────────────────────────
        forgotPassword = new JLabel("Forgot password?");
        forgotPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotPassword.setForeground(WHITE);
        forgotPassword.setBounds(50, 420, 160, 22);
        forgotPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.add(forgotPassword);

        return p;
    }

    // ── Shared field style ─────────────────────────────────
    private void styleField(JTextField field) {
        field.setOpaque(false);
        field.setBackground(new Color(0, 0, 0, 0));
        field.setForeground(WHITE);
        field.setCaretColor(WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, WHITE),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)
        ));
    }

    // ── Getters ────────────────────────────────────────────
    public String  getEmail()     { return emailField.getText().trim(); }
    public String  getPassword()  { return new String(passwordField.getPassword()); }
    public boolean isRememberMe() { return rememberMe.isSelected(); }

    // ── Listeners ──────────────────────────────────────────
    public void addLoginListener(ActionListener l)        { loginButton.addActionListener(l); }
    public void addForgotPasswordListener(MouseAdapter l) { forgotPassword.addMouseListener(l); }

    // ── Helpers ────────────────────────────────────────────
    public void showMessage(String msg) { JOptionPane.showMessageDialog(this, msg); }
}