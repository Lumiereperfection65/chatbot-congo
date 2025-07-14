package com.gestion.cours.dao;

import com.gestion.cours.model.Cours;
import com.gestion.cours.model.Professeur;
import com.gestion.cours.model.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des cours
 */
public class CoursDAO extends BaseDAO<Cours> {
    
    private static final String TABLE_NAME = "cours";
    
    private static final String SQL_FIND_BY_ID = 
        "SELECT c.*, p.nom as prof_nom, p.prenom as prof_prenom, p.specialite as prof_specialite, " +
        "cat.nom as cat_nom, cat.description as cat_description " +
        "FROM " + TABLE_NAME + " c " +
        "LEFT JOIN professeurs p ON c.professeur_id = p.id " +
        "LEFT JOIN categories cat ON c.categorie_id = cat.id " +
        "WHERE c.id = ?";
    
    private static final String SQL_FIND_ALL = 
        "SELECT c.*, p.nom as prof_nom, p.prenom as prof_prenom, p.specialite as prof_specialite, " +
        "cat.nom as cat_nom, cat.description as cat_description " +
        "FROM " + TABLE_NAME + " c " +
        "LEFT JOIN professeurs p ON c.professeur_id = p.id " +
        "LEFT JOIN categories cat ON c.categorie_id = cat.id " +
        "ORDER BY c.titre";
    
    private static final String SQL_FIND_ACTIVE = 
        "SELECT c.*, p.nom as prof_nom, p.prenom as prof_prenom, p.specialite as prof_specialite, " +
        "cat.nom as cat_nom, cat.description as cat_description " +
        "FROM " + TABLE_NAME + " c " +
        "LEFT JOIN professeurs p ON c.professeur_id = p.id " +
        "LEFT JOIN categories cat ON c.categorie_id = cat.id " +
        "WHERE c.actif = true ORDER BY c.titre";
    
    private static final String SQL_INSERT = 
        "INSERT INTO " + TABLE_NAME + " (titre, description, duree_heures, prix, niveau, capacite_max, " +
        "professeur_id, categorie_id, date_debut, date_fin, horaire_debut, horaire_fin, jours_semaine, salle, actif) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SQL_UPDATE = 
        "UPDATE " + TABLE_NAME + " SET titre = ?, description = ?, duree_heures = ?, prix = ?, niveau = ?, " +
        "capacite_max = ?, professeur_id = ?, categorie_id = ?, date_debut = ?, date_fin = ?, " +
        "horaire_debut = ?, horaire_fin = ?, jours_semaine = ?, salle = ?, actif = ?, " +
        "date_modification = CURRENT_TIMESTAMP WHERE id = ?";
    
    private static final String SQL_DELETE = 
        "UPDATE " + TABLE_NAME + " SET actif = false, date_modification = CURRENT_TIMESTAMP WHERE id = ?";
    
    private static final String SQL_FIND_BY_PROFESSEUR = 
        "SELECT c.*, p.nom as prof_nom, p.prenom as prof_prenom, p.specialite as prof_specialite, " +
        "cat.nom as cat_nom, cat.description as cat_description " +
        "FROM " + TABLE_NAME + " c " +
        "LEFT JOIN professeurs p ON c.professeur_id = p.id " +
        "LEFT JOIN categories cat ON c.categorie_id = cat.id " +
        "WHERE c.professeur_id = ? AND c.actif = true ORDER BY c.date_debut";
    
    @Override
    public Cours findById(Integer id) {
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
            logger.error("Erreur lors de la recherche du cours par ID: {}", id, e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return null;
    }
    
    @Override
    public List<Cours> findAll() {
        List<Cours> cours = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_FIND_ALL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                cours.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de tous les cours", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return cours;
    }
    
    /**
     * Trouve tous les cours actifs
     */
    public List<Cours> findAllActive() {
        List<Cours> cours = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_FIND_ACTIVE);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                cours.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche des cours actifs", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return cours;
    }
    
    /**
     * Trouve les cours d'un professeur
     */
    public List<Cours> findByProfesseur(Integer professeurId) {
        if (professeurId == null) return new ArrayList<>();
        
        List<Cours> cours = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_FIND_BY_PROFESSEUR);
            stmt.setInt(1, professeurId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                cours.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche des cours du professeur ID: {}", professeurId, e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return cours;
    }
    
