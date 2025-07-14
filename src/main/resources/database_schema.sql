-- Base de données pour la gestion de cours d'apprentissage
CREATE DATABASE IF NOT EXISTS gestion_cours_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestion_cours_db;

-- Table des professeurs
CREATE TABLE IF NOT EXISTS professeurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    specialite VARCHAR(100),
    date_embauche DATE,
    salaire DECIMAL(10,2),
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table des étudiants
CREATE TABLE IF NOT EXISTS etudiants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    date_naissance DATE,
    adresse TEXT,
    niveau_etude VARCHAR(50),
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table des catégories de cours
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des cours
CREATE TABLE IF NOT EXISTS cours (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    duree_heures INT NOT NULL,
    prix DECIMAL(10,2) NOT NULL,
    niveau VARCHAR(50) NOT NULL,
    capacite_max INT DEFAULT 30,
    professeur_id INT,
    categorie_id INT,
    date_debut DATE,
    date_fin DATE,
    horaire_debut TIME,
    horaire_fin TIME,
    jours_semaine VARCHAR(50), -- Ex: "Lundi,Mercredi,Vendredi"
    salle VARCHAR(50),
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (professeur_id) REFERENCES professeurs(id) ON DELETE SET NULL,
    FOREIGN KEY (categorie_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- Table des inscriptions
CREATE TABLE IF NOT EXISTS inscriptions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    etudiant_id INT NOT NULL,
    cours_id INT NOT NULL,
    date_inscription DATE NOT NULL,
    statut ENUM('EN_ATTENTE', 'CONFIRMEE', 'ANNULEE', 'TERMINEE') DEFAULT 'EN_ATTENTE',
    note_finale DECIMAL(4,2),
    commentaires TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (etudiant_id) REFERENCES etudiants(id) ON DELETE CASCADE,
    FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE,
    UNIQUE KEY unique_inscription (etudiant_id, cours_id)
);

-- Table des présences
CREATE TABLE IF NOT EXISTS presences (
    id INT AUTO_INCREMENT PRIMARY KEY,
    inscription_id INT NOT NULL,
    date_cours DATE NOT NULL,
    present BOOLEAN DEFAULT FALSE,
    retard BOOLEAN DEFAULT FALSE,
    justifie BOOLEAN DEFAULT FALSE,
    commentaire TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (inscription_id) REFERENCES inscriptions(id) ON DELETE CASCADE,
    UNIQUE KEY unique_presence (inscription_id, date_cours)
);

-- Table des évaluations
CREATE TABLE IF NOT EXISTS evaluations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cours_id INT NOT NULL,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    date_evaluation DATE NOT NULL,
    note_max DECIMAL(4,2) DEFAULT 20.00,
    coefficient DECIMAL(3,2) DEFAULT 1.00,
    type_evaluation ENUM('CONTROLE', 'EXAMEN', 'PROJET', 'ORAL') NOT NULL,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE
);

-- Table des notes
CREATE TABLE IF NOT EXISTS notes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    inscription_id INT NOT NULL,
    evaluation_id INT NOT NULL,
    note DECIMAL(4,2),
    commentaire TEXT,
    date_saisie TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (inscription_id) REFERENCES inscriptions(id) ON DELETE CASCADE,
    FOREIGN KEY (evaluation_id) REFERENCES evaluations(id) ON DELETE CASCADE,
    UNIQUE KEY unique_note (inscription_id, evaluation_id)
);

-- Insertion de données de test
INSERT INTO categories (nom, description) VALUES
('Informatique', 'Cours liés à l''informatique et au développement'),
('Mathématiques', 'Cours de mathématiques générales et appliquées'),
('Langues', 'Cours de langues étrangères'),
('Sciences', 'Cours de sciences physiques et naturelles'),
('Arts', 'Cours d''arts plastiques et créatifs');

INSERT INTO professeurs (nom, prenom, email, telephone, specialite, date_embauche, salaire) VALUES
('Dupont', 'Jean', 'jean.dupont@email.com', '0123456789', 'Java/Spring', '2020-01-15', 45000.00),
('Martin', 'Marie', 'marie.martin@email.com', '0123456790', 'Base de données', '2019-03-10', 42000.00),
('Bernard', 'Pierre', 'pierre.bernard@email.com', '0123456791', 'Mathématiques', '2021-09-01', 40000.00),
('Durand', 'Sophie', 'sophie.durand@email.com', '0123456792', 'Anglais', '2020-06-15', 38000.00);

INSERT INTO etudiants (nom, prenom, email, telephone, date_naissance, adresse, niveau_etude) VALUES
('Leroy', 'Thomas', 'thomas.leroy@email.com', '0612345678', '1995-05-15', '123 Rue de la Paix, Paris', 'Bac+2'),
('Moreau', 'Emma', 'emma.moreau@email.com', '0612345679', '1997-08-22', '456 Avenue des Champs, Lyon', 'Bac+3'),
('Petit', 'Lucas', 'lucas.petit@email.com', '0612345680', '1996-12-03', '789 Boulevard Saint-Michel, Marseille', 'Bac+1'),
('Roux', 'Léa', 'lea.roux@email.com', '0612345681', '1998-02-18', '321 Rue Victor Hugo, Toulouse', 'Bac+2');

INSERT INTO cours (titre, description, duree_heures, prix, niveau, capacite_max, professeur_id, categorie_id, date_debut, date_fin, horaire_debut, horaire_fin, jours_semaine, salle) VALUES
('Programmation Java Avancée', 'Approfondissement des concepts Java avec Spring Framework', 40, 800.00, 'Intermédiaire', 25, 1, 1, '2024-02-01', '2024-04-30', '09:00:00', '12:00:00', 'Lundi,Mercredi', 'Salle A101'),
('Base de Données MySQL', 'Conception et administration de bases de données MySQL', 30, 600.00, 'Débutant', 20, 2, 1, '2024-02-15', '2024-04-15', '14:00:00', '17:00:00', 'Mardi,Jeudi', 'Salle B202'),
('Algèbre Linéaire', 'Cours de mathématiques : algèbre linéaire et applications', 35, 500.00, 'Intermédiaire', 30, 3, 2, '2024-03-01', '2024-05-31', '10:00:00', '12:00:00', 'Lundi,Mercredi,Vendredi', 'Salle C301'),
('Anglais Professionnel', 'Anglais appliqué au monde professionnel', 25, 450.00, 'Débutant', 15, 4, 3, '2024-01-15', '2024-06-15', '18:00:00', '20:00:00', 'Mardi,Jeudi', 'Salle D102');

-- Index pour optimiser les performances
CREATE INDEX idx_cours_professeur ON cours(professeur_id);
CREATE INDEX idx_cours_categorie ON cours(categorie_id);
CREATE INDEX idx_inscriptions_etudiant ON inscriptions(etudiant_id);
CREATE INDEX idx_inscriptions_cours ON inscriptions(cours_id);
CREATE INDEX idx_presences_inscription ON presences(inscription_id);
CREATE INDEX idx_notes_inscription ON notes(inscription_id);
CREATE INDEX idx_evaluations_cours ON evaluations(cours_id);