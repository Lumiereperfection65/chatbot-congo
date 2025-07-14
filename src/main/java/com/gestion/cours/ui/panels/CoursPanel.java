package com.gestion.cours.ui.panels;

import com.gestion.cours.dao.*;
import com.gestion.cours.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Panneau de gestion des cours
 */
public class CoursPanel extends JPanel implements RefreshablePanel {
    
    private JTable coursTable;
    private DefaultTableModel tableModel;
    private CoursDAO coursDAO;
    private ProfesseurDAO professeurDAO;
    private CategorieDAO categorieDAO;
    
    private JTextField titreField;
    private JTextArea descriptionArea;
    private JTextField dureeField;
    private JTextField prixField;
    private JComboBox<String> niveauCombo;
    private JTextField capaciteField;
    private JComboBox<Professeur> professeurCombo;
    private JComboBox<Categorie> categorieCombo;
    private JTextField dateDebutField;
    private JTextField dateFinField;
    private JTextField horaireDebutField;
    private JTextField horaireFinField;
    private JTextField joursField;
    private JTextField salleField;
    private JCheckBox actifCheck;
    
    private Cours selectedCours;
    
    public CoursPanel() {
        initializeDAO();
        createUI();
        refresh();
    }
    
    private void initializeDAO() {
        coursDAO = new CoursDAO();
        professeurDAO = new ProfesseurDAO();
        categorieDAO = new CategorieDAO();
    }
    
    private void createUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel principal avec split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        
        // Panel gauche - Liste des cours
        JPanel leftPanel = createCoursListPanel();
        splitPane.setLeftComponent(leftPanel);
        
        // Panel droit - Formulaire
        JPanel rightPanel = createFormPanel();
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createCoursListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Liste des Cours"));
        
        // Table des cours
        String[] columns = {"ID", "Titre", "Niveau", "Durée (h)", "Prix", "Professeur", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        coursTable = new JTable(tableModel);
        coursTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        coursTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedCours();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(coursTable);
        scrollPane.setPreferredSize(new Dimension(580, 300));
        
        // Boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Nouveau");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        JButton refreshButton = new JButton("Actualiser");
        
        addButton.addActionListener(e -> clearForm());
        editButton.addActionListener(e -> loadSelectedCours());
        deleteButton.addActionListener(e -> deleteCours());
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
        panel.setBorder(BorderFactory.createTitledBorder("Détails du Cours"));
        
        // Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Titre
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Titre *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        titreField = new JTextField(20);
        formPanel.add(titreField, gbc);
        
        // Description
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);
        
