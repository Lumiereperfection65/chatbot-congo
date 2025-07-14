package com.gestion.cours.dao;

import com.gestion.cours.model.Cours;
import com.gestion.cours.model.Cours.Niveau;
import com.gestion.cours.model.User;
import com.gestion.cours.model.Categorie;
import com.gestion.cours.util.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des cours
 */
public class CoursDAO extends BaseDAO {
    private static final Logger logger = LoggerFactory.getLogger(CoursDAO.class);

    /**
     * Créer un nouveau cours
     */
    public Cours creer(Cours cours) {
        String sql = "INSERT INTO cours (titre, presentation, mots_cles, public_vise, prerequis, niveau, " +
                     "duree_heures, prix, professeur_id, categorie_id, visible, image_couverture, date_publication) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cours.getTitre());
            stmt.setString(2, cours.getPresentation());
            stmt.setString(3, cours.getMotsCles());
            stmt.setString(4, cours.getPublicVise());
            stmt.setString(5, cours.getPrerequis());
            stmt.setString(6, cours.getNiveau().name());
            stmt.setObject(7, cours.getDureeHeures());
            stmt.setBigDecimal(8, cours.getPrix());
            stmt.setInt(9, cours.getProfesseurId());
            stmt.setObject(10, cours.getCategorieId());
            stmt.setBoolean(11, cours.isVisible());
            stmt.setString(12, cours.getImageCouverture());
            stmt.setDate(13, cours.getDatePublication() != null ? Date.valueOf(cours.getDatePublication()) : null);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    cours.setId(generatedKeys.getInt(1));
                    logger.info("Cours créé avec succès: {}", cours.getTitre());
                    return cours;
                }
            }

        } catch (SQLException e) {
            logger.error("Erreur lors de la création du cours: {}", cours.getTitre(), e);
        }
        return null;
    }

    /**
     * Trouver un cours par ID
     */
    public Cours trouverParId(Integer id) {
        String sql = "SELECT c.*, u.nom as prof_nom, u.prenom as prof_prenom, u.email as prof_email, " +
                     "cat.nom as cat_nom, cat.description as cat_description " +
                     "FROM cours c " +
                     "LEFT JOIN users u ON c.professeur_id = u.id " +
                     "LEFT JOIN categories cat ON c.categorie_id = cat.id " +
                     "WHERE c.id = ? AND c.actif = TRUE";

        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCours(rs);
            }

        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du cours par ID: {}", id, e);
        }
        return null;
    }

    /**
     * Lister tous les cours
     */
    public List<Cours> listerTous() {
        String sql = "SELECT c.*, u.nom as prof_nom, u.prenom as prof_prenom, u.email as prof_email, " +
                     "cat.nom as cat_nom, cat.description as cat_description " +
                     "FROM cours c " +
                     "LEFT JOIN users u ON c.professeur_id = u.id " +
                     "LEFT JOIN categories cat ON c.categorie_id = cat.id " +
                     "WHERE c.actif = TRUE ORDER BY c.date_creation DESC";

        return executeQuery(sql);
    }

    /**
     * Lister les cours visibles (publiés)
     */
    public List<Cours> listerCoursVisibles() {
        String sql = "SELECT c.*, u.nom as prof_nom, u.prenom as prof_prenom, u.email as prof_email, " +
                     "cat.nom as cat_nom, cat.description as cat_description " +
                     "FROM cours c " +
                     "LEFT JOIN users u ON c.professeur_id = u.id " +
                     "LEFT JOIN categories cat ON c.categorie_id = cat.id " +
                     "WHERE c.actif = TRUE AND c.visible = TRUE " +
                     "AND (c.date_publication IS NULL OR c.date_publication <= CURRENT_DATE) " +
                     "ORDER BY c.date_creation DESC";

        return executeQuery(sql);
    }

    /**
     * Lister les cours par professeur
     */
    public List<Cours> listerParProfesseur(Integer professeurId) {
        String sql = "SELECT c.*, u.nom as prof_nom, u.prenom as prof_prenom, u.email as prof_email, " +
                     "cat.nom as cat_nom, cat.description as cat_description " +
                     "FROM cours c " +
                     "LEFT JOIN users u ON c.professeur_id = u.id " +
                     "LEFT JOIN categories cat ON c.categorie_id = cat.id " +
                     "WHERE c.professeur_id = ? AND c.actif = TRUE " +
                     "ORDER BY c.date_creation DESC";

        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, professeurId);
            return executeQueryWithStatement(stmt);

        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche des cours par professeur: {}", professeurId, e);
        }
        return new ArrayList<>();
    }

    /**
     * Lister les cours par catégorie
     */
    public List<Cours> listerParCategorie(Integer categorieId) {
        String sql = "SELECT c.*, u.nom as prof_nom, u.prenom as prof_prenom, u.email as prof_email, " +
                     "cat.nom as cat_nom, cat.description as cat_description " +
                     "FROM cours c " +
                     "LEFT JOIN users u ON c.professeur_id = u.id " +
                     "LEFT JOIN categories cat ON c.categorie_id = cat.id " +
                     "WHERE c.categorie_id = ? AND c.actif = TRUE AND c.visible = TRUE " +
                     "ORDER BY c.date_creation DESC";

        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categorieId);
            return executeQueryWithStatement(stmt);

        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche des cours par catégorie: {}", categorieId, e);
        }
        return new ArrayList<>();
    }

    /**
     * Rechercher des cours par critères
     */
    public List<Cours> rechercher(String motsCles, Niveau niveau, Integer categorieId, boolean gratuitSeulement) {
        StringBuilder sql = new StringBuilder(
            "SELECT c.*, u.nom as prof_nom, u.prenom as prof_prenom, u.email as prof_email, " +
            "cat.nom as cat_nom, cat.description as cat_description " +
            "FROM cours c " +
            "LEFT JOIN users u ON c.professeur_id = u.id " +
            "LEFT JOIN categories cat ON c.categorie_id = cat.id " +
            "WHERE c.actif = TRUE AND c.visible = TRUE "
        );

        List<Object> params = new ArrayList<>();

        if (motsCles != null && !motsCles.trim().isEmpty()) {
            sql.append("AND (c.titre LIKE ? OR c.presentation LIKE ? OR c.mots_cles LIKE ?) ");
            String searchPattern = "%" + motsCles.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (niveau != null) {
            sql.append("AND c.niveau = ? ");
            params.add(niveau.name());
        }

        if (categorieId != null) {
            sql.append("AND c.categorie_id = ? ");
            params.add(categorieId);
        }

        if (gratuitSeulement) {
            sql.append("AND (c.prix IS NULL OR c.prix = 0) ");
        }

        sql.append("ORDER BY c.date_creation DESC");

        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            return executeQueryWithStatement(stmt);

        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de cours", e);
        }
        return new ArrayList<>();
    }

    /**
     * Mettre à jour un cours
     */
    public boolean mettreAJour(Cours cours) {
        String sql = "UPDATE cours SET titre = ?, presentation = ?, mots_cles = ?, public_vise = ?, " +
                     "prerequis = ?, niveau = ?, duree_heures = ?, prix = ?, categorie_id = ?, " +
                     "visible = ?, image_couverture = ?, date_publication = ?, date_modification = CURRENT_TIMESTAMP " +
                     "WHERE id = ?";

        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cours.getTitre());
            stmt.setString(2, cours.getPresentation());
            stmt.setString(3, cours.getMotsCles());
            stmt.setString(4, cours.getPublicVise());
            stmt.setString(5, cours.getPrerequis());
            stmt.setString(6, cours.getNiveau().name());
            stmt.setObject(7, cours.getDureeHeures());
            stmt.setBigDecimal(8, cours.getPrix());
            stmt.setObject(9, cours.getCategorieId());
            stmt.setBoolean(10, cours.isVisible());
            stmt.setString(11, cours.getImageCouverture());
            stmt.setDate(12, cours.getDatePublication() != null ? Date.valueOf(cours.getDatePublication()) : null);
            stmt.setInt(13, cours.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Cours mis à jour avec succès: {}", cours.getTitre());
                return true;
            }

        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du cours: {}", cours.getId(), e);
        }
        return false;
    }

    /**
     * Changer la visibilité d'un cours
     */
    public boolean changerVisibilite(Integer coursId, boolean visible) {
        String sql = "UPDATE cours SET visible = ?, date_modification = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, visible);
            stmt.setInt(2, coursId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Visibilité du cours {} changée à: {}", coursId, visible);
                return true;
            }

        } catch (SQLException e) {
            logger.error("Erreur lors du changement de visibilité du cours: {}", coursId, e);
        }
        return false;
    }

    /**
     * Supprimer un cours (suppression logique)
     */
    public boolean supprimer(Integer coursId) {
        String sql = "UPDATE cours SET actif = FALSE, date_modification = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, coursId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Cours supprimé (logiquement): {}", coursId);
                return true;
            }

        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression du cours: {}", coursId, e);
        }
        return false;
    }

    /**
     * Compter le nombre d'étudiants inscrits à un cours
     */
    public int compterEtudiantsInscrits(Integer coursId) {
        String sql = "SELECT COUNT(*) FROM inscriptions WHERE cours_id = ? AND statut = 'ACCEPTEE'";

        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, coursId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            logger.error("Erreur lors du comptage des étudiants inscrits: {}", coursId, e);
        }
        return 0;
    }

    /**
     * Exécuter une requête sans paramètres
     */
    private List<Cours> executeQuery(String sql) {
        List<Cours> cours = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            return executeQueryWithStatement(stmt);

        } catch (SQLException e) {
            logger.error("Erreur lors de l'exécution de la requête", e);
        }
        return cours;
    }

    /**
     * Exécuter une requête avec un PreparedStatement
     */
    private List<Cours> executeQueryWithStatement(PreparedStatement stmt) throws SQLException {
        List<Cours> cours = new ArrayList<>();
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            cours.add(mapResultSetToCours(rs));
        }

        return cours;
    }

    /**
     * Mapper un ResultSet vers un objet Cours
     */
    private Cours mapResultSetToCours(ResultSet rs) throws SQLException {
        Cours cours = new Cours();
        cours.setId(rs.getInt("id"));
        cours.setTitre(rs.getString("titre"));
        cours.setPresentation(rs.getString("presentation"));
        cours.setMotsCles(rs.getString("mots_cles"));
        cours.setPublicVise(rs.getString("public_vise"));
        cours.setPrerequis(rs.getString("prerequis"));
        cours.setNiveau(Niveau.valueOf(rs.getString("niveau")));
        cours.setDureeHeures(rs.getObject("duree_heures", Integer.class));
        cours.setPrix(rs.getBigDecimal("prix"));
        cours.setProfesseurId(rs.getInt("professeur_id"));
        cours.setCategorieId(rs.getObject("categorie_id", Integer.class));
        cours.setVisible(rs.getBoolean("visible"));
        cours.setImageCouverture(rs.getString("image_couverture"));

        Date datePublication = rs.getDate("date_publication");
        if (datePublication != null) {
            cours.setDatePublication(datePublication.toLocalDate());
        }

        cours.setActif(rs.getBoolean("actif"));
        cours.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        cours.setDateModification(rs.getTimestamp("date_modification").toLocalDateTime());

        // Mapper le professeur si présent
        String profNom = rs.getString("prof_nom");
        if (profNom != null) {
            User professeur = new User();
            professeur.setId(rs.getInt("professeur_id"));
            professeur.setNom(profNom);
            professeur.setPrenom(rs.getString("prof_prenom"));
            professeur.setEmail(rs.getString("prof_email"));
            cours.setProfesseur(professeur);
        }

        // Mapper la catégorie si présente
        String catNom = rs.getString("cat_nom");
        if (catNom != null) {
            Categorie categorie = new Categorie();
            categorie.setId(rs.getInt("categorie_id"));
            categorie.setNom(catNom);
            categorie.setDescription(rs.getString("cat_description"));
            cours.setCategorie(categorie);
        }

        return cours;
    }
}