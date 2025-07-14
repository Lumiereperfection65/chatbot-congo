package com.gestion.cours.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestionnaire de configuration et de connexions à la base de données
 */
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static DatabaseConfig instance;
    private Properties properties;
    
    private String url;
    private String username;
    private String password;
    private String driver;
    
    private DatabaseConfig() {
        loadConfiguration();
    }
    
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    private void loadConfiguration() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                logger.error("Fichier database.properties introuvable");
                throw new RuntimeException("Configuration de base de données non trouvée");
            }
            
            properties.load(input);
            
            url = properties.getProperty("db.url");
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");
            driver = properties.getProperty("db.driver");
            
            // Charger le driver
            Class.forName(driver);
            
            logger.info("Configuration de base de données chargée avec succès");
            
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Erreur lors du chargement de la configuration", e);
            throw new RuntimeException("Erreur de configuration de base de données", e);
        }
    }
    
    /**
     * Obtient une nouvelle connexion à la base de données
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            conn.setAutoCommit(true);
            return conn;
        } catch (SQLException e) {
            logger.error("Erreur lors de la connexion à la base de données: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Teste la connexion à la base de données
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            logger.error("Test de connexion échoué", e);
            return false;
        }
    }
    
    /**
     * Ferme une connexion de manière sécurisée
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.warn("Erreur lors de la fermeture de connexion", e);
            }
        }
    }
    
    // Getters
    public String getUrl() { return url; }
    public String getUsername() { return username; }
    public String getDriver() { return driver; }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}