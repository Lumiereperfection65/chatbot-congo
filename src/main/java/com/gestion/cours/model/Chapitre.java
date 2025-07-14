package com.gestion.cours.model;

import java.time.LocalDateTime;

/**
 * Classe représentant un chapitre/section de cours
 */
public class Chapitre {
    private Integer id;
    private Integer coursId;
    private String titre;
    private String description;
    private Integer ordreAffichage;
    private boolean visible; // Visibilité progressive
    private LocalDateTime dateCreation;

    // Objet lié pour affichage
    private Cours cours;

    // Constructeur par défaut
    public Chapitre() {
        this.visible = false;
        this.dateCreation = LocalDateTime.now();
    }

    // Constructeur avec paramètres essentiels
    public Chapitre(Integer coursId, String titre, Integer ordreAffichage) {
        this();
        this.coursId = coursId;
        this.titre = titre;
        this.ordreAffichage = ordreAffichage;
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

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrdreAffichage() {
        return ordreAffichage;
    }

    public void setOrdreAffichage(Integer ordreAffichage) {
        this.ordreAffichage = ordreAffichage;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    // Méthodes utilitaires
    public boolean isValide() {
        return coursId != null &&
               titre != null && !titre.trim().isEmpty() &&
               ordreAffichage != null && ordreAffichage > 0;
    }

    @Override
    public String toString() {
        return "Chapitre " + ordreAffichage + ": " + titre;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Chapitre that = (Chapitre) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}