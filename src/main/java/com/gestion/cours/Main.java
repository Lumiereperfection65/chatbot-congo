package com.gestion.cours;

import com.gestion.cours.ui.LoginFrame;
import com.gestion.cours.util.DatabaseConfig;

import javax.swing.*;
import java.awt.*;

/**
 * Classe principale de l'application de gestion de cours
 */
public class Main {
    
    public static void main(String[] args) {
        // Configurer l'apparence de l'application
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Lancer l'application dans l'EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            try {
                // Tester la connexion à la base de données
                testDatabaseConnection();
                
                // Créer et afficher l'interface de connexion
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                
            } catch (Exception e) {
                showStartupError(e);
            }
        });
    }
    
    /**
     * Teste la connexion à la base de données au démarrage
     */
    private static void testDatabaseConnection() {
        try {
            DatabaseConfig config = DatabaseConfig.getInstance();
            if (!config.testConnection()) {
                showDatabaseWarning();
            }
        } catch (Exception e) {
            System.err.println("Erreur de configuration de base de données : " + e.getMessage());
            showDatabaseError(e);
        }
    }
    
    /**
     * Affiche un avertissement en cas de problème de connexion
     */
    private static void showDatabaseWarning() {
        String message = "⚠️ Avertissement : Impossible de se connecter à la base de données.\n\n" +
                "L'application va démarrer mais certaines fonctionnalités pourraient ne pas fonctionner.\n\n" +
                "Veuillez vérifier :\n" +
                "• Que MySQL est démarré\n" +
                "• Les paramètres dans database.properties\n" +
                "• Que la base de données 'gestion_cours_db' existe\n\n" +
                "Voulez-vous continuer ?";
        
        int option = JOptionPane.showConfirmDialog(null, message, 
                "Problème de connexion à la base de données", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (option != JOptionPane.YES_OPTION) {
            System.exit(1);
        }
    }
    
    /**
     * Affiche une erreur critique de configuration
     */
    private static void showDatabaseError(Exception e) {
        String message = "❌ Erreur critique de configuration !\n\n" +
                "Détails : " + e.getMessage() + "\n\n" +
                "L'application ne peut pas démarrer sans une configuration valide.\n" +
                "Veuillez vérifier le fichier database.properties.";
        
        JOptionPane.showMessageDialog(null, message, 
                "Erreur de configuration", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
    
    /**
     * Affiche une erreur de démarrage générale
     */
    private static void showStartupError(Exception e) {
        String message = "❌ Erreur lors du démarrage de l'application !\n\n" +
                "Détails : " + e.getMessage() + "\n\n" +
                "Veuillez redémarrer l'application ou contacter le support.";
        
        JOptionPane.showMessageDialog(null, message, 
                "Erreur de démarrage", JOptionPane.ERROR_MESSAGE);
        
        e.printStackTrace();
        System.exit(1);
    }
}