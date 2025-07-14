package com.gestion.cours.model;

import java.time.LocalDateTime;

/**
 * Classe représentant une catégorie de cours
 */
public class Categorie {
    private Integer id;
    private String nom;
    private String description;
    private boolean actif;
    private LocalDateTime dateCreation;

    // Constructeur par défaut
    public Categorie() {
        this.actif = true;
        this.dateCreation = LocalDateTime.now();
    }

    // Constructeur avec paramètres
    public Categorie(String nom, String description) {
        this();
        this.nom = nom;
        this.description = description;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    // Méthodes utilitaires
    public boolean isValide() {
        return nom != null && !nom.trim().isEmpty();
    }

    @Override
    public String toString() {
        return nom;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Categorie that = (Categorie) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}