package com.gestion.cours.dao;

import com.gestion.cours.model.Etudiant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des étudiants
 */
public class EtudiantDAO extends BaseDAO<Etudiant> {
    
    private static final String TABLE_NAME = "etudiants";
    
    private static final String SQL_FIND_BY_ID = 
        "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
    
    private static final String SQL_FIND_ALL = 
        "SELECT * FROM " + TABLE_NAME + " ORDER BY nom, prenom";
    
    private static final String SQL_FIND_ACTIVE = 
        "SELECT * FROM " + TABLE_NAME + " WHERE actif = true ORDER BY nom, prenom";
    
    private static final String SQL_INSERT = 
        "INSERT INTO " + TABLE_NAME + " (nom, prenom, email, telephone, date_naissance, adresse, niveau_etude, actif) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SQL_UPDATE = 
        "UPDATE " + TABLE_NAME + " SET nom = ?, prenom = ?, email = ?, telephone = ?, " +
        "date_naissance = ?, adresse = ?, niveau_etude = ?, actif = ?, date_modification = CURRENT_TIMESTAMP " +
        "WHERE id = ?";
    
    private static final String SQL_DELETE = 
        "UPDATE " + TABLE_NAME + " SET actif = false, date_modification = CURRENT_TIMESTAMP WHERE id = ?";
    
    private static final String SQL_FIND_BY_EMAIL = 
        "SELECT * FROM " + TABLE_NAME + " WHERE email = ?";
    
    @Override
    public Etudiant findById(Integer id) {
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
            logger.error("Erreur lors de la recherche de l'étudiant par ID: {}", id, e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return null;
    }
    
    @Override
    public List<Etudiant> findAll() {
        List<Etudiant> etudiants = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_FIND_ALL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                etudiants.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de tous les étudiants", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return etudiants;
    }
    
    /**
     * Trouve tous les étudiants actifs
     */
    public List<Etudiant> findAllActive() {
        List<Etudiant> etudiants = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_FIND_ACTIVE);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                etudiants.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche des étudiants actifs", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return etudiants;
    }
    
    /**
     * Trouve un étudiant par email
     */
    public Etudiant findByEmail(String email) {
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
            logger.error("Erreur lors de la recherche de l'étudiant par email: {}", email, e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return null;
    }
    
    @Override
    public Etudiant save(Etudiant etudiant) {
        if (etudiant == null || !etudiant.isValide()) {
            logger.warn("Tentative de sauvegarde d'un étudiant invalide");
            return null;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, etudiant.getNom());
            stmt.setString(2, etudiant.getPrenom());
            stmt.setString(3, etudiant.getEmail());
            stmt.setString(4, etudiant.getTelephone());
            stmt.setDate(5, etudiant.getDateNaissance() != null ? Date.valueOf(etudiant.getDateNaissance()) : null);
            stmt.setString(6, etudiant.getAdresse());
            stmt.setString(7, etudiant.getNiveauEtude());
            stmt.setBoolean(8, etudiant.isActif());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la création de l'étudiant");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    etudiant.setId(generatedKeys.getInt(1));
                    logger.info("Étudiant créé avec l'ID: {}", etudiant.getId());
                    return etudiant;
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la sauvegarde de l'étudiant", e);
        } finally {
            closeResources(conn, stmt);
        }
        
        return null;
    }
    
    @Override
    public Etudiant update(Etudiant etudiant) {
        if (etudiant == null || etudiant.getId() == null || !etudiant.isValide()) {
            logger.warn("Tentative de mise à jour d'un étudiant invalide");
            return null;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE);
            
            stmt.setString(1, etudiant.getNom());
            stmt.setString(2, etudiant.getPrenom());
            stmt.setString(3, etudiant.getEmail());
            stmt.setString(4, etudiant.getTelephone());
            stmt.setDate(5, etudiant.getDateNaissance() != null ? Date.valueOf(etudiant.getDateNaissance()) : null);
            stmt.setString(6, etudiant.getAdresse());
            stmt.setString(7, etudiant.getNiveauEtude());
            stmt.setBoolean(8, etudiant.isActif());
            stmt.setInt(9, etudiant.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Étudiant mis à jour: ID {}", etudiant.getId());
                return etudiant;
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de l'étudiant", e);
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
                logger.info("Étudiant désactivé: ID {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de l'étudiant ID: {}", id, e);
        } finally {
            closeResources(conn, stmt);
        }
        
        return false;
    }
    
    @Override
    protected Etudiant mapResultSetToEntity(ResultSet rs) throws SQLException {
        Etudiant etudiant = new Etudiant();
        
        etudiant.setId(rs.getInt("id"));
        etudiant.setNom(rs.getString("nom"));
        etudiant.setPrenom(rs.getString("prenom"));
        etudiant.setEmail(rs.getString("email"));
        etudiant.setTelephone(rs.getString("telephone"));
        
        Date dateNaissance = rs.getDate("date_naissance");
        if (dateNaissance != null) {
            etudiant.setDateNaissance(dateNaissance.toLocalDate());
        }
        
        etudiant.setAdresse(rs.getString("adresse"));
        etudiant.setNiveauEtude(rs.getString("niveau_etude"));
        etudiant.setActif(rs.getBoolean("actif"));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            etudiant.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        Timestamp dateModification = rs.getTimestamp("date_modification");
        if (dateModification != null) {
            etudiant.setDateModification(dateModification.toLocalDateTime());
        }
        
        return etudiant;
    }
}