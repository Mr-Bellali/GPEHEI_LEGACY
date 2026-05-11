package view.settings;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class SettingsFrame extends JFrame {

    // ── Color Palette ──────────────────────────────────────
    private static final Color PRIMARY    = new Color(0x3D, 0x34, 0x8B);
    private static final Color SECONDARY  = new Color(0x76, 0x78, 0xED);
    private static final Color WHITE      = new Color(0xFF, 0xFF, 0xFF);
    private static final Color LIGHT_GRAY = new Color(0xD1, 0xD1, 0xD1);
    private static final Color BG_PAGE    = new Color(0xF4, 0xF4, 0xF8);
    private static final Color SIDEBAR_BG = new Color(0x2E, 0x28, 0x72);
    private static final Color RED        = new Color(0xE7, 0x4C, 0x3C);
    private static final Color GREEN      = new Color(0x27, 0xAE, 0x60);

    // ── Profile fields ─────────────────────────────────────
    private JTextField  firstNameField;
    private JTextField  lastNameField;
    private JTextField  emailField;
    private JTextField  phoneField;

    // ── Password fields ────────────────────────────────────
    private JPasswordField currentPassField;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;

    // ── System settings ────────────────────────────────────
    private JComboBox<String> languageBox;
    private JComboBox<String> themeBox;
    private JCheckBox         emailNotifCheck;
    private JCheckBox         smsNotifCheck;
    private JCheckBox         autoBackupCheck;

    // ── Buttons ────────────────────────────────────────────
    private JButton saveProfileBtn;
    private JButton changePasswordBtn;
    private JButton saveSystemBtn;
    private JButton logoutButton;

    public SettingsFrame() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("GPEHEI - Settings");
        setSize(1050, 660);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_PAGE);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMain(),    BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── SIDEBAR ────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(210, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(22, 0, 22, 0));

        JLabel brand = new JLabel("  GPEHEI");
        brand.setFont(new Font("Georgia", Font.BOLD, 17));
        brand.setForeground(WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 14, 16, 0));
        p.add(brand);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(210, 1));
        sep.setForeground(new Color(255, 255, 255, 40));
        p.add(sep);
        p.add(Box.createVerticalStrut(14));

        addNavBtn(p, "Dashboard", false);
        addNavBtn(p, "Students",  false);
        addNavBtn(p, "Projects",  false);
        addNavBtn(p, "Reports",   false);
        addNavBtn(p, "Settings",  true);   // active
        return p;
    }

    private void addNavBtn(JPanel sidebar, String text, boolean active) {
        JButton btn = new JButton("  " + text) {
            boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hovered || active) {
                    g2.setColor(new Color(255, 255, 255, 22));
                    g2.fillRoundRect(8, 2, getWidth()-16, getHeight()-4, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setMaximumSize(new Dimension(210, 42));
        btn.setPreferredSize(new Dimension(210, 42));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setForeground(WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(4));
    }

    // ── MAIN ───────────────────────────────────────────────
    private JPanel buildMain() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PAGE);
        p.add(buildHeader(),  BorderLayout.NORTH);
        p.add(buildContent(), BorderLayout.CENTER);
        p.add(buildStatus(),  BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(PRIMARY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(13, 22, 13, 22));

        JLabel title = new JLabel("Settings");
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        logoutButton = makeBtn("Logout", RED);
        logoutButton.setPreferredSize(new Dimension(90, 34));
        right.add(logoutButton);

        p.add(title, BorderLayout.WEST);
        p.add(right,  BorderLayout.EAST);
        return p;
    }

    // ── CONTENT (3 sections side by side) ──────────────────
    private JPanel buildContent() {
        JScrollPane scroll = new JScrollPane(buildSettingsBody());
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_PAGE);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_PAGE);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildSettingsBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(BG_PAGE);
        body.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 16);

        // Section 1: Profile
        gbc.gridx = 0; gbc.weightx = 1.0;
        body.add(buildProfileSection(), gbc);

        // Section 2: Password
        gbc.gridx = 1; gbc.weightx = 0.8;
        body.add(buildPasswordSection(), gbc);

        // Section 3: System
        gbc.gridx = 2; gbc.weightx = 0.8; gbc.insets = new Insets(0,0,0,0);
        body.add(buildSystemSection(), gbc);

        return body;
    }

    // ── Profile Section ────────────────────────────────────
    private JPanel buildProfileSection() {
        JPanel card = buildCard("Profile Information");

        firstNameField = addField(card, "First Name",  "");
        lastNameField  = addField(card, "Last Name",   "");
        emailField     = addField(card, "Email",       "");
        phoneField     = addField(card, "Phone",       "");

        card.add(Box.createVerticalStrut(20));

        saveProfileBtn = makeBtn("Save Profile", PRIMARY);
        saveProfileBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveProfileBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(saveProfileBtn);

        return card;
    }

    // ── Password Section ───────────────────────────────────
    private JPanel buildPasswordSection() {
        JPanel card = buildCard("Change Password");

        currentPassField = addPassField(card, "Current Password");
        newPassField     = addPassField(card, "New Password");
        confirmPassField = addPassField(card, "Confirm New Password");

        // Password strength hint
        JLabel hint = new JLabel("Min. 8 chars, include numbers & symbols");
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        hint.setForeground(new Color(0x99, 0x99, 0xAA));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(Box.createVerticalStrut(6));
        card.add(hint);
        card.add(Box.createVerticalStrut(20));

        changePasswordBtn = makeBtn("Change Password", SECONDARY);
        changePasswordBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        changePasswordBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(changePasswordBtn);

        return card;
    }

    // ── System Section ─────────────────────────────────────
    private JPanel buildSystemSection() {
        JPanel card = buildCard("System Settings");

        // Language
        card.add(makeFieldLabel("Language"));
        card.add(Box.createVerticalStrut(6));
        languageBox = new JComboBox<>(new String[]{"English", "French", "Arabic"});
        languageBox.setFont(new Font("Arial", Font.PLAIN, 13));
        languageBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        languageBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(languageBox);
        card.add(Box.createVerticalStrut(14));

        // Theme
        card.add(makeFieldLabel("Theme"));
        card.add(Box.createVerticalStrut(6));
        themeBox = new JComboBox<>(new String[]{"Light", "Dark", "System"});
        themeBox.setFont(new Font("Arial", Font.PLAIN, 13));
        themeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        themeBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(themeBox);
        card.add(Box.createVerticalStrut(18));

        // Notifications
        card.add(makeFieldLabel("Notifications"));
        card.add(Box.createVerticalStrut(8));

        emailNotifCheck = new JCheckBox("Email notifications");
        emailNotifCheck.setFont(new Font("Arial", Font.PLAIN, 13));
        emailNotifCheck.setForeground(new Color(0x33,0x33,0x44));
        emailNotifCheck.setOpaque(false);
        emailNotifCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailNotifCheck.setSelected(true);

        smsNotifCheck = new JCheckBox("SMS notifications");
        smsNotifCheck.setFont(new Font("Arial", Font.PLAIN, 13));
        smsNotifCheck.setForeground(new Color(0x33,0x33,0x44));
        smsNotifCheck.setOpaque(false);
        smsNotifCheck.setAlignmentX(Component.LEFT_ALIGNMENT);

        autoBackupCheck = new JCheckBox("Auto backup (daily)");
        autoBackupCheck.setFont(new Font("Arial", Font.PLAIN, 13));
        autoBackupCheck.setForeground(new Color(0x33,0x33,0x44));
        autoBackupCheck.setOpaque(false);
        autoBackupCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        autoBackupCheck.setSelected(true);

        card.add(emailNotifCheck);
        card.add(Box.createVerticalStrut(6));
        card.add(smsNotifCheck);
        card.add(Box.createVerticalStrut(6));
        card.add(autoBackupCheck);
        card.add(Box.createVerticalGlue());

        saveSystemBtn = makeBtn("Save Settings", GREEN);
        saveSystemBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveSystemBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(Box.createVerticalStrut(20));
        card.add(saveSystemBtn);

        return card;
    }

    // ── Card builder ───────────────────────────────────────
    private JPanel buildCard(String sectionTitle) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,12));
                g2.fillRoundRect(4, 4, getWidth()-4, getHeight()-4, 14, 14);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 14, 14);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lbl = new JLabel(sectionTitle);
        lbl.setFont(new Font("Georgia", Font.BOLD, 15));
        lbl.setForeground(PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(LIGHT_GRAY);

        card.add(lbl);
        card.add(Box.createVerticalStrut(10));
        card.add(sep);
        card.add(Box.createVerticalStrut(16));
        return card;
    }

    private JTextField addField(JPanel card, String label, String defaultValue) {
        card.add(makeFieldLabel(label));
        card.add(Box.createVerticalStrut(6));
        JTextField f = new JTextField(defaultValue);
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        card.add(f);
        card.add(Box.createVerticalStrut(12));
        return f;
    }

    private JPasswordField addPassField(JPanel card, String label) {
        card.add(makeFieldLabel(label));
        card.add(Box.createVerticalStrut(6));
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        card.add(f);
        card.add(Box.createVerticalStrut(12));
        return f;
    }

    private JLabel makeFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        l.setForeground(new Color(0x55, 0x55, 0x66));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setForeground(WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel buildStatus() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(LIGHT_GRAY);
        p.setBorder(BorderFactory.createEmptyBorder(5, 16, 5, 16));
        JLabel s = new JLabel("Ready");
        s.setFont(new Font("Arial", Font.PLAIN, 11));
        s.setForeground(new Color(0x55,0x55,0x66));
        p.add(s, BorderLayout.WEST);
        return p;
    }

    // ── PUBLIC API ─────────────────────────────────────────
    /** Populate profile fields from DB */
    public void setProfileData(String firstName, String lastName, String email, String phone) {
        firstNameField.setText(firstName);
        lastNameField.setText(lastName);
        emailField.setText(email);
        phoneField.setText(phone);
    }

    public String getFirstName()      { return firstNameField.getText().trim(); }
    public String getLastName()       { return lastNameField.getText().trim(); }
    public String getEmail()          { return emailField.getText().trim(); }
    public String getPhone()          { return phoneField.getText().trim(); }
    public String getCurrentPassword(){ return new String(currentPassField.getPassword()); }
    public String getNewPassword()    { return new String(newPassField.getPassword()); }
    public String getConfirmPassword(){ return new String(confirmPassField.getPassword()); }
    public String getLanguage()       { return (String) languageBox.getSelectedItem(); }
    public String getTheme()          { return (String) themeBox.getSelectedItem(); }
    public boolean isEmailNotif()     { return emailNotifCheck.isSelected(); }
    public boolean isSmsNotif()       { return smsNotifCheck.isSelected(); }
    public boolean isAutoBackup()     { return autoBackupCheck.isSelected(); }

    public void addSaveProfileListener(ActionListener l)    { saveProfileBtn.addActionListener(l); }
    public void addChangePasswordListener(ActionListener l) { changePasswordBtn.addActionListener(l); }
    public void addSaveSystemListener(ActionListener l)     { saveSystemBtn.addActionListener(l); }
    public void addLogoutListener(ActionListener l)         { logoutButton.addActionListener(l); }

    public void showMessage(String msg) { JOptionPane.showMessageDialog(this, msg); }
    public int showConfirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirm", JOptionPane.YES_NO_OPTION);
    }
}