package view.library;

import javax.swing.*;
import java.awt.*;
import model.Library;

public class LibraryFormDialog extends JDialog {
    private JTextField nameField;
    private JComboBox<String> statusCombo;
    private boolean confirmed = false;
    private Library library;

    public LibraryFormDialog(JFrame parent, String title) {
        super(parent, title, true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        initComponents();
    }

    public LibraryFormDialog(JFrame parent, String title, Library l) {
        this(parent, title);
        this.library = l;
        populateFields();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Library Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"active", "disabled"});
        add(statusCombo, gbc);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> { confirmed = true; dispose(); });
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JPanel bp = new JPanel();
        bp.add(saveBtn);
        bp.add(cancelBtn);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(bp, gbc);
    }

    private void populateFields() {
        if (library != null) {
            nameField.setText(library.getName());
            statusCombo.setSelectedItem(library.getStatus());
        }
    }

    public Library getLibrary() {
        Library l = new Library();
        if (library != null) l.setId(library.getId());
        l.setName(nameField.getText().trim());
        l.setStatus((String) statusCombo.getSelectedItem());
        return l;
    }

    public boolean isConfirmed() { return confirmed; }
}
