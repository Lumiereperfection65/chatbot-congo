package com.gestion.cours.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Classe représentant un professeur
 */
public class Professeur {
    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String specialite;
    private LocalDate dateEmbauche;
    private BigDecimal salaire;
    private boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Constructeur par défaut
    public Professeur() {
        this.actif = true;
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    // Constructeur avec paramètres essentiels
    public Professeur(String nom, String prenom, String email, String specialite) {
        this();
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.specialite = specialite;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public BigDecimal getSalaire() {
        return salaire;
    }

    public void setSalaire(BigDecimal salaire) {
        this.salaire = salaire;
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

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    // Méthodes utilitaires
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public boolean isValide() {
        return nom != null && !nom.trim().isEmpty() &&
               prenom != null && !prenom.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               specialite != null && !specialite.trim().isEmpty();
    }

    @Override
    public String toString() {
        return getNomComplet() + " (" + specialite + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Professeur that = (Professeur) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}