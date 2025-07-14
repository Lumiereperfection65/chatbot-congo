package com.gestion.cours.dao;

import com.gestion.cours.model.Professeur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des professeurs
 */
public class ProfesseurDAO extends BaseDAO<Professeur> {
    
    private static final String TABLE_NAME = "professeurs";
    
    private static final String SQL_FIND_BY_ID = 
        "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
    
    private static final String SQL_FIND_ALL = 
        "SELECT * FROM " + TABLE_NAME + " ORDER BY nom, prenom";
    
    private static final String SQL_FIND_ACTIVE = 
        "SELECT * FROM " + TABLE_NAME + " WHERE actif = true ORDER BY nom, prenom";
    
    private static final String SQL_INSERT = 
        "INSERT INTO " + TABLE_NAME + " (nom, prenom, email, telephone, specialite, date_embauche, salaire, actif) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SQL_UPDATE = 
        "UPDATE " + TABLE_NAME + " SET nom = ?, prenom = ?, email = ?, telephone = ?, " +
        "specialite = ?, date_embauche = ?, salaire = ?, actif = ?, date_modification = CURRENT_TIMESTAMP " +
        "WHERE id = ?";
    
    private static final String SQL_DELETE = 
        "UPDATE " + TABLE_NAME + " SET actif = false, date_modification = CURRENT_TIMESTAMP WHERE id = ?";
    
    private static final String SQL_FIND_BY_EMAIL = 
        "SELECT * FROM " + TABLE_NAME + " WHERE email = ?";
    
    @Override
    public Professeur findById(Integer id) {
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
            logger.error("Erreur lors de la recherche du professeur par ID: {}", id, e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return null;
    }
    
    @Override
    public List<Professeur> findAll() {
        List<Professeur> professeurs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_FIND_ALL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                professeurs.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de tous les professeurs", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return professeurs;
    }
    
    /**
     * Trouve tous les professeurs actifs
     */
    public List<Professeur> findAllActive() {
        List<Professeur> professeurs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_FIND_ACTIVE);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                professeurs.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche des professeurs actifs", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return professeurs;
    }
    
    /**
     * Trouve un professeur par email
     */
    public Professeur findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_EMAIL);
            stmt.setString(1, email);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du professeur par email: {}", email, e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return null;
    }
    
    @Override
    public Professeur save(Professeur professeur) {
        if (professeur == null || !professeur.isValide()) {
            logger.warn("Tentative de sauvegarde d'un professeur invalide");
            return null;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, professeur.getNom());
            stmt.setString(2, professeur.getPrenom());
            stmt.setString(3, professeur.getEmail());
            stmt.setString(4, professeur.getTelephone());
            stmt.setString(5, professeur.getSpecialite());
            stmt.setDate(6, professeur.getDateEmbauche() != null ? Date.valueOf(professeur.getDateEmbauche()) : null);
            stmt.setBigDecimal(7, professeur.getSalaire());
            stmt.setBoolean(8, professeur.isActif());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la création du professeur");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    professeur.setId(generatedKeys.getInt(1));
                    logger.info("Professeur créé avec l'ID: {}", professeur.getId());
                    return professeur;
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la sauvegarde du professeur", e);
        } finally {
            closeResources(conn, stmt);
        }
        
        return null;
    }
    
    @Override
    public Professeur update(Professeur professeur) {
        if (professeur == null || professeur.getId() == null || !professeur.isValide()) {
            logger.warn("Tentative de mise à jour d'un professeur invalide");
            return null;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE);
            
            stmt.setString(1, professeur.getNom());
            stmt.setString(2, professeur.getPrenom());
            stmt.setString(3, professeur.getEmail());
            stmt.setString(4, professeur.getTelephone());
            stmt.setString(5, professeur.getSpecialite());
            stmt.setDate(6, professeur.getDateEmbauche() != null ? Date.valueOf(professeur.getDateEmbauche()) : null);
            stmt.setBigDecimal(7, professeur.getSalaire());
            stmt.setBoolean(8, professeur.isActif());
            stmt.setInt(9, professeur.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Professeur mis à jour: ID {}", professeur.getId());
                return professeur;
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du professeur", e);
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
                logger.info("Professeur désactivé: ID {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression du professeur ID: {}", id, e);
        } finally {
            closeResources(conn, stmt);
        }
        
        return false;
    }
    
    @Override
    protected Professeur mapResultSetToEntity(ResultSet rs) throws SQLException {
        Professeur professeur = new Professeur();
        
        professeur.setId(rs.getInt("id"));
        professeur.setNom(rs.getString("nom"));
        professeur.setPrenom(rs.getString("prenom"));
        professeur.setEmail(rs.getString("email"));
        professeur.setTelephone(rs.getString("telephone"));
        professeur.setSpecialite(rs.getString("specialite"));
        
        Date dateEmbauche = rs.getDate("date_embauche");
        if (dateEmbauche != null) {
            professeur.setDateEmbauche(dateEmbauche.toLocalDate());
        }
        
        professeur.setSalaire(rs.getBigDecimal("salaire"));
        professeur.setActif(rs.getBoolean("actif"));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            professeur.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        Timestamp dateModification = rs.getTimestamp("date_modification");
        if (dateModification != null) {
            professeur.setDateModification(dateModification.toLocalDateTime());
        }
        
        return professeur;
    }
}