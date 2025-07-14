-- Base de données pour la plateforme d'apprentissage en ligne - Gestion de Cours 2025
CREATE DATABASE IF NOT EXISTS gestion_cours_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestion_cours_db;

-- Table des utilisateurs (professeurs et étudiants)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL, -- Hashé avec BCrypt
    type_utilisateur ENUM('PROFESSEUR', 'ETUDIANT') NOT NULL,
    telephone VARCHAR(20),
    date_naissance DATE,
    adresse TEXT,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    derniere_connexion TIMESTAMP NULL
);

-- Table des catégories de cours
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icone VARCHAR(50), -- Nom de l'icône pour l'affichage
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des cours (adaptée pour plateforme d'apprentissage en ligne)
CREATE TABLE IF NOT EXISTS cours (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    presentation TEXT NOT NULL, -- Description détaillée du cours
    mots_cles TEXT, -- Mots clés séparés par des virgules
    public_vise TEXT, -- Description du public ciblé
    prerequis TEXT, -- Prérequis pour suivre le cours
    niveau ENUM('DEBUTANT', 'INTERMEDIAIRE', 'AVANCE') NOT NULL,
    duree_heures INT,
    prix DECIMAL(10,2) DEFAULT 0.00,
    professeur_id INT NOT NULL,
    categorie_id INT,
    visible BOOLEAN DEFAULT FALSE, -- Visibilité progressive du cours
    image_couverture VARCHAR(255), -- Chemin vers l'image de couverture
    date_publication DATE,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (professeur_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (categorie_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- Table des chapitres/sections de cours
CREATE TABLE IF NOT EXISTS chapitres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cours_id INT NOT NULL,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    ordre_affichage INT NOT NULL,
    visible BOOLEAN DEFAULT FALSE, -- Visibilité progressive
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE
);

-- Table des ressources (fichiers PDF, documents)
CREATE TABLE IF NOT EXISTS ressources (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cours_id INT,
    chapitre_id INT,
    nom_fichier VARCHAR(255) NOT NULL,
    nom_original VARCHAR(255) NOT NULL,
    chemin_fichier VARCHAR(500) NOT NULL,
    type_fichier VARCHAR(10) DEFAULT 'PDF',
    taille_fichier BIGINT, -- Taille en octets
    description TEXT,
    visible BOOLEAN DEFAULT FALSE, -- Visibilité progressive
    ordre_affichage INT DEFAULT 1,
    date_upload TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE,
    FOREIGN KEY (chapitre_id) REFERENCES chapitres(id) ON DELETE CASCADE
);

-- Table des inscriptions aux cours
CREATE TABLE IF NOT EXISTS inscriptions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    etudiant_id INT NOT NULL,
    cours_id INT NOT NULL,
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('EN_ATTENTE', 'ACCEPTEE', 'REFUSEE', 'TERMINEE') DEFAULT 'EN_ATTENTE',
    progres_pourcentage DECIMAL(5,2) DEFAULT 0.00,
    derniere_activite TIMESTAMP NULL,
    FOREIGN KEY (etudiant_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE,
    UNIQUE KEY unique_inscription (etudiant_id, cours_id)
);

-- Table des messages/communications
CREATE TABLE IF NOT EXISTS messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    expediteur_id INT NOT NULL,
    destinataire_id INT,
    cours_id INT,
    sujet VARCHAR(255) NOT NULL,
    contenu TEXT NOT NULL,
    type_message ENUM('QUESTION', 'ANNONCE', 'REPONSE', 'PRIVE') NOT NULL,
    lu BOOLEAN DEFAULT FALSE,
    date_envoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (expediteur_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (destinataire_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE
);

-- Table des annonces
CREATE TABLE IF NOT EXISTS annonces (
    id INT AUTO_INCREMENT PRIMARY KEY,
    professeur_id INT NOT NULL,
    cours_id INT NOT NULL,
    titre VARCHAR(255) NOT NULL,
    contenu TEXT NOT NULL,
    importante BOOLEAN DEFAULT FALSE,
    date_publication TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_expiration DATE NULL,
    FOREIGN KEY (professeur_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE
);

-- Table de suivi de lecture des ressources
CREATE TABLE IF NOT EXISTS lectures_ressources (
    id INT AUTO_INCREMENT PRIMARY KEY,
    etudiant_id INT NOT NULL,
    ressource_id INT NOT NULL,
    date_lecture TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    temps_lecture_minutes INT DEFAULT 0,
    FOREIGN KEY (etudiant_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (ressource_id) REFERENCES ressources(id) ON DELETE CASCADE,
    UNIQUE KEY unique_lecture (etudiant_id, ressource_id)
);

-- Table des sessions utilisateur
CREATE TABLE IF NOT EXISTS sessions (
    id VARCHAR(255) PRIMARY KEY,
    user_id INT NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_expiration TIMESTAMP NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    actif BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index pour optimiser les performances
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_type ON users(type_utilisateur);
CREATE INDEX idx_cours_professeur ON cours(professeur_id);
CREATE INDEX idx_cours_categorie ON cours(categorie_id);
CREATE INDEX idx_cours_visible ON cours(visible);
CREATE INDEX idx_chapitres_cours ON chapitres(cours_id);
CREATE INDEX idx_ressources_cours ON ressources(cours_id);
CREATE INDEX idx_ressources_chapitre ON ressources(chapitre_id);
CREATE INDEX idx_inscriptions_etudiant ON inscriptions(etudiant_id);
CREATE INDEX idx_inscriptions_cours ON inscriptions(cours_id);
CREATE INDEX idx_messages_expediteur ON messages(expediteur_id);
CREATE INDEX idx_messages_destinataire ON messages(destinataire_id);
CREATE INDEX idx_messages_cours ON messages(cours_id);
CREATE INDEX idx_annonces_cours ON annonces(cours_id);

-- Insertion de données de test
INSERT INTO categories (nom, description, icone) VALUES
('Informatique', 'Cours de programmation et développement', 'computer'),
('Mathématiques', 'Cours de mathématiques et statistiques', 'calculator'),
('Langues', 'Apprentissage des langues étrangères', 'language'),
('Sciences', 'Sciences physiques et naturelles', 'science'),
('Arts & Design', 'Créativité et design graphique', 'palette'),
('Business', 'Gestion et entrepreneuriat', 'briefcase');

-- Insertion d'utilisateurs de test (mot de passe: "password123" hashé)
INSERT INTO users (nom, prenom, email, mot_de_passe, type_utilisateur, telephone) VALUES
-- Professeurs
('Dupont', 'Jean', 'jean.dupont@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Uo0hVvPnj2QyZKTWnQXt9W1CJYEjW6', 'PROFESSEUR', '0123456789'),
('Martin', 'Marie', 'marie.martin@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Uo0hVvPnj2QyZKTWnQXt9W1CJYEjW6', 'PROFESSEUR', '0123456790'),
('Bernard', 'Pierre', 'pierre.bernard@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Uo0hVvPnj2QyZKTWnQXt9W1CJYEjW6', 'PROFESSEUR', '0123456791'),
('Durand', 'Sophie', 'sophie.durand@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Uo0hVvPnj2QyZKTWnQXt9W1CJYEjW6', 'PROFESSEUR', '0123456792'),
-- Étudiants
('Leroy', 'Thomas', 'thomas.leroy@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Uo0hVvPnj2QyZKTWnQXt9W1CJYEjW6', 'ETUDIANT', '0612345678'),
('Moreau', 'Emma', 'emma.moreau@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Uo0hVvPnj2QyZKTWnQXt9W1CJYEjW6', 'ETUDIANT', '0612345679'),
('Petit', 'Lucas', 'lucas.petit@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Uo0hVvPnj2QyZKTWnQXt9W1CJYEjW6', 'ETUDIANT', '0612345680'),
('Roux', 'Léa', 'lea.roux@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Uo0hVvPnj2QyZKTWnQXt9W1CJYEjW6', 'ETUDIANT', '0612345681');

-- Insertion de cours de test
INSERT INTO cours (titre, presentation, mots_cles, public_vise, prerequis, niveau, duree_heures, prix, professeur_id, categorie_id, visible, date_publication) VALUES
('Java Spring Boot - Développement Web Moderne', 
 'Apprenez à créer des applications web robustes avec Spring Boot. Ce cours couvre les fondamentaux du framework, la création d''APIs REST, la sécurité, et l''intégration avec des bases de données.',
 'Java, Spring Boot, REST API, JPA, Hibernate, Maven',
 'Développeurs ayant une base en Java et souhaitant apprendre le développement web moderne',
 'Connaissance de base en Java (syntaxe, POO), notions de HTML/CSS',
 'INTERMEDIAIRE', 40, 299.99, 1, 1, TRUE, '2024-01-15'),

('Python pour la Data Science', 
 'Découvrez l''écosystème Python pour l''analyse de données : pandas, numpy, matplotlib, scikit-learn. Projets pratiques inclus.',
 'Python, Data Science, Pandas, NumPy, Matplotlib, Machine Learning',
 'Analystes, étudiants en informatique, professionnels souhaitant se reconvertir',
 'Bases de programmation recommandées, mathématiques niveau lycée',
 'DEBUTANT', 35, 249.99, 2, 1, TRUE, '2024-01-20'),

('Design UX/UI avec Figma', 
 'Maîtrisez les principes du design UX/UI et apprenez à créer des interfaces modernes avec Figma. De la recherche utilisateur au prototype interactif.',
 'UX Design, UI Design, Figma, Prototypage, User Research',
 'Designers débutants, développeurs, chefs de produit',
 'Aucun prérequis technique, créativité et intérêt pour le design',
 'DEBUTANT', 25, 199.99, 3, 5, FALSE, '2024-02-01'),

('Anglais Professionnel B2', 
 'Perfectionnez votre anglais professionnel : présentations, emails, négociations, réunions. Préparation certification TOEIC.',
 'Anglais, Business English, TOEIC, Communication professionnelle',
 'Professionnels, étudiants, personnes visant une certification',
 'Niveau B1 minimum en anglais',
 'INTERMEDIAIRE', 30, 179.99, 4, 3, TRUE, '2024-01-25');

-- Insertion de chapitres pour le cours Java
INSERT INTO chapitres (cours_id, titre, description, ordre_affichage, visible) VALUES
(1, 'Introduction à Spring Boot', 'Découverte du framework et premier projet', 1, TRUE),
(1, 'Configuration et Structure', 'Organisation du projet et fichiers de configuration', 2, TRUE),
(1, 'Créer des APIs REST', 'Développement d''endpoints REST avec Spring MVC', 3, FALSE),
(1, 'Gestion des données avec JPA', 'Intégration base de données et ORM', 4, FALSE),
(1, 'Sécurité et authentification', 'Spring Security et JWT', 5, FALSE);

-- Insertion de ressources pour les chapitres
INSERT INTO ressources (cours_id, chapitre_id, nom_fichier, nom_original, chemin_fichier, description, visible, ordre_affichage) VALUES
(1, 1, 'intro-spring-boot.pdf', 'Introduction à Spring Boot.pdf', '/resources/cours1/intro-spring-boot.pdf', 'Guide d''introduction complet à Spring Boot', TRUE, 1),
(1, 1, 'setup-environment.pdf', 'Configuration de l''environnement.pdf', '/resources/cours1/setup-environment.pdf', 'Instructions d''installation et configuration', TRUE, 2),
(1, 2, 'project-structure.pdf', 'Structure de projet Spring Boot.pdf', '/resources/cours1/project-structure.pdf', 'Bonnes pratiques d''organisation', TRUE, 1),
(1, 3, 'rest-apis-guide.pdf', 'Guide des APIs REST.pdf', '/resources/cours1/rest-apis-guide.pdf', 'Création d''APIs REST avec Spring', FALSE, 1);

-- Insertion d'inscriptions de test
INSERT INTO inscriptions (etudiant_id, cours_id, statut, progres_pourcentage) VALUES
(5, 1, 'ACCEPTEE', 25.0), -- Thomas inscrit au cours Java
(6, 1, 'ACCEPTEE', 15.0), -- Emma inscrite au cours Java
(7, 2, 'ACCEPTEE', 40.0), -- Lucas inscrit au cours Python
(8, 4, 'ACCEPTEE', 60.0); -- Léa inscrite au cours Anglais

-- Insertion d'annonces de test
INSERT INTO annonces (professeur_id, cours_id, titre, contenu, importante, date_expiration) VALUES
(1, 1, 'Bienvenue dans le cours Java Spring Boot !', 
 'Bienvenue à tous ! Ce cours vous permettra de maîtriser Spring Boot. N''hésitez pas à poser vos questions dans la section messages.',
 TRUE, '2024-12-31'),
(2, 2, 'Nouveau chapitre disponible', 
 'Le chapitre sur Pandas et l''analyse de données est maintenant accessible. Bon apprentissage !',
 FALSE, '2024-06-30');

-- Insertion de messages de test
INSERT INTO messages (expediteur_id, destinataire_id, cours_id, sujet, contenu, type_message) VALUES
(5, 1, 1, 'Question sur les annotations Spring', 
 'Bonjour M. Dupont, j''ai une question concernant l''utilisation des annotations @Service et @Component. Quelle est la différence ?',
 'QUESTION'),
(1, 5, 1, 'Re: Question sur les annotations Spring',
 'Bonjour Thomas, excellente question ! @Service est une spécialisation de @Component pour la couche métier...',
 'REPONSE');