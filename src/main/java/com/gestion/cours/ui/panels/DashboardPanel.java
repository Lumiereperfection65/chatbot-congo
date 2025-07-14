package com.gestion.cours.ui.panels;

import com.gestion.cours.dao.*;
import com.gestion.cours.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panneau de tableau de bord avec statistiques et informations principales
 */
public class DashboardPanel extends JPanel implements RefreshablePanel {
    
    private JLabel totalCoursLabel;
    private JLabel totalEtudiantsLabel;
    private JLabel totalProfesseursLabel;
    private JLabel totalInscriptionsLabel;
    private JLabel coursActifsLabel;
    private JLabel coursTerminesLabel;
    
    private CoursDAO coursDAO;
    private EtudiantDAO etudiantDAO;
    private ProfesseurDAO professeurDAO;
    
    public DashboardPanel() {
        initializeDAO();
        createUI();
        refresh();
    }
    
    private void initializeDAO() {
        coursDAO = new CoursDAO();
        etudiantDAO = new EtudiantDAO();
        professeurDAO = new ProfesseurDAO();
    }
    
    private void createUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Titre
        JLabel titleLabel = new JLabel("Tableau de Bord", JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Panel principal avec les statistiques
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // Première ligne - Statistiques générales
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(createStatCard("Total Cours", "0", Color.BLUE), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(createStatCard("Total Étudiants", "0", new Color(34, 139, 34)), gbc);
        
        gbc.gridx = 2;
        mainPanel.add(createStatCard("Total Professeurs", "0", new Color(255, 140, 0)), gbc);
        
        gbc.gridx = 3;
        mainPanel.add(createStatCard("Total Inscriptions", "0", new Color(220, 20, 60)), gbc);
        
        // Deuxième ligne - État des cours
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        mainPanel.add(createStatCard("Cours Actifs", "0", new Color(46, 125, 50)), gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 2;
        mainPanel.add(createStatCard("Cours Terminés", "0", new Color(117, 117, 117)), gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Panel d'informations supplémentaires
        createInfoPanel();
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        card.setBackground(Color.WHITE);
        
        // Titre
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        titleLabel.setForeground(color);
        
        // Valeur
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
        valueLabel.setForeground(color);
        
        // Stocker les références pour les mettre à jour
        if (title.contains("Cours") && !title.contains("Actifs") && !title.contains("Terminés")) {
            totalCoursLabel = valueLabel;
        } else if (title.contains("Étudiants")) {
            totalEtudiantsLabel = valueLabel;
        } else if (title.contains("Professeurs")) {
            totalProfesseursLabel = valueLabel;
        } else if (title.contains("Inscriptions")) {
            totalInscriptionsLabel = valueLabel;
        } else if (title.contains("Actifs")) {
            coursActifsLabel = valueLabel;
        } else if (title.contains("Terminés")) {
            coursTerminesLabel = valueLabel;
        }
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informations Rapides"));
        infoPanel.setPreferredSize(new Dimension(0, 150));
        
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        infoArea.setText("Bienvenue dans l'application de gestion de cours !\n\n" +
                "Utilisez les onglets pour :\n" +
                "• Gérer les cours et leurs détails\n" +
                "• Administrer les étudiants et professeurs\n" +
                "• Suivre les inscriptions et les notes\n\n" +
                "Les données sont automatiquement sauvegardées.");
        
        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    @Override
    public void refresh() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private int totalCours, totalEtudiants, totalProfesseurs;
            private int coursActifs, coursTermines;
            
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Récupérer les statistiques
                    List<Cours> allCours = coursDAO.findAll();
                    totalCours = allCours.size();
                    
                    coursActifs = (int) allCours.stream()
                            .filter(c -> c.isActif() && (c.isEnCours() || c.isAVenir()))
                            .count();
                    
                    coursTermines = (int) allCours.stream()
                            .filter(c -> c.isTermine())
                            .count();
                    
                    totalEtudiants = etudiantDAO.findAll().size();
                    totalProfesseurs = professeurDAO.findAll().size();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            
            @Override
            protected void done() {
                // Mettre à jour l'interface
                SwingUtilities.invokeLater(() -> {
                    if (totalCoursLabel != null) {
                        totalCoursLabel.setText(String.valueOf(totalCours));
                    }
                    if (totalEtudiantsLabel != null) {
                        totalEtudiantsLabel.setText(String.valueOf(totalEtudiants));
                    }
                    if (totalProfesseursLabel != null) {
                        totalProfesseursLabel.setText(String.valueOf(totalProfesseurs));
                    }
                    if (coursActifsLabel != null) {
                        coursActifsLabel.setText(String.valueOf(coursActifs));
                    }
                    if (coursTerminesLabel != null) {
                        coursTerminesLabel.setText(String.valueOf(coursTermines));
                    }
                    if (totalInscriptionsLabel != null) {
                        // TODO: Ajouter le calcul des inscriptions quand le DAO sera créé
                        totalInscriptionsLabel.setText("0");
                    }
                });
            }
        };
        
        worker.execute();
    }
}