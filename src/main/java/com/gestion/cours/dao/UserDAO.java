package com.gestion.cours.dao;

import com.gestion.cours.model.User;
import com.gestion.cours.model.User.TypeUtilisateur;
import com.gestion.cours.util.DatabaseConfig;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des utilisateurs (professeurs et étudiants)
 */
public class UserDAO extends BaseDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    /**
     * Authentifie un utilisateur
     */
    public User authentifier(String email, String motDePasse) {
        String sql = "SELECT * FROM users WHERE email = ? AND actif = TRUE";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String hashedPassword = rs.getString("mot_de_passe");
                
                // Vérifier le mot de passe avec BCrypt
                if (BCrypt.checkpw(motDePasse, hashedPassword)) {
                    User user = mapResultSetToUser(rs);
                    
                    // Mettre à jour la dernière connexion
                    updateDerniereConnexion(user.getId());
                    user.setDerniereConnexion(LocalDateTime.now());
                    
                    logger.info("Authentification réussie pour l'utilisateur: {}", email);
                    return user;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Erreur lors de l'authentification de l'utilisateur: {}", email, e);
        }
        
        logger.warn("Échec d'authentification pour l'utilisateur: {}", email);
        return null;
    }

    /**
     * Créer un nouvel utilisateur
     */
    public User creer(User user) {
        String sql = "INSERT INTO users (nom, prenom, email, mot_de_passe, type_utilisateur, telephone, date_naissance, adresse) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Hasher le mot de passe avec BCrypt
            String hashedPassword = BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt());
            
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, hashedPassword);
            stmt.setString(5, user.getTypeUtilisateur().name());
            stmt.setString(6, user.getTelephone());
            stmt.setDate(7, user.getDateNaissance() != null ? Date.valueOf(user.getDateNaissance()) : null);
            stmt.setString(8, user.getAdresse());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                    logger.info("Utilisateur créé avec succès: {}", user.getEmail());
                    return user;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la création de l'utilisateur: {}", user.getEmail(), e);
        }
        return null;
    }

    /**
     * Trouver un utilisateur par ID
     */
    public User trouverParId(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ? AND actif = TRUE";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de l'utilisateur par ID: {}", id, e);
        }
        return null;
    }

    /**
     * Trouver un utilisateur par email
     */
    public User trouverParEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND actif = TRUE";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de l'utilisateur par email: {}", email, e);
        }
        return null;
    }

    /**
     * Lister tous les professeurs
     */
    public List<User> listerProfesseurs() {
        return listerParType(TypeUtilisateur.PROFESSEUR);
    }

    /**
     * Lister tous les étudiants
     */
    public List<User> listerEtudiants() {
        return listerParType(TypeUtilisateur.ETUDIANT);
    }

    /**
     * Lister les utilisateurs par type
     */
    public List<User> listerParType(TypeUtilisateur type) {
        String sql = "SELECT * FROM users WHERE type_utilisateur = ? AND actif = TRUE ORDER BY nom, prenom";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des utilisateurs de type: {}", type, e);
        }
        return users;
    }

    /**
     * Mettre à jour un utilisateur
     */
    public boolean mettreAJour(User user) {
        String sql = "UPDATE users SET nom = ?, prenom = ?, telephone = ?, date_naissance = ?, adresse = ?, " +
                     "date_modification = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getTelephone());
            stmt.setDate(4, user.getDateNaissance() != null ? Date.valueOf(user.getDateNaissance()) : null);
            stmt.setString(5, user.getAdresse());
            stmt.setInt(6, user.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Utilisateur mis à jour avec succès: {}", user.getEmail());
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de l'utilisateur: {}", user.getId(), e);
        }
        return false;
    }

    /**
     * Changer le mot de passe d'un utilisateur
     */
    public boolean changerMotDePasse(Integer userId, String nouveauMotDePasse) {
        String sql = "UPDATE users SET mot_de_passe = ?, date_modification = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String hashedPassword = BCrypt.hashpw(nouveauMotDePasse, BCrypt.gensalt());
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Mot de passe mis à jour pour l'utilisateur: {}", userId);
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Erreur lors du changement de mot de passe pour l'utilisateur: {}", userId, e);
        }
        return false;
    }

    /**
     * Vérifier si un email existe déjà
     */
    public boolean emailExiste(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la vérification de l'email: {}", email, e);
        }
        return false;
    }

    /**
     * Désactiver un utilisateur
     */
    public boolean desactiver(Integer userId) {
        String sql = "UPDATE users SET actif = FALSE, date_modification = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Utilisateur désactivé: {}", userId);
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la désactivation de l'utilisateur: {}", userId, e);
        }
        return false;
    }

    /**
     * Mettre à jour la dernière connexion
     */
    private void updateDerniereConnexion(Integer userId) {
        String sql = "UPDATE users SET derniere_connexion = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            logger.warn("Erreur lors de la mise à jour de la dernière connexion: {}", userId, e);
        }
    }

    /**
     * Mapper un ResultSet vers un objet User
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("email"));
        user.setMotDePasse(rs.getString("mot_de_passe"));
        user.setTypeUtilisateur(TypeUtilisateur.valueOf(rs.getString("type_utilisateur")));
        user.setTelephone(rs.getString("telephone"));
        
        Date dateNaissance = rs.getDate("date_naissance");
        if (dateNaissance != null) {
            user.setDateNaissance(dateNaissance.toLocalDate());
        }
        
        user.setAdresse(rs.getString("adresse"));
        user.setActif(rs.getBoolean("actif"));
        user.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        user.setDateModification(rs.getTimestamp("date_modification").toLocalDateTime());
        
        Timestamp derniereConnexion = rs.getTimestamp("derniere_connexion");
        if (derniereConnexion != null) {
            user.setDerniereConnexion(derniereConnexion.toLocalDateTime());
        }
        
        return user;
    }
}