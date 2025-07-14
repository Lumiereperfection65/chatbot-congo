package com.gestion.cours.ui.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Panneau de gestion des inscriptions
 * Version simple pour commencer
 */
public class InscriptionPanel extends JPanel implements RefreshablePanel {
    
    public InscriptionPanel() {
        createUI();
    }
    
    private void createUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Message temporaire
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel iconLabel = new JLabel("📝", JLabel.CENTER);
        iconLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 48));
        centerPanel.add(iconLabel, gbc);
        
        gbc.gridy = 1;
        JLabel titleLabel = new JLabel("Gestion des Inscriptions", JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        centerPanel.add(titleLabel, gbc);
        
        gbc.gridy = 2;
        JLabel messageLabel = new JLabel("<html><center>Module en développement<br><br>" +
                "Cette section permettra de :<br>" +
                "• Inscrire des étudiants aux cours<br>" +
                "• Gérer les statuts d'inscription<br>" +
                "• Suivre les notes et évaluations<br>" +
                "• Générer des rapports</center></html>", JLabel.CENTER);
        messageLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        messageLabel.setForeground(Color.GRAY);
        centerPanel.add(messageLabel, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    @Override
    public void refresh() {
        // Rien à faire pour le moment
    }
}