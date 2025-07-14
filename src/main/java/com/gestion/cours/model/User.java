package com.gestion.cours.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Classe représentant un utilisateur (professeur ou étudiant)
 */
public class User {
    public enum TypeUtilisateur {
        PROFESSEUR, ETUDIANT
    }

    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse; // Hashé avec BCrypt
    private TypeUtilisateur typeUtilisateur;
    private String telephone;
    private LocalDate dateNaissance;
    private String adresse;
    private boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private LocalDateTime derniereConnexion;

    // Constructeur par défaut
    public User() {
        this.actif = true;
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    // Constructeur avec paramètres essentiels
    public User(String nom, String prenom, String email, String motDePasse, TypeUtilisateur typeUtilisateur) {
        this();
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.typeUtilisateur = typeUtilisateur;
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

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public TypeUtilisateur getTypeUtilisateur() {
        return typeUtilisateur;
    }

    public void setTypeUtilisateur(TypeUtilisateur typeUtilisateur) {
        this.typeUtilisateur = typeUtilisateur;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
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

    public LocalDateTime getDerniereConnexion() {
        return derniereConnexion;
    }

    public void setDerniereConnexion(LocalDateTime derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }

    // Méthodes utilitaires
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public boolean isProfesseur() {
        return typeUtilisateur == TypeUtilisateur.PROFESSEUR;
    }

    public boolean isEtudiant() {
        return typeUtilisateur == TypeUtilisateur.ETUDIANT;
    }

    public int getAge() {
        if (dateNaissance == null) return 0;
        return LocalDate.now().getYear() - dateNaissance.getYear();
    }

    public boolean isValide() {
        return nom != null && !nom.trim().isEmpty() &&
               prenom != null && !prenom.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               motDePasse != null && !motDePasse.trim().isEmpty() &&
               typeUtilisateur != null;
    }

    @Override
    public String toString() {
        return getNomComplet() + " (" + typeUtilisateur.name() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User that = (User) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}