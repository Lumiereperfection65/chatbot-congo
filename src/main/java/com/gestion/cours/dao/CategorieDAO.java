package com.gestion.cours.dao;

import com.gestion.cours.model.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des catégories de cours
 */
public class CategorieDAO extends BaseDAO<Categorie> {
    
    private static final String TABLE_NAME = "categories";
    
    private static final String SQL_FIND_BY_ID = 
        "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
    
    private static final String SQL_FIND_ALL = 
        "SELECT * FROM " + TABLE_NAME + " ORDER BY nom";
    
    private static final String SQL_FIND_ACTIVE = 
        "SELECT * FROM " + TABLE_NAME + " WHERE actif = true ORDER BY nom";
    
    private static final String SQL_INSERT = 
        "INSERT INTO " + TABLE_NAME + " (nom, description, actif) VALUES (?, ?, ?)";
    
    private static final String SQL_UPDATE = 
        "UPDATE " + TABLE_NAME + " SET nom = ?, description = ?, actif = ? WHERE id = ?";
    
    private static final String SQL_DELETE = 
        "UPDATE " + TABLE_NAME + " SET actif = false WHERE id = ?";
    
    @Override
    public Categorie findById(Integer id) {
        if (id == null) return null;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_ID);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de la catégorie par ID: {}", id, e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return null;
    }
    
    @Override
    public List<Categorie> findAll() {
        List<Categorie> categories = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_FIND_ALL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                categories.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de toutes les catégories", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return categories;
    }
    
    /**
     * Trouve toutes les catégories actives
     */
    public List<Categorie> findAllActive() {
        List<Categorie> categories = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_FIND_ACTIVE);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                categories.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche des catégories actives", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return categories;
    }
    
    @Override
    public Categorie save(Categorie categorie) {
        if (categorie == null || !categorie.isValide()) {
            logger.warn("Tentative de sauvegarde d'une catégorie invalide");
            return null;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, categorie.getNom());
            stmt.setString(2, categorie.getDescription());
            stmt.setBoolean(3, categorie.isActif());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la création de la catégorie");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    categorie.setId(generatedKeys.getInt(1));
                    logger.info("Catégorie créée avec l'ID: {}", categorie.getId());
                    return categorie;
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la sauvegarde de la catégorie", e);
        } finally {
            closeResources(conn, stmt);
        }
        
        return null;
    }
    
    @Override
    public Categorie update(Categorie categorie) {
        if (categorie == null || categorie.getId() == null || !categorie.isValide()) {
            logger.warn("Tentative de mise à jour d'une catégorie invalide");
            return null;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE);
            
            stmt.setString(1, categorie.getNom());
            stmt.setString(2, categorie.getDescription());
            stmt.setBoolean(3, categorie.isActif());
            stmt.setInt(4, categorie.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Catégorie mise à jour: ID {}", categorie.getId());
                return categorie;
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de la catégorie", e);
        } finally {
            closeResources(conn, stmt);
        }
        
        return null;
    }
    
    @Override
    public boolean delete(Integer id) {
        if (id == null) return false;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Catégorie désactivée: ID {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de la catégorie ID: {}", id, e);
        } finally {
            closeResources(conn, stmt);
        }
        
        return false;
    }
    
    @Override
    protected Categorie mapResultSetToEntity(ResultSet rs) throws SQLException {
        Categorie categorie = new Categorie();
        
        categorie.setId(rs.getInt("id"));
        categorie.setNom(rs.getString("nom"));
        categorie.setDescription(rs.getString("description"));
        categorie.setActif(rs.getBoolean("actif"));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            categorie.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        return categorie;
    }
}