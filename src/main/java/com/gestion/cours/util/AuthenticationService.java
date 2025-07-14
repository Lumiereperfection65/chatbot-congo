package com.gestion.cours.util;

import com.gestion.cours.dao.UserDAO;
import com.gestion.cours.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service d'authentification pour gérer les sessions utilisateur
 */
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private static AuthenticationService instance;
    private UserDAO userDAO;
    private User currentUser;

    private AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    /**
     * Authentifier un utilisateur
     */
    public boolean authentifier(String email, String motDePasse) {
        try {
            User user = userDAO.authentifier(email, motDePasse);
            if (user != null) {
                this.currentUser = user;
                logger.info("Utilisateur authentifié avec succès: {}", email);
                return true;
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'authentification: {}", email, e);
        }
        return false;
    }

    /**
     * Inscrire un nouvel utilisateur
     */
    public boolean inscrire(User newUser) {
        try {
            // Vérifier si l'email existe déjà
            if (userDAO.emailExiste(newUser.getEmail())) {
                logger.warn("Tentative d'inscription avec un email existant: {}", newUser.getEmail());
                return false;
            }

            // Valider les données
            if (!newUser.isValide()) {
                logger.warn("Tentative d'inscription avec des données invalides");
                return false;
            }

            User user = userDAO.creer(newUser);
            if (user != null) {
                logger.info("Nouvel utilisateur inscrit: {}", user.getEmail());
                return true;
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'inscription de l'utilisateur: {}", newUser.getEmail(), e);
        }
        return false;
    }

    /**
     * Se déconnecter
     */
    public void deconnecter() {
        if (currentUser != null) {
            logger.info("Déconnexion de l'utilisateur: {}", currentUser.getEmail());
            currentUser = null;
        }
    }

    /**
     * Vérifier si un utilisateur est connecté
     */
    public boolean estConnecte() {
        return currentUser != null;
    }

    /**
     * Obtenir l'utilisateur actuellement connecté
     */
    public User getUtilisateurConnecte() {
        return currentUser;
    }

    /**
     * Vérifier si l'utilisateur connecté est un professeur
     */
    public boolean estProfesseur() {
        return currentUser != null && currentUser.isProfesseur();
    }

    /**
     * Vérifier si l'utilisateur connecté est un étudiant
     */
    public boolean estEtudiant() {
        return currentUser != null && currentUser.isEtudiant();
    }

    /**
     * Obtenir l'ID de l'utilisateur connecté
     */
    public Integer getIdUtilisateurConnecte() {
        return currentUser != null ? currentUser.getId() : null;
    }

    /**
     * Changer le mot de passe de l'utilisateur connecté
     */
    public boolean changerMotDePasse(String ancienMotDePasse, String nouveauMotDePasse) {
        if (currentUser == null) {
            return false;
        }

        try {
            // Vérifier l'ancien mot de passe
            User verification = userDAO.authentifier(currentUser.getEmail(), ancienMotDePasse);
            if (verification == null) {
                logger.warn("Tentative de changement de mot de passe avec un ancien mot de passe incorrect");
                return false;
            }

            // Changer le mot de passe
            boolean success = userDAO.changerMotDePasse(currentUser.getId(), nouveauMotDePasse);
            if (success) {
                logger.info("Mot de passe changé avec succès pour l'utilisateur: {}", currentUser.getEmail());
            }
            return success;
        } catch (Exception e) {
            logger.error("Erreur lors du changement de mot de passe", e);
        }
        return false;
    }

    /**
     * Mettre à jour les informations de l'utilisateur connecté
     */
    public boolean mettreAJourProfil(User userMisAJour) {
        if (currentUser == null || !currentUser.getId().equals(userMisAJour.getId())) {
            return false;
        }

        try {
            boolean success = userDAO.mettreAJour(userMisAJour);
            if (success) {
                // Mettre à jour les informations en session
                currentUser.setNom(userMisAJour.getNom());
                currentUser.setPrenom(userMisAJour.getPrenom());
                currentUser.setTelephone(userMisAJour.getTelephone());
                currentUser.setDateNaissance(userMisAJour.getDateNaissance());
                currentUser.setAdresse(userMisAJour.getAdresse());
                logger.info("Profil mis à jour pour l'utilisateur: {}", currentUser.getEmail());
            }
            return success;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du profil", e);
        }
        return false;
    }

    /**
     * Valider un email
     */
    public static boolean validerEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Valider un mot de passe
     */
    public static boolean validerMotDePasse(String motDePasse) {
        if (motDePasse == null) {
            return false;
        }
        
        // Au moins 6 caractères
        return motDePasse.length() >= 6;
    }

    /**
     * Obtenir les critères de mot de passe
     */
    public static String getCriteresMotDePasse() {
        return "Le mot de passe doit contenir au moins 6 caractères.";
    }
}