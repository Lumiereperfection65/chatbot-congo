package com.gestion.cours.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe représentant un message/communication entre utilisateurs
 */
public class Message {
    public enum TypeMessage {
        QUESTION, ANNONCE, REPONSE, PRIVE
    }

    private Integer id;
    private Integer expediteurId;
    private Integer destinataireId; // Peut être null pour les annonces publiques
    private Integer coursId;
    private String sujet;
    private String contenu;
    private TypeMessage typeMessage;
    private boolean lu;
    private LocalDateTime dateEnvoi;

    // Objets liés pour affichage
    private User expediteur;
    private User destinataire;
    private Cours cours;

    // Constructeur par défaut
    public Message() {
        this.lu = false;
        this.dateEnvoi = LocalDateTime.now();
    }

    // Constructeur avec paramètres essentiels
    public Message(Integer expediteurId, String sujet, String contenu, TypeMessage typeMessage) {
        this();
        this.expediteurId = expediteurId;
        this.sujet = sujet;
        this.contenu = contenu;
        this.typeMessage = typeMessage;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getExpediteurId() {
        return expediteurId;
    }

    public void setExpediteurId(Integer expediteurId) {
        this.expediteurId = expediteurId;
    }

    public Integer getDestinataireId() {
        return destinataireId;
    }

    public void setDestinataireId(Integer destinataireId) {
        this.destinataireId = destinataireId;
    }

    public Integer getCoursId() {
        return coursId;
    }

    public void setCoursId(Integer coursId) {
        this.coursId = coursId;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public TypeMessage getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(TypeMessage typeMessage) {
        this.typeMessage = typeMessage;
    }

    public boolean isLu() {
        return lu;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public User getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(User expediteur) {
        this.expediteur = expediteur;
    }

    public User getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(User destinataire) {
        this.destinataire = destinataire;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    // Méthodes utilitaires
    public String getDateEnvoiFormatee() {
        if (dateEnvoi == null) return "";
        return dateEnvoi.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getTypeMessageLibelle() {
        switch (typeMessage) {
            case QUESTION:
                return "Question";
            case REPONSE:
                return "Réponse";
            case ANNONCE:
                return "Annonce";
            case PRIVE:
                return "Message privé";
            default:
                return "Message";
        }
    }

    public boolean isAnnonce() {
        return typeMessage == TypeMessage.ANNONCE;
    }

    public boolean isQuestion() {
        return typeMessage == TypeMessage.QUESTION;
    }

    public boolean isReponse() {
        return typeMessage == TypeMessage.REPONSE;
    }

    public boolean isPrive() {
        return typeMessage == TypeMessage.PRIVE;
    }

    public boolean isMessagePublic() {
        return destinataireId == null || isAnnonce();
    }

    public String getContenuApercu(int longueurMax) {
        if (contenu == null || contenu.trim().isEmpty()) {
            return "";
        }
        
        String contenuNettoye = contenu.trim().replaceAll("\\s+", " ");
        if (contenuNettoye.length() <= longueurMax) {
            return contenuNettoye;
        }
        
        return contenuNettoye.substring(0, longueurMax - 3) + "...";
    }

    public boolean isValide() {
        return expediteurId != null &&
               sujet != null && !sujet.trim().isEmpty() &&
               contenu != null && !contenu.trim().isEmpty() &&
               typeMessage != null;
    }

    @Override
    public String toString() {
        return sujet + " (" + getTypeMessageLibelle() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Message that = (Message) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}