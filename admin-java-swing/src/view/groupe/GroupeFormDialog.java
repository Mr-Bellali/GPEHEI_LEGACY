package view.groupe;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.Groupe;
import model.Filiere;
import model.GroupeStatus;
import service.FiliereService;
import exception.DatabaseException;

public class GroupeFormDialog extends JDialog {
    private JTextField nameField;
    private JComboBox<Filiere> filiereCombo;
    private JComboBox<GroupeStatus> statusCombo;
    
    private boolean confirmed = false;
    private Groupe groupe;
    private FiliereService filiereService;

    public GroupeFormDialog(JFrame parent, String title) {
        super(parent, title, true);
        this.filiereService = new FiliereService();
        initComponents();
        loadFilieres();
        setLocationRelativeTo(parent);
    }

    public GroupeFormDialog(JFrame parent, String title, Groupe g) {
        this(parent, title);
        this.groupe = g;
        populateFields();
    }

    private void initComponents() {
        setSize(500, 350);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Group Name (e.g. A):"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(30);
        nameField.setPreferredSize(new Dimension(250, 35));
        add(nameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Filiere:"), gbc);
        gbc.gridx = 1;
        filiereCombo = new JComboBox<>();
        filiereCombo.setPreferredSize(new Dimension(250, 35));
        add(filiereCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(GroupeStatus.values());
        statusCombo.setPreferredSize(new Dimension(250, 35));
        add(statusCombo, gbc);
        row++;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = new JButton("Save Group");
        JButton cancelBtn = new JButton("Cancel");
        saveBtn.setBackground(new Color(0x2C, 0x3E, 0x50));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(130, 35));
        
        cancelBtn.setBackground(new Color(0xE7, 0x4C, 0x3C));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setPreferredSize(new Dimension(100, 35));

        saveBtn.addActionListener(e -> { 
            if(validateForm()) {
                confirmed = true; 
                dispose(); 
            }
        });
        cancelBtn.addActionListener(e -> dispose());
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        add(btnPanel, gbc);
        
        filiereCombo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Filiere) setText(((Filiere) value).getName());
                return this;
            }
        });
    }

    private void loadFilieres() {
        try {
            List<Filiere> filieres = filiereService.getAllFilieres();
            for (Filiere f : filieres) filiereCombo.addItem(f);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        if(nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Group Name is required!", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if(filiereCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a Filiere!", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void populateFields() {
        if (groupe != null) {
            nameField.setText(groupe.getName());
            statusCombo.setSelectedItem(groupe.getStatus());
            for (int i = 0; i < filiereCombo.getItemCount(); i++) {
                if (filiereCombo.getItemAt(i).getId() == groupe.getFiliereId()) {
                    filiereCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public Groupe getGroupe() {
        Groupe g = new Groupe();
        if (this.groupe != null) g.setId(this.groupe.getId());
        g.setName(nameField.getText().trim());
        Filiere f = (Filiere) filiereCombo.getSelectedItem();
        if (f != null) g.setFiliereId(f.getId());
        g.setStatus((GroupeStatus) statusCombo.getSelectedItem());
        return g;
    }

    public boolean isConfirmed() { return confirmed; }
}
