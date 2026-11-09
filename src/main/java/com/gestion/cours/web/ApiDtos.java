package com.gestion.cours.web;

import com.gestion.cours.model.Categorie;
import com.gestion.cours.model.Cours;
import com.gestion.cours.model.Etudiant;
import com.gestion.cours.model.Professeur;
import com.gestion.cours.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ApiDtos {
    private ApiDtos() {
    }

    public static class Credentials {
        public String email;
        public String password;
    }

    public static class Registration {
        public String nom;
        public String prenom;
        public String email;
        public String password;
        public String typeUtilisateur;
        public String telephone;
    }

    public static class CourseRequest {
        public String titre;
        public String presentation;
        public String description;
        public String motsCles;
        public String publicVise;
        public String prerequis;
        public String niveau;
        public Integer dureeHeures;
        public Integer capaciteMax = 30;
        public BigDecimal prix = BigDecimal.ZERO;
        public Integer professeurId;
        public Integer categorieId;
        public Boolean visible = Boolean.FALSE;
        public String imageCouverture;
        public LocalDate datePublication;
        public LocalDate dateDebut;
        public LocalDate dateFin;
        public LocalTime horaireDebut;
        public LocalTime horaireFin;
        public String joursSemaine;
        public String salle;
        public Boolean actif = Boolean.TRUE;

        public Cours toEntity() {
            Cours cours = new Cours();
            cours.setTitre(titre);
            cours.setPresentation(presentation == null ? description : presentation);
            cours.setDescription(description);
            cours.setMotsCles(motsCles);
            cours.setPublicVise(publicVise);
            cours.setPrerequis(prerequis);
            cours.setNiveau(niveau);
            cours.setDureeHeures(dureeHeures);
            cours.setCapaciteMax(capaciteMax);
            cours.setPrix(prix);
            cours.setProfesseurId(professeurId);
            cours.setCategorieId(categorieId);
            cours.setVisible(Boolean.TRUE.equals(visible));
            cours.setImageCouverture(imageCouverture);
            cours.setDatePublication(datePublication);
            cours.setDateDebut(dateDebut);
            cours.setDateFin(dateFin);
            cours.setHoraireDebut(horaireDebut);
            cours.setHoraireFin(horaireFin);
            cours.setJoursSemaine(joursSemaine);
            cours.setSalle(salle);
            cours.setActif(!Boolean.FALSE.equals(actif));
            return cours;
        }
    }

    public static class ProfessorRequest {
        public String nom;
        public String prenom;
        public String email;
        public String telephone;
        public String specialite;
        public LocalDate dateEmbauche;
        public BigDecimal salaire;

        public Professeur toEntity() {
            Professeur professeur = new Professeur(nom, prenom, email, specialite);
            professeur.setTelephone(telephone);
            professeur.setDateEmbauche(dateEmbauche);
            professeur.setSalaire(salaire);
            return professeur;
        }
    }

    public static class StudentRequest {
        public String nom;
        public String prenom;
        public String email;
        public String telephone;
        public LocalDate dateNaissance;
        public String adresse;
        public String niveauEtude;

        public Etudiant toEntity() {
            Etudiant etudiant = new Etudiant(nom, prenom, email);
            etudiant.setTelephone(telephone);
            etudiant.setDateNaissance(dateNaissance);
            etudiant.setAdresse(adresse);
            etudiant.setNiveauEtude(niveauEtude);
            return etudiant;
        }
    }

    public static Map<String, Object> user(User user) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", user.getId());
        result.put("nom", user.getNom());
        result.put("prenom", user.getPrenom());
        result.put("nomComplet", user.getNomComplet());
        result.put("email", user.getEmail());
        result.put("typeUtilisateur", user.getTypeUtilisateur().name());
        result.put("telephone", user.getTelephone());
        return result;
    }

    public static Map<String, Object> course(Cours cours) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", cours.getId());
        result.put("titre", cours.getTitre());
        result.put("presentation", cours.getPresentation());
        result.put("motsCles", cours.getMotsCles());
        result.put("publicVise", cours.getPublicVise());
        result.put("prerequis", cours.getPrerequis());
        result.put("niveau", cours.getNiveau() == null ? null : cours.getNiveau().name());
        result.put("dureeHeures", cours.getDureeHeures());
        result.put("capaciteMax", cours.getCapaciteMax());
        result.put("prix", cours.getPrix());
        result.put("professeurId", cours.getProfesseurId());
        result.put("professeur", cours.getProfesseur() == null ? null : cours.getProfesseur().getNomComplet());
        result.put("categorieId", cours.getCategorieId());
        result.put("categorie", cours.getCategorie() == null ? null : cours.getCategorie().getNom());
        result.put("visible", cours.isVisible());
        result.put("imageCouverture", cours.getImageCouverture());
        result.put("datePublication", cours.getDatePublication());
        result.put("dateDebut", cours.getDateDebut());
        result.put("dateFin", cours.getDateFin());
        result.put("horaireDebut", cours.getHoraireDebut());
        result.put("horaireFin", cours.getHoraireFin());
        result.put("joursSemaine", cours.getJoursSemaine());
        result.put("salle", cours.getSalle());
        result.put("actif", cours.isActif());
        result.put("statut", cours.getStatutCours());
        return result;
    }

    public static Map<String, Object> professor(Professeur professeur) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", professeur.getId());
        result.put("nom", professeur.getNom());
        result.put("prenom", professeur.getPrenom());
        result.put("nomComplet", professeur.getNomComplet());
        result.put("email", professeur.getEmail());
        result.put("telephone", professeur.getTelephone());
        result.put("specialite", professeur.getSpecialite());
        result.put("dateEmbauche", professeur.getDateEmbauche());
        result.put("salaire", professeur.getSalaire());
        result.put("actif", professeur.isActif());
        return result;
    }

    public static Map<String, Object> student(Etudiant etudiant) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", etudiant.getId());
        result.put("nom", etudiant.getNom());
        result.put("prenom", etudiant.getPrenom());
        result.put("nomComplet", etudiant.getNomComplet());
        result.put("email", etudiant.getEmail());
        result.put("telephone", etudiant.getTelephone());
        result.put("dateNaissance", etudiant.getDateNaissance());
        result.put("adresse", etudiant.getAdresse());
        result.put("niveauEtude", etudiant.getNiveauEtude());
        result.put("actif", etudiant.isActif());
        return result;
    }

    public static Map<String, Object> category(Categorie categorie) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", categorie.getId());
        result.put("nom", categorie.getNom());
        result.put("description", categorie.getDescription());
        return result;
    }
}
