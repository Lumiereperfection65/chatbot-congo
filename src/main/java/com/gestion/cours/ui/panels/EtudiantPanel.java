package com.gestion.cours.ui.panels;

import com.gestion.cours.dao.EtudiantDAO;
import com.gestion.cours.model.Etudiant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Panneau de gestion des étudiants
 */
public class EtudiantPanel extends JPanel implements RefreshablePanel {
    
    private JTable etudiantTable;
    private DefaultTableModel tableModel;
    private EtudiantDAO etudiantDAO;
    
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailField;
    private JTextField telephoneField;
    private JTextField dateNaissanceField;
    private JTextArea adresseArea;
    private JComboBox<String> niveauCombo;
    private JCheckBox actifCheck;
    
    private Etudiant selectedEtudiant;
    
    public EtudiantPanel() {
        etudiantDAO = new EtudiantDAO();
        createUI();
        refresh();
    }
    
    private void createUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        
        JPanel leftPanel = createEtudiantListPanel();
        splitPane.setLeftComponent(leftPanel);
        
        JPanel rightPanel = createFormPanel();
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createEtudiantListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Liste des Étudiants"));
        
        String[] columns = {"ID", "Nom", "Prénom", "Email", "Téléphone", "Niveau", "Actif"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        etudiantTable = new JTable(tableModel);
        etudiantTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        etudiantTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedEtudiant();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(etudiantTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Nouveau");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        JButton refreshButton = new JButton("Actualiser");
        
        addButton.addActionListener(e -> clearForm());
        editButton.addActionListener(e -> loadSelectedEtudiant());
        deleteButton.addActionListener(e -> deleteEtudiant());
        refreshButton.addActionListener(e -> refresh());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Détails de l'Étudiant"));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Nom
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Nom *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        nomField = new JTextField(20);
        formPanel.add(nomField, gbc);
        
        // Prénom
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Prénom *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        prenomField = new JTextField(20);
        formPanel.add(prenomField, gbc);
        
        // Email
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Email *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        
        // Téléphone
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Téléphone:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        telephoneField = new JTextField(20);
        formPanel.add(telephoneField, gbc);
        
        // Date de naissance
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Date naissance (yyyy-mm-dd):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dateNaissanceField = new JTextField(20);
        formPanel.add(dateNaissanceField, gbc);
        
        // Adresse
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Adresse:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        adresseArea = new JTextArea(3, 20);
        adresseArea.setLineWrap(true);
        adresseArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(adresseArea), gbc);
        
        // Niveau d'étude
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        formPanel.add(new JLabel("Niveau d'étude:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        niveauCombo = new JComboBox<>(new String[]{"Bac", "Bac+1", "Bac+2", "Bac+3", "Bac+4", "Bac+5", "Autre"});
        formPanel.add(niveauCombo, gbc);
        
        // Actif
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Actif:"), gbc);
        gbc.gridx = 1;
        actifCheck = new JCheckBox();
        actifCheck.setSelected(true);
        formPanel.add(actifCheck, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        
        saveButton.addActionListener(e -> saveEtudiant());
        cancelButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        panel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadSelectedEtudiant() {
        int selectedRow = etudiantTable.getSelectedRow();
        if (selectedRow >= 0) {
            Integer etudiantId = (Integer) tableModel.getValueAt(selectedRow, 0);
            selectedEtudiant = etudiantDAO.findById(etudiantId);
            
            if (selectedEtudiant != null) {
                populateForm(selectedEtudiant);
            }
        }
    }
    
    private void populateForm(Etudiant etudiant) {
        nomField.setText(etudiant.getNom());
        prenomField.setText(etudiant.getPrenom());
        emailField.setText(etudiant.getEmail());
        telephoneField.setText(etudiant.getTelephone());
        dateNaissanceField.setText(etudiant.getDateNaissance() != null ? etudiant.getDateNaissance().toString() : "");
        adresseArea.setText(etudiant.getAdresse());
        niveauCombo.setSelectedItem(etudiant.getNiveauEtude());
        actifCheck.setSelected(etudiant.isActif());
    }
    
    private void clearForm() {
        selectedEtudiant = null;
        nomField.setText("");
        prenomField.setText("");
        emailField.setText("");
        telephoneField.setText("");
        dateNaissanceField.setText("");
        adresseArea.setText("");
        niveauCombo.setSelectedIndex(0);
        actifCheck.setSelected(true);
    }
    
    private void saveEtudiant() {
        try {
            if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty() || 
                emailField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nom, prénom et email sont obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Etudiant etudiant = selectedEtudiant != null ? selectedEtudiant : new Etudiant();
            
            etudiant.setNom(nomField.getText().trim());
            etudiant.setPrenom(prenomField.getText().trim());
            etudiant.setEmail(emailField.getText().trim());
            etudiant.setTelephone(telephoneField.getText().trim());
            
            if (!dateNaissanceField.getText().trim().isEmpty()) {
                try {
                    etudiant.setDateNaissance(LocalDate.parse(dateNaissanceField.getText().trim()));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Format de date invalide (yyyy-mm-dd)", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            etudiant.setAdresse(adresseArea.getText().trim());
            etudiant.setNiveauEtude((String) niveauCombo.getSelectedItem());
            etudiant.setActif(actifCheck.isSelected());
            
            Etudiant savedEtudiant;
            if (selectedEtudiant == null) {
                savedEtudiant = etudiantDAO.save(etudiant);
            } else {
                savedEtudiant = etudiantDAO.update(etudiant);
            }
            
            if (savedEtudiant != null) {
                JOptionPane.showMessageDialog(this, "Étudiant sauvegardé avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteEtudiant() {
        if (selectedEtudiant != null) {
            int option = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer cet étudiant ?", 
                "Confirmation", JOptionPane.YES_NO_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                if (etudiantDAO.delete(selectedEtudiant.getId())) {
                    JOptionPane.showMessageDialog(this, "Étudiant supprimé", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    @Override
    public void refresh() {
        SwingWorker<List<Etudiant>, Void> worker = new SwingWorker<List<Etudiant>, Void>() {
            @Override
            protected List<Etudiant> doInBackground() throws Exception {
                return etudiantDAO.findAll();
            }
            
            @Override
            protected void done() {
                try {
                    List<Etudiant> etudiants = get();
                    tableModel.setRowCount(0);
                    
                    for (Etudiant e : etudiants) {
                        Object[] row = {
                            e.getId(),
                            e.getNom(),
                            e.getPrenom(),
                            e.getEmail(),
                            e.getTelephone(),
                            e.getNiveauEtude(),
                            e.isActif() ? "Oui" : "Non"
                        };
                        tableModel.addRow(row);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(EtudiantPanel.this, 
                        "Erreur lors du chargement: " + e.getMessage(), 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
}