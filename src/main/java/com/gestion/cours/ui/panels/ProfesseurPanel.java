package com.gestion.cours.ui.panels;

import com.gestion.cours.dao.ProfesseurDAO;
import com.gestion.cours.model.Professeur;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Panneau de gestion des professeurs
 */
public class ProfesseurPanel extends JPanel implements RefreshablePanel {
    
    private JTable professeurTable;
    private DefaultTableModel tableModel;
    private ProfesseurDAO professeurDAO;
    
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailField;
    private JTextField telephoneField;
    private JTextField specialiteField;
    private JTextField dateEmbaucheField;
    private JTextField salaireField;
    private JCheckBox actifCheck;
    
    private Professeur selectedProfesseur;
    
    public ProfesseurPanel() {
        professeurDAO = new ProfesseurDAO();
        createUI();
        refresh();
    }
    
    private void createUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        
        JPanel leftPanel = createProfesseurListPanel();
        splitPane.setLeftComponent(leftPanel);
        
        JPanel rightPanel = createFormPanel();
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createProfesseurListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Liste des Professeurs"));
        
        String[] columns = {"ID", "Nom", "Prénom", "Email", "Spécialité", "Salaire", "Actif"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        professeurTable = new JTable(tableModel);
        professeurTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        professeurTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedProfesseur();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(professeurTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Nouveau");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        JButton refreshButton = new JButton("Actualiser");
        
        addButton.addActionListener(e -> clearForm());
        editButton.addActionListener(e -> loadSelectedProfesseur());
        deleteButton.addActionListener(e -> deleteProfesseur());
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
        panel.setBorder(BorderFactory.createTitledBorder("Détails du Professeur"));
        
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
        
        // Spécialité
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Spécialité *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        specialiteField = new JTextField(20);
        formPanel.add(specialiteField, gbc);
        
        // Date d'embauche
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Date embauche (yyyy-mm-dd):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dateEmbaucheField = new JTextField(20);
        formPanel.add(dateEmbaucheField, gbc);
        
        // Salaire
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Salaire:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        salaireField = new JTextField(20);
        formPanel.add(salaireField, gbc);
        
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
        
        saveButton.addActionListener(e -> saveProfesseur());
        cancelButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadSelectedProfesseur() {
        int selectedRow = professeurTable.getSelectedRow();
        if (selectedRow >= 0) {
            Integer professeurId = (Integer) tableModel.getValueAt(selectedRow, 0);
            selectedProfesseur = professeurDAO.findById(professeurId);
            
            if (selectedProfesseur != null) {
                populateForm(selectedProfesseur);
            }
        }
    }
    
    private void populateForm(Professeur professeur) {
        nomField.setText(professeur.getNom());
        prenomField.setText(professeur.getPrenom());
        emailField.setText(professeur.getEmail());
        telephoneField.setText(professeur.getTelephone());
        specialiteField.setText(professeur.getSpecialite());
        dateEmbaucheField.setText(professeur.getDateEmbauche() != null ? professeur.getDateEmbauche().toString() : "");
        salaireField.setText(professeur.getSalaire() != null ? professeur.getSalaire().toString() : "");
        actifCheck.setSelected(professeur.isActif());
    }
    
    private void clearForm() {
        selectedProfesseur = null;
        nomField.setText("");
        prenomField.setText("");
        emailField.setText("");
        telephoneField.setText("");
        specialiteField.setText("");
        dateEmbaucheField.setText("");
        salaireField.setText("");
        actifCheck.setSelected(true);
    }
    
    private void saveProfesseur() {
        try {
            if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty() || 
                emailField.getText().trim().isEmpty() || specialiteField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nom, prénom, email et spécialité sont obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Professeur professeur = selectedProfesseur != null ? selectedProfesseur : new Professeur();
            
            professeur.setNom(nomField.getText().trim());
            professeur.setPrenom(prenomField.getText().trim());
            professeur.setEmail(emailField.getText().trim());
            professeur.setTelephone(telephoneField.getText().trim());
            professeur.setSpecialite(specialiteField.getText().trim());
            
            if (!dateEmbaucheField.getText().trim().isEmpty()) {
                try {
                    professeur.setDateEmbauche(LocalDate.parse(dateEmbaucheField.getText().trim()));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Format de date invalide (yyyy-mm-dd)", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (!salaireField.getText().trim().isEmpty()) {
                try {
                    professeur.setSalaire(new BigDecimal(salaireField.getText().trim()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Format de salaire invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            professeur.setActif(actifCheck.isSelected());
            
            Professeur savedProfesseur;
            if (selectedProfesseur == null) {
                savedProfesseur = professeurDAO.save(professeur);
            } else {
                savedProfesseur = professeurDAO.update(professeur);
            }
            
            if (savedProfesseur != null) {
                JOptionPane.showMessageDialog(this, "Professeur sauvegardé avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteProfesseur() {
        if (selectedProfesseur != null) {
            int option = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer ce professeur ?", 
                "Confirmation", JOptionPane.YES_NO_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                if (professeurDAO.delete(selectedProfesseur.getId())) {
                    JOptionPane.showMessageDialog(this, "Professeur supprimé", "Succès", JOptionPane.INFORMATION_MESSAGE);
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
        SwingWorker<List<Professeur>, Void> worker = new SwingWorker<List<Professeur>, Void>() {
            @Override
            protected List<Professeur> doInBackground() throws Exception {
                return professeurDAO.findAll();
            }
            
            @Override
            protected void done() {
                try {
                    List<Professeur> professeurs = get();
                    tableModel.setRowCount(0);
                    
                    for (Professeur p : professeurs) {
                        Object[] row = {
                            p.getId(),
                            p.getNom(),
                            p.getPrenom(),
                            p.getEmail(),
                            p.getSpecialite(),
                            p.getSalaire(),
                            p.isActif() ? "Oui" : "Non"
                        };
                        tableModel.addRow(row);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ProfesseurPanel.this, 
                        "Erreur lors du chargement: " + e.getMessage(), 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
}