    @Override
    public Cours save(Cours cours) {
        if (cours == null || !cours.isValide()) {
            logger.warn("Tentative de sauvegarde d'un cours invalide");
            return null;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, cours.getTitre());
            stmt.setString(2, cours.getDescription());
            stmt.setInt(3, cours.getDureeHeures());
            stmt.setBigDecimal(4, cours.getPrix());
            stmt.setString(5, cours.getNiveau());
            stmt.setInt(6, cours.getCapaciteMax());
            stmt.setObject(7, cours.getProfesseurId());
            stmt.setObject(8, cours.getCategorieId());
            stmt.setDate(9, cours.getDateDebut() != null ? Date.valueOf(cours.getDateDebut()) : null);
            stmt.setDate(10, cours.getDateFin() != null ? Date.valueOf(cours.getDateFin()) : null);
            stmt.setTime(11, cours.getHoraireDebut() != null ? Time.valueOf(cours.getHoraireDebut()) : null);
            stmt.setTime(12, cours.getHoraireFin() != null ? Time.valueOf(cours.getHoraireFin()) : null);
            stmt.setString(13, cours.getJoursSemaine());
            stmt.setString(14, cours.getSalle());
            stmt.setBoolean(15, cours.isActif());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la création du cours");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cours.setId(generatedKeys.getInt(1));
                    logger.info("Cours créé avec l'ID: {}", cours.getId());
                    return cours;
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la sauvegarde du cours", e);
        } finally {
            closeResources(conn, stmt);
        }
        
        return null;
    }
    
    @Override
    public Cours update(Cours cours) {
        if (cours == null || cours.getId() == null || !cours.isValide()) {
            logger.warn("Tentative de mise à jour d'un cours invalide");
            return null;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE);
            
            stmt.setString(1, cours.getTitre());
            stmt.setString(2, cours.getDescription());
            stmt.setInt(3, cours.getDureeHeures());
            stmt.setBigDecimal(4, cours.getPrix());
            stmt.setString(5, cours.getNiveau());
            stmt.setInt(6, cours.getCapaciteMax());
            stmt.setObject(7, cours.getProfesseurId());
            stmt.setObject(8, cours.getCategorieId());
            stmt.setDate(9, cours.getDateDebut() != null ? Date.valueOf(cours.getDateDebut()) : null);
            stmt.setDate(10, cours.getDateFin() != null ? Date.valueOf(cours.getDateFin()) : null);
            stmt.setTime(11, cours.getHoraireDebut() != null ? Time.valueOf(cours.getHoraireDebut()) : null);
            stmt.setTime(12, cours.getHoraireFin() != null ? Time.valueOf(cours.getHoraireFin()) : null);
            stmt.setString(13, cours.getJoursSemaine());
            stmt.setString(14, cours.getSalle());
            stmt.setBoolean(15, cours.isActif());
            stmt.setInt(16, cours.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Cours mis à jour: ID {}", cours.getId());
                return cours;
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du cours", e);
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
                logger.info("Cours désactivé: ID {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression du cours ID: {}", id, e);
        } finally {
            closeResources(conn, stmt);
        }
        
        return false;
    }
    
    @Override
    protected Cours mapResultSetToEntity(ResultSet rs) throws SQLException {
        Cours cours = new Cours();
        
        cours.setId(rs.getInt("id"));
        cours.setTitre(rs.getString("titre"));
        cours.setDescription(rs.getString("description"));
        cours.setDureeHeures(rs.getInt("duree_heures"));
        cours.setPrix(rs.getBigDecimal("prix"));
        cours.setNiveau(rs.getString("niveau"));
        cours.setCapaciteMax(rs.getInt("capacite_max"));
        cours.setProfesseurId(rs.getObject("professeur_id", Integer.class));
        cours.setCategorieId(rs.getObject("categorie_id", Integer.class));
        
        Date dateDebut = rs.getDate("date_debut");
        if (dateDebut != null) {
            cours.setDateDebut(dateDebut.toLocalDate());
        }
        
        Date dateFin = rs.getDate("date_fin");
        if (dateFin != null) {
            cours.setDateFin(dateFin.toLocalDate());
        }
        
        Time horaireDebut = rs.getTime("horaire_debut");
        if (horaireDebut != null) {
            cours.setHoraireDebut(horaireDebut.toLocalTime());
        }
        
        Time horaireFin = rs.getTime("horaire_fin");
        if (horaireFin != null) {
            cours.setHoraireFin(horaireFin.toLocalTime());
        }
        
        cours.setJoursSemaine(rs.getString("jours_semaine"));
        cours.setSalle(rs.getString("salle"));
        cours.setActif(rs.getBoolean("actif"));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            cours.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        Timestamp dateModification = rs.getTimestamp("date_modification");
        if (dateModification != null) {
            cours.setDateModification(dateModification.toLocalDateTime());
        }
        
        // Mapper les objets liés
        String profNom = rs.getString("prof_nom");
        if (profNom != null) {
            Professeur professeur = new Professeur();
            professeur.setId(cours.getProfesseurId());
            professeur.setNom(profNom);
            professeur.setPrenom(rs.getString("prof_prenom"));
            professeur.setSpecialite(rs.getString("prof_specialite"));
            cours.setProfesseur(professeur);
        }
        
        String catNom = rs.getString("cat_nom");
        if (catNom != null) {
            Categorie categorie = new Categorie();
            categorie.setId(cours.getCategorieId());
            categorie.setNom(catNom);
            categorie.setDescription(rs.getString("cat_description"));
            cours.setCategorie(categorie);
        }
        
        return cours;
    }
}