package com.gestion.cours.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe représentant une inscription d'un étudiant à un cours
 */
public class Inscription {
    public enum Statut {
        EN_ATTENTE, ACCEPTEE, REFUSEE, TERMINEE
    }

    private Integer id;
    private Integer etudiantId;
    private Integer coursId;
    private LocalDateTime dateInscription;
    private Statut statut;
    private BigDecimal progresPourcentage;
    private LocalDateTime derniereActivite;

    // Objets liés pour affichage
    private User etudiant;
    private Cours cours;

    // Constructeur par défaut
    public Inscription() {
        this.dateInscription = LocalDateTime.now();
        this.statut = Statut.EN_ATTENTE;
        this.progresPourcentage = BigDecimal.ZERO;
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

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public BigDecimal getProgresPourcentage() {
        return progresPourcentage;
    }

    public void setProgresPourcentage(BigDecimal progresPourcentage) {
        this.progresPourcentage = progresPourcentage;
    }

    public LocalDateTime getDerniereActivite() {
        return derniereActivite;
    }

    public void setDerniereActivite(LocalDateTime derniereActivite) {
        this.derniereActivite = derniereActivite;
    }

    public User getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(User etudiant) {
        this.etudiant = etudiant;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    // Méthodes utilitaires
    public String getDateInscriptionFormatee() {
        if (dateInscription == null) return "";
        return dateInscription.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getDerniereActiviteFormatee() {
        if (derniereActivite == null) return "Aucune activité";
        return derniereActivite.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getStatutLibelle() {
        switch (statut) {
            case EN_ATTENTE:
                return "En attente";
            case ACCEPTEE:
                return "Acceptée";
            case REFUSEE:
                return "Refusée";
            case TERMINEE:
                return "Terminée";
            default:
                return "Inconnu";
        }
    }

    public boolean isEnAttente() {
        return statut == Statut.EN_ATTENTE;
    }

    public boolean isAcceptee() {
        return statut == Statut.ACCEPTEE;
    }

    public boolean isRefusee() {
        return statut == Statut.REFUSEE;
    }

    public boolean isTerminee() {
        return statut == Statut.TERMINEE;
    }

    public boolean isActive() {
        return isAcceptee() && !isTerminee();
    }

    public String getProgresFormate() {
        if (progresPourcentage == null) {
            return "0%";
        }
        return String.format("%.1f%%", progresPourcentage);
    }

    public int getProgresEntier() {
        if (progresPourcentage == null) {
            return 0;
        }
        return progresPourcentage.intValue();
    }

    public boolean isProgresComplete() {
        return progresPourcentage != null && 
               progresPourcentage.compareTo(new BigDecimal("100")) >= 0;
    }

    public void ajouterProgres(BigDecimal progressionSupplementaire) {
        if (progressionSupplementaire == null || progressionSupplementaire.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        
        if (progresPourcentage == null) {
            progresPourcentage = BigDecimal.ZERO;
        }
        
        progresPourcentage = progresPourcentage.add(progressionSupplementaire);
        
        // S'assurer que le progrès ne dépasse pas 100%
        if (progresPourcentage.compareTo(new BigDecimal("100")) > 0) {
            progresPourcentage = new BigDecimal("100");
        }
        
        updateDerniereActivite();
        
        // Marquer comme terminé si 100% atteint
        if (isProgresComplete() && isAcceptee()) {
            statut = Statut.TERMINEE;
        }
    }

    public void updateDerniereActivite() {
        this.derniereActivite = LocalDateTime.now();
    }

    public boolean isValide() {
        return etudiantId != null && coursId != null;
    }

    @Override
    public String toString() {
        String etudiantNom = etudiant != null ? etudiant.getNomComplet() : "Étudiant #" + etudiantId;
        String coursNom = cours != null ? cours.getTitre() : "Cours #" + coursId;
        return etudiantNom + " -> " + coursNom + " (" + getStatutLibelle() + ")";
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