package com.gestion.cours.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Classe représentant un cours dans la plateforme d'apprentissage
 */
public class Cours {
    public enum Niveau {
        DEBUTANT, INTERMEDIAIRE, AVANCE
    }

    private Integer id;
    private String titre;
    private String presentation; // Description détaillée du cours
    private String motsCles; // Mots clés séparés par des virgules
    private String publicVise; // Description du public ciblé
    private String prerequis; // Prérequis pour suivre le cours
    private Niveau niveau;
    private Integer dureeHeures;
    private BigDecimal prix;
    private Integer professeurId;
    private Integer categorieId;
    private boolean visible; // Visibilité progressive du cours
    private String imageCouverture; // Chemin vers l'image de couverture
    private LocalDate datePublication;
    private boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Objets liés (pour affichage)
    private User professeur;
    private Categorie categorie;

    // Constructeur par défaut
    public Cours() {
        this.actif = true;
        this.visible = false;
        this.prix = BigDecimal.ZERO;
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    // Constructeur avec paramètres essentiels
    public Cours(String titre, String presentation, Niveau niveau, Integer professeurId) {
        this();
        this.titre = titre;
        this.presentation = presentation;
        this.niveau = niveau;
        this.professeurId = professeurId;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public String getMotsCles() {
        return motsCles;
    }

    public void setMotsCles(String motsCles) {
        this.motsCles = motsCles;
    }

    public String getPublicVise() {
        return publicVise;
    }

    public void setPublicVise(String publicVise) {
        this.publicVise = publicVise;
    }

    public String getPrerequis() {
        return prerequis;
    }

    public void setPrerequis(String prerequis) {
        this.prerequis = prerequis;
    }

    public Niveau getNiveau() {
        return niveau;
    }

    public void setNiveau(Niveau niveau) {
        this.niveau = niveau;
    }

    public Integer getDureeHeures() {
        return dureeHeures;
    }

    public void setDureeHeures(Integer dureeHeures) {
        this.dureeHeures = dureeHeures;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public Integer getProfesseurId() {
        return professeurId;
    }

    public void setProfesseurId(Integer professeurId) {
        this.professeurId = professeurId;
    }

    public Integer getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(Integer categorieId) {
        this.categorieId = categorieId;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getImageCouverture() {
        return imageCouverture;
    }

    public void setImageCouverture(String imageCouverture) {
        this.imageCouverture = imageCouverture;
    }

    public LocalDate getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
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

    public User getProfesseur() {
        return professeur;
    }

    public void setProfesseur(User professeur) {
        this.professeur = professeur;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    // Méthodes utilitaires
    public List<String> getListeMotsCles() {
        if (motsCles == null || motsCles.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.asList(motsCles.split(","))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public void setListeMotsCles(List<String> motsClesList) {
        if (motsClesList == null || motsClesList.isEmpty()) {
            this.motsCles = null;
        } else {
            this.motsCles = String.join(", ", motsClesList);
        }
    }

    public boolean isPublie() {
        return datePublication != null && 
               !datePublication.isAfter(LocalDate.now()) && 
               visible && actif;
    }

    public boolean isEnPreparation() {
        return !visible || datePublication == null || 
               datePublication.isAfter(LocalDate.now());
    }

    public String getStatutCours() {
        if (!actif) return "Inactif";
        if (isPublie()) return "Publié";
        if (isEnPreparation()) return "En préparation";
        return "Brouillon";
    }

    public boolean isGratuit() {
        return prix == null || prix.compareTo(BigDecimal.ZERO) == 0;
    }

    public String getPrixFormate() {
        if (isGratuit()) {
            return "Gratuit";
        }
        return String.format("%.2f €", prix);
    }

    public boolean isValide() {
        return titre != null && !titre.trim().isEmpty() &&
               presentation != null && !presentation.trim().isEmpty() &&
               niveau != null &&
               professeurId != null;
    }

    @Override
    public String toString() {
        return titre + " (" + niveau + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cours that = (Cours) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}