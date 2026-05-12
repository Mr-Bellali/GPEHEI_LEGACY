package view.settings;

import view.master.MasterPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import model.Filiere;
import service.FiliereService;
import service.SettingsService;
import exception.DatabaseException;
import java.util.List;
import java.util.Map;

public class SettingsPanel extends JPanel implements MasterPanel {

    private static final Color PRIMARY    = new Color(0x34, 0x49, 0x5E);
    private static final Color WHITE      = new Color(0xFF, 0xFF, 0xFF);
    private static final Color BG_PAGE    = new Color(0xF4, 0xF4, 0xF8);

    private JComboBox<Filiere> filiereCombo;
    private JTextField promotionField, minCapField, maxCapField;
    private JButton saveCapBtn;
    
    private final FiliereService filiereService;
    private final SettingsService settingsService;

    public SettingsPanel() {
        this.filiereService = new FiliereService();
        this.settingsService = new SettingsService();
        setLayout(new BorderLayout());
        setBackground(BG_PAGE);
        initializeUI();
        loadFilieres();
    }

    private void initializeUI() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(13, 22, 13, 22));
        JLabel title = new JLabel("System Settings");
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(WHITE);
        header.add(title, BorderLayout.WEST);
        return header;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BG_PAGE);
        content.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Project Capacity Section
        JPanel capPanel = new JPanel(new GridBagLayout());
        capPanel.setBorder(BorderFactory.createTitledBorder("Project Capacity Settings"));
        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.insets = new Insets(8, 8, 8, 8);
        cgbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        cgbc.gridx = 0; cgbc.gridy = row;
        capPanel.add(new JLabel("Filiere:"), cgbc);
        cgbc.gridx = 1;
        filiereCombo = new JComboBox<>();
        filiereCombo.setPreferredSize(new Dimension(200, 30));
        capPanel.add(filiereCombo, cgbc);
        row++;

        cgbc.gridx = 0; cgbc.gridy = row;
        capPanel.add(new JLabel("Promotion (Year):"), cgbc);
        cgbc.gridx = 1;
        promotionField = new JTextField("2026", 10);
        capPanel.add(promotionField, cgbc);
        row++;

        cgbc.gridx = 0; cgbc.gridy = row;
        capPanel.add(new JLabel("Min Students per Project:"), cgbc);
        cgbc.gridx = 1;
        minCapField = new JTextField("1", 5);
        capPanel.add(minCapField, cgbc);
        row++;

        cgbc.gridx = 0; cgbc.gridy = row;
        capPanel.add(new JLabel("Max Students per Project:"), cgbc);
        cgbc.gridx = 1;
        maxCapField = new JTextField("3", 5);
        capPanel.add(maxCapField, cgbc);
        row++;

        cgbc.gridx = 0; cgbc.gridy = row; cgbc.gridwidth = 2;
        saveCapBtn = new JButton("Update Capacity Settings");
        saveCapBtn.addActionListener(e -> updateCapacity());
        capPanel.add(saveCapBtn, cgbc);

        gbc.gridx = 0; gbc.gridy = 0;
        content.add(capPanel, gbc);

        return content;
    }

    private void loadFilieres() {
        try {
            List<Filiere> filieres = filiereService.getAllFilieres();
            for (Filiere f : filieres) filiereCombo.addItem(f);
            
            filiereCombo.setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Filiere) setText(((Filiere) value).getName());
                    return this;
                }
            });
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    private void updateCapacity() {
        Filiere f = (Filiere) filiereCombo.getSelectedItem();
        if (f == null) return;
        try {
            int min = Integer.parseInt(minCapField.getText().trim());
            int max = Integer.parseInt(maxCapField.getText().trim());
            String promo = promotionField.getText().trim();
            settingsService.updateProjectCapacity(f.getId(), promo, min, max);
            JOptionPane.showMessageDialog(this, "Settings updated successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    @Override public String getPanelName() { return "Settings"; }
    @Override public void refreshData() {}
    @Override public JPanel getContentPanel() { return this; }
    @Override public void onPanelShown() {}
    @Override public void onPanelHidden() {}
}