        // Durée
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        formPanel.add(new JLabel("Durée (heures) *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dureeField = new JTextField(10);
        formPanel.add(dureeField, gbc);
        
        // Prix
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Prix *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        prixField = new JTextField(10);
        formPanel.add(prixField, gbc);
        
        // Niveau
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Niveau *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        niveauCombo = new JComboBox<>(new String[]{"Débutant", "Intermédiaire", "Avancé"});
        formPanel.add(niveauCombo, gbc);
        
        // Capacité
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Capacité max:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        capaciteField = new JTextField(10);
        capaciteField.setText("30");
        formPanel.add(capaciteField, gbc);
        
        // Professeur
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Professeur:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        professeurCombo = new JComboBox<>();
        formPanel.add(professeurCombo, gbc);
        
        // Catégorie
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Catégorie:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        categorieCombo = new JComboBox<>();
        formPanel.add(categorieCombo, gbc);
        
        // Dates
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Date début (yyyy-mm-dd):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dateDebutField = new JTextField(10);
        formPanel.add(dateDebutField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Date fin (yyyy-mm-dd):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dateFinField = new JTextField(10);
        formPanel.add(dateFinField, gbc);
        
        // Horaires
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Horaire début (HH:mm):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        horaireDebutField = new JTextField(10);
        formPanel.add(horaireDebutField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Horaire fin (HH:mm):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        horaireFinField = new JTextField(10);
        formPanel.add(horaireFinField, gbc);
        
        // Jours et salle
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Jours semaine:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        joursField = new JTextField(20);
        joursField.setToolTipText("Ex: Lundi,Mercredi,Vendredi");
        formPanel.add(joursField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Salle:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        salleField = new JTextField(10);
        formPanel.add(salleField, gbc);
        
        // Actif
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Actif:"), gbc);
        gbc.gridx = 1;
        actifCheck = new JCheckBox();
        actifCheck.setSelected(true);
        formPanel.add(actifCheck, gbc);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        
        saveButton.addActionListener(e -> saveCours());
        cancelButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        panel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadSelectedCours() {
        int selectedRow = coursTable.getSelectedRow();
        if (selectedRow >= 0) {
            Integer coursId = (Integer) tableModel.getValueAt(selectedRow, 0);
            selectedCours = coursDAO.findById(coursId);
            
            if (selectedCours != null) {
                populateForm(selectedCours);
            }
        }
    }
    
    private void populateForm(Cours cours) {
        titreField.setText(cours.getTitre());
        descriptionArea.setText(cours.getDescription());
        dureeField.setText(cours.getDureeHeures() != null ? cours.getDureeHeures().toString() : "");
        prixField.setText(cours.getPrix() != null ? cours.getPrix().toString() : "");
        niveauCombo.setSelectedItem(cours.getNiveau());
        capaciteField.setText(cours.getCapaciteMax() != null ? cours.getCapaciteMax().toString() : "30");
        
        // Sélectionner le professeur
        if (cours.getProfesseurId() != null) {
            for (int i = 0; i < professeurCombo.getItemCount(); i++) {
                Professeur prof = professeurCombo.getItemAt(i);
                if (prof != null && prof.getId().equals(cours.getProfesseurId())) {
                    professeurCombo.setSelectedItem(prof);
                    break;
                }
            }
        }
        
        // Sélectionner la catégorie
        if (cours.getCategorieId() != null) {
            for (int i = 0; i < categorieCombo.getItemCount(); i++) {
                Categorie cat = categorieCombo.getItemAt(i);
                if (cat != null && cat.getId().equals(cours.getCategorieId())) {
                    categorieCombo.setSelectedItem(cat);
                    break;
                }
            }
        }
        
        dateDebutField.setText(cours.getDateDebut() != null ? cours.getDateDebut().toString() : "");
        dateFinField.setText(cours.getDateFin() != null ? cours.getDateFin().toString() : "");
        horaireDebutField.setText(cours.getHoraireDebut() != null ? cours.getHoraireDebut().toString() : "");
        horaireFinField.setText(cours.getHoraireFin() != null ? cours.getHoraireFin().toString() : "");
        joursField.setText(cours.getJoursSemaine());
        salleField.setText(cours.getSalle());
        actifCheck.setSelected(cours.isActif());
    }
    
    private void clearForm() {
        selectedCours = null;
        titreField.setText("");
        descriptionArea.setText("");
        dureeField.setText("");
        prixField.setText("");
        niveauCombo.setSelectedIndex(0);
        capaciteField.setText("30");
        professeurCombo.setSelectedIndex(-1);
        categorieCombo.setSelectedIndex(-1);
        dateDebutField.setText("");
        dateFinField.setText("");
        horaireDebutField.setText("");
        horaireFinField.setText("");
        joursField.setText("");
        salleField.setText("");
        actifCheck.setSelected(true);
    }
    
    private void saveCours() {
        try {
            // Validation
            if (titreField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le titre est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Créer ou mettre à jour le cours
            Cours cours = selectedCours != null ? selectedCours : new Cours();
            
            cours.setTitre(titreField.getText().trim());
            cours.setDescription(descriptionArea.getText().trim());
            
            try {
                cours.setDureeHeures(Integer.parseInt(dureeField.getText().trim()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Durée invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                cours.setPrix(new BigDecimal(prixField.getText().trim()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Prix invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            cours.setNiveau((String) niveauCombo.getSelectedItem());
            
            try {
                cours.setCapaciteMax(Integer.parseInt(capaciteField.getText().trim()));
            } catch (NumberFormatException e) {
                cours.setCapaciteMax(30);
            }
            
            Professeur selectedProf = (Professeur) professeurCombo.getSelectedItem();
            cours.setProfesseurId(selectedProf != null ? selectedProf.getId() : null);
            
            Categorie selectedCat = (Categorie) categorieCombo.getSelectedItem();
            cours.setCategorieId(selectedCat != null ? selectedCat.getId() : null);
            
            // Dates
            if (!dateDebutField.getText().trim().isEmpty()) {
                try {
                    cours.setDateDebut(LocalDate.parse(dateDebutField.getText().trim()));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Format de date début invalide (yyyy-mm-dd)", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (!dateFinField.getText().trim().isEmpty()) {
                try {
                    cours.setDateFin(LocalDate.parse(dateFinField.getText().trim()));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Format de date fin invalide (yyyy-mm-dd)", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Horaires
            if (!horaireDebutField.getText().trim().isEmpty()) {
                try {
                    cours.setHoraireDebut(LocalTime.parse(horaireDebutField.getText().trim()));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Format d'horaire début invalide (HH:mm)", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (!horaireFinField.getText().trim().isEmpty()) {
                try {
                    cours.setHoraireFin(LocalTime.parse(horaireFinField.getText().trim()));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Format d'horaire fin invalide (HH:mm)", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            cours.setJoursSemaine(joursField.getText().trim());
            cours.setSalle(salleField.getText().trim());
            cours.setActif(actifCheck.isSelected());
            
            // Sauvegarder
            Cours savedCours;
            if (selectedCours == null) {
                savedCours = coursDAO.save(cours);
            } else {
                savedCours = coursDAO.update(cours);
            }
            
            if (savedCours != null) {
                JOptionPane.showMessageDialog(this, "Cours sauvegardé avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteCours() {
        if (selectedCours != null) {
            int option = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer ce cours ?", 
                "Confirmation", JOptionPane.YES_NO_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                if (coursDAO.delete(selectedCours.getId())) {
                    JOptionPane.showMessageDialog(this, "Cours supprimé", "Succès", JOptionPane.INFORMATION_MESSAGE);
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
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private List<Cours> cours;
            private List<Professeur> professeurs;
            private List<Categorie> categories;
            
            @Override
            protected Void doInBackground() throws Exception {
                cours = coursDAO.findAll();
                professeurs = professeurDAO.findAllActive();
                categories = categorieDAO.findAllActive();
                return null;
            }
            
            @Override
            protected void done() {
                // Mettre à jour la table
                tableModel.setRowCount(0);
                for (Cours c : cours) {
                    Object[] row = {
                        c.getId(),
                        c.getTitre(),
                        c.getNiveau(),
                        c.getDureeHeures(),
                        c.getPrix(),
                        c.getProfesseur() != null ? c.getProfesseur().getNomComplet() : "",
                        c.getStatutCours()
                    };
                    tableModel.addRow(row);
                }
                
                // Mettre à jour les combos
                professeurCombo.removeAllItems();
                professeurCombo.addItem(null);
                for (Professeur p : professeurs) {
                    professeurCombo.addItem(p);
                }
                
                categorieCombo.removeAllItems();
                categorieCombo.addItem(null);
                for (Categorie c : categories) {
                    categorieCombo.addItem(c);
                }
            }
        };
        
        worker.execute();
    }
}