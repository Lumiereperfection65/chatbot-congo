package com.gestion.cours.dao;

import com.gestion.cours.util.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Classe de base pour tous les objets DAO
 */
public abstract class BaseDAO<T> {
    protected static final Logger logger = LoggerFactory.getLogger(BaseDAO.class);
    protected DatabaseConfig dbConfig;
    
    public BaseDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Obtient une connexion à la base de données
     */
    protected Connection getConnection() throws SQLException {
        return dbConfig.getConnection();
    }
    
    /**
     * Ferme les ressources de base de données
     */
    protected void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.warn("Erreur lors de la fermeture du ResultSet", e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.warn("Erreur lors de la fermeture du PreparedStatement", e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.warn("Erreur lors de la fermeture de la Connection", e);
            }
        }
    }
    
    /**
     * Ferme les ressources sans ResultSet
     */
    protected void closeResources(Connection conn, PreparedStatement stmt) {
        closeResources(conn, stmt, null);
    }
    
    /**
     * Exécute une requête de mise à jour et retourne l'ID généré
     */
    protected Integer executeInsertAndGetId(Connection conn, String sql, Object... params) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(stmt, params);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Aucune ligne affectée lors de l'insertion");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Impossible d'obtenir l'ID généré");
                }
            }
        }
    }
    
    /**
     * Exécute une requête de mise à jour
     */
    protected int executeUpdate(Connection conn, String sql, Object... params) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Définit les paramètres d'un PreparedStatement
     */
    protected void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
    
    /**
     * Vérifie si un enregistrement existe avec l'ID donné
     */
    protected boolean existsById(String tableName, Integer id) {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la vérification d'existence pour l'ID {}", id, e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return false;
    }
    
    // Méthodes abstraites à implémenter par les classes filles
    public abstract T findById(Integer id);
    public abstract List<T> findAll();
    public abstract T save(T entity);
    public abstract T update(T entity);
    public abstract boolean delete(Integer id);
    
    /**
     * Méthode abstraite pour mapper un ResultSet vers un objet
     */
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
}