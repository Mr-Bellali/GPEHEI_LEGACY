package view.master;

import javax.swing.*;
import java.awt.*;
import controller.MasterController;
import utils.SessionManager;
import view.auth.LoginFrame;

public class MasterFrame extends JFrame {

    private HeaderPanel headerPanel;
    private SidebarPanel sidebarPanel;
    private FooterPanel footerPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private MasterController controller;

    public MasterFrame() {
        initializeUI();
        setupPanels();
    }

    private void initializeUI() {
        setTitle("GPEHEI - Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 768));

        // Main layout
        setLayout(new BorderLayout());
    }

    private void setupPanels() {
        // Create header
        headerPanel = new HeaderPanel(() -> logout());
        add(headerPanel, BorderLayout.NORTH);

        // Create sidebar
        sidebarPanel = new SidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Create content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        // Create footer
        footerPanel = new FooterPanel();
        add(footerPanel, BorderLayout.SOUTH);

        // Add default loading panel
        JPanel loadingPanel = new JPanel(new GridBagLayout());
        loadingPanel.add(new JLabel("Loading..."));
        contentPanel.add(loadingPanel, "LOADING");
        cardLayout.show(contentPanel, "LOADING");

        // Initialize controller
        controller = new MasterController(this);
    }

    private void logout() {
        footerPanel.stopTimer();
        dispose();

        // Show login frame
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            new controller.LoginController(loginFrame); // You need to adjust this import
            loginFrame.setVisible(true);
        });
    }

    @Override
    public void dispose() {
        footerPanel.stopTimer();
        super.dispose();
    }

    // ========= GETTERS =========

    public HeaderPanel getHeaderPanel() {
        return headerPanel;
    }

    public SidebarPanel getSidebarPanel() {
        return sidebarPanel;
    }

    public FooterPanel getFooterPanel() {
        return footerPanel;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public MasterController getController() {
        return controller;
    }
}