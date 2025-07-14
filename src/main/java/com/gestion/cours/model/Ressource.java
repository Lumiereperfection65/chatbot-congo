package com.gestion.cours.model;

import java.time.LocalDateTime;

/**
 * Classe représentant une ressource (fichier PDF, document) de cours
 */
public class Ressource {
    private Integer id;
    private Integer coursId;
    private Integer chapitreId;
    private String nomFichier; // Nom du fichier sur le système
    private String nomOriginal; // Nom original du fichier
    private String cheminFichier; // Chemin vers le fichier
    private String typeFichier; // PDF, DOC, etc.
    private Long tailleFichier; // Taille en octets
    private String description;
    private boolean visible; // Visibilité progressive
    private Integer ordreAffichage;
    private LocalDateTime dateUpload;

    // Objets liés pour affichage
    private Cours cours;
    private Chapitre chapitre;

    // Constructeur par défaut
    public Ressource() {
        this.visible = false;
        this.ordreAffichage = 1;
        this.typeFichier = "PDF";
        this.dateUpload = LocalDateTime.now();
    }

    // Constructeur avec paramètres essentiels
    public Ressource(String nomFichier, String nomOriginal, String cheminFichier) {
        this();
        this.nomFichier = nomFichier;
        this.nomOriginal = nomOriginal;
        this.cheminFichier = cheminFichier;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCoursId() {
        return coursId;
    }

    public void setCoursId(Integer coursId) {
        this.coursId = coursId;
    }

    public Integer getChapitreId() {
        return chapitreId;
    }

    public void setChapitreId(Integer chapitreId) {
        this.chapitreId = chapitreId;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getNomOriginal() {
        return nomOriginal;
    }

    public void setNomOriginal(String nomOriginal) {
        this.nomOriginal = nomOriginal;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    public String getTypeFichier() {
        return typeFichier;
    }

    public void setTypeFichier(String typeFichier) {
        this.typeFichier = typeFichier;
    }

    public Long getTailleFichier() {
        return tailleFichier;
    }

    public void setTailleFichier(Long tailleFichier) {
        this.tailleFichier = tailleFichier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Integer getOrdreAffichage() {
        return ordreAffichage;
    }

    public void setOrdreAffichage(Integer ordreAffichage) {
        this.ordreAffichage = ordreAffichage;
    }

    public LocalDateTime getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(LocalDateTime dateUpload) {
        this.dateUpload = dateUpload;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    public Chapitre getChapitre() {
        return chapitre;
    }

    public void setChapitre(Chapitre chapitre) {
        this.chapitre = chapitre;
    }

    // Méthodes utilitaires
    public String getTailleFormatee() {
        if (tailleFichier == null || tailleFichier == 0) {
            return "Taille inconnue";
        }

        double taille = tailleFichier.doubleValue();
        String[] unites = {"B", "KB", "MB", "GB"};
        int uniteIndex = 0;

        while (taille >= 1024 && uniteIndex < unites.length - 1) {
            taille /= 1024;
            uniteIndex++;
        }

        return String.format("%.1f %s", taille, unites[uniteIndex]);
    }

    public boolean isPDF() {
        return "PDF".equalsIgnoreCase(typeFichier);
    }

    public boolean isImage() {
        return typeFichier != null && 
               (typeFichier.toUpperCase().equals("JPG") || 
                typeFichier.toUpperCase().equals("JPEG") || 
                typeFichier.toUpperCase().equals("PNG") || 
                typeFichier.toUpperCase().equals("GIF"));
    }

    public String getExtension() {
        if (nomOriginal != null && nomOriginal.contains(".")) {
            return nomOriginal.substring(nomOriginal.lastIndexOf(".") + 1).toUpperCase();
        }
        return typeFichier != null ? typeFichier.toUpperCase() : "UNKNOWN";
    }

    public boolean isValide() {
        return nomFichier != null && !nomFichier.trim().isEmpty() &&
               nomOriginal != null && !nomOriginal.trim().isEmpty() &&
               cheminFichier != null && !cheminFichier.trim().isEmpty();
    }

    @Override
    public String toString() {
        return nomOriginal + " (" + getTailleFormatee() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ressource that = (Ressource) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}