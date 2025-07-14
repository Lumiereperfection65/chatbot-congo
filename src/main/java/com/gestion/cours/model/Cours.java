package com.gestion.cours.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Classe représentant un cours
 */
public class Cours {
    private Integer id;
    private String titre;
    private String description;
    private Integer dureeHeures;
    private BigDecimal prix;
    private String niveau;
    private Integer capaciteMax;
    private Integer professeurId;
    private Integer categorieId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalTime horaireDebut;
    private LocalTime horaireFin;
    private String joursSemaine;
    private String salle;
    private boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Objets liés (pour affichage)
    private Professeur professeur;
    private Categorie categorie;

    // Constructeur par défaut
    public Cours() {
        this.actif = true;
        this.capaciteMax = 30;
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    // Constructeur avec paramètres essentiels
    public Cours(String titre, String description, Integer dureeHeures, BigDecimal prix, String niveau) {
        this();
        this.titre = titre;
        this.description = description;
        this.dureeHeures = dureeHeures;
        this.prix = prix;
        this.niveau = niveau;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public Integer getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(Integer capaciteMax) {
        this.capaciteMax = capaciteMax;
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

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public LocalTime getHoraireDebut() {
        return horaireDebut;
    }

    public void setHoraireDebut(LocalTime horaireDebut) {
        this.horaireDebut = horaireDebut;
    }

    public LocalTime getHoraireFin() {
        return horaireFin;
    }

    public void setHoraireFin(LocalTime horaireFin) {
        this.horaireFin = horaireFin;
    }

    public String getJoursSemaine() {
        return joursSemaine;
    }

    public void setJoursSemaine(String joursSemaine) {
        this.joursSemaine = joursSemaine;
    }

    public String getSalle() {
        return salle;
    }

    public void setSalle(String salle) {
        this.salle = salle;
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

    public Professeur getProfesseur() {
        return professeur;
    }

    public void setProfesseur(Professeur professeur) {
        this.professeur = professeur;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    // Méthodes utilitaires
    public boolean isEnCours() {
        LocalDate today = LocalDate.now();
        return dateDebut != null && dateFin != null && 
               !today.isBefore(dateDebut) && !today.isAfter(dateFin);
    }

    public boolean isTermine() {
        LocalDate today = LocalDate.now();
        return dateFin != null && today.isAfter(dateFin);
    }

    public boolean isAVenir() {
        LocalDate today = LocalDate.now();
        return dateDebut != null && today.isBefore(dateDebut);
    }

    public String getStatutCours() {
        if (isTermine()) return "Terminé";
        if (isEnCours()) return "En cours";
        if (isAVenir()) return "À venir";
        return "Non planifié";
    }

    public boolean isValide() {
        return titre != null && !titre.trim().isEmpty() &&
               dureeHeures != null && dureeHeures > 0 &&
               prix != null && prix.compareTo(BigDecimal.ZERO) >= 0 &&
               niveau != null && !niveau.trim().isEmpty();
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