package com.gestion.cours.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Classe représentant une inscription d'un étudiant à un cours
 */
public class Inscription {
    
    public enum StatutInscription {
        EN_ATTENTE("En attente"),
        CONFIRMEE("Confirmée"),
        ANNULEE("Annulée"),
        TERMINEE("Terminée");
        
        private final String libelle;
        
        StatutInscription(String libelle) {
            this.libelle = libelle;
        }
        
        public String getLibelle() {
            return libelle;
        }
        
        @Override
        public String toString() {
            return libelle;
        }
    }
    
    private Integer id;
    private Integer etudiantId;
    private Integer coursId;
    private LocalDate dateInscription;
    private StatutInscription statut;
    private BigDecimal noteFinale;
    private String commentaires;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    
    // Objets liés (pour affichage)
    private Etudiant etudiant;
    private Cours cours;

    // Constructeur par défaut
    public Inscription() {
        this.statut = StatutInscription.EN_ATTENTE;
        this.dateInscription = LocalDate.now();
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    // Constructeur avec paramètres essentiels
    public Inscription(Integer etudiantId, Integer coursId) {
        this();
        this.etudiantId = etudiantId;
        this.coursId = coursId;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEtudiantId() {
        return etudiantId;
    }

    public void setEtudiantId(Integer etudiantId) {
        this.etudiantId = etudiantId;
    }

    public Integer getCoursId() {
        return coursId;
    }

    public void setCoursId(Integer coursId) {
        this.coursId = coursId;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public StatutInscription getStatut() {
        return statut;
    }

    public void setStatut(StatutInscription statut) {
        this.statut = statut;
    }

    public BigDecimal getNoteFinale() {
        return noteFinale;
    }

    public void setNoteFinale(BigDecimal noteFinale) {
        this.noteFinale = noteFinale;
    }

    public String getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
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

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    // Méthodes utilitaires
    public boolean isActive() {
        return statut == StatutInscription.CONFIRMEE || statut == StatutInscription.EN_ATTENTE;
    }

    public boolean isTerminee() {
        return statut == StatutInscription.TERMINEE;
    }

    public boolean isAnnulee() {
        return statut == StatutInscription.ANNULEE;
    }

    public String getStatutTexte() {
        return statut != null ? statut.getLibelle() : "Non défini";
    }

    public boolean hasNoteFinale() {
        return noteFinale != null && noteFinale.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean isValide() {
        return etudiantId != null && coursId != null && dateInscription != null;
    }

    @Override
    public String toString() {
        String etudiantNom = etudiant != null ? etudiant.getNomComplet() : "ID: " + etudiantId;
        String coursNom = cours != null ? cours.getTitre() : "ID: " + coursId;
        return etudiantNom + " - " + coursNom + " (" + getStatutTexte() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Inscription that = (Inscription) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}