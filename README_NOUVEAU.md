# Gestion de Cours 2025 - Plateforme d'Apprentissage en Ligne

## 📚 Description

**Gestion de Cours 2025** est une plateforme d'apprentissage en ligne complète développée en Java avec Swing et MySQL. Elle permet aux professeurs de créer et gérer des cours, et aux étudiants de s'inscrire et d'accéder à ces cours avec un système d'authentification sécurisé.

### 🎯 Fonctionnalités Principales

#### 🔐 **Inscription et Authentification**
- Inscription sécurisée pour professeurs et étudiants
- Authentification par email et mot de passe hashé (BCrypt)
- Gestion des sessions utilisateur
- Validation des données d'entrée

#### 👨‍🏫 **Gestion des Cours par les Professeurs**
- Création de cours avec informations détaillées :
  - Titre et présentation complète
  - Mots-clés pour la recherche
  - Public visé et prérequis
  - Niveau (Débutant, Intermédiaire, Avancé)
  - Prix et durée
- Organisation en chapitres/sections
- Ajout de ressources PDF
- **Visibilité progressive** : contrôle de l'accès aux contenus
- Modification et suppression des cours
- Statistiques d'inscription

#### 👨‍🎓 **Fonctionnalités pour les Étudiants**
- Inscription aux cours disponibles
- Accès aux ressources des cours inscrits
- **Recherche avancée** par critères :
  - Mots-clés
  - Niveau
  - Catégorie
  - Cours gratuits
- Consultation et téléchargement des fichiers PDF
- Suivi du progrès d'apprentissage
- Communication avec les professeurs

#### 💬 **Communication et Annonces**
- Système de messages entre utilisateurs
- Questions des étudiants aux professeurs
- Annonces des professeurs aux étudiants inscrits
- Messages privés et publics

## 🛠️ Technologies Utilisées

- **Java 11+** - Langage principal
- **Swing** - Interface graphique native
- **MySQL 8.0** - Base de données
- **Maven** - Gestion des dépendances
- **BCrypt** - Hachage sécurisé des mots de passe
- **FlatLaf** - Look and Feel moderne
- **SLF4J + Logback** - Logging

## 📋 Prérequis

- **Java JDK 11** ou supérieur
- **MySQL Server 8.0** ou supérieur
- **Maven 3.6** ou supérieur
- **NetBeans IDE** (recommandé) ou tout autre IDE Java

## 🚀 Installation

### 1. Cloner le Projet
```bash
git clone <url-du-repo>
cd gestion-cours-2025
```

### 2. Configuration de la Base de Données

#### Démarrer MySQL
```bash
# Sur Linux/macOS
sudo systemctl start mysql
# ou
brew services start mysql

# Sur Windows
net start mysql
```

#### Créer la Base de Données
```sql
-- Se connecter à MySQL
mysql -u root -p

-- Exécuter le script de création
source src/main/resources/database_schema.sql;
```

#### Configurer la Connexion
Modifier le fichier `src/main/resources/database.properties` :
```properties
db.url=jdbc:mysql://localhost:3306/gestion_cours_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=root
db.password=votre_mot_de_passe_mysql
db.driver=com.mysql.cj.jdbc.Driver
```

### 3. Compilation et Exécution

#### Avec Maven
```bash
# Compiler le projet
mvn clean compile

# Lancer l'application
mvn exec:java -Dexec.mainClass="com.gestion.cours.Main"

# Ou créer un JAR exécutable
mvn clean package
java -jar target/gestion-cours-app-1.0.0-jar-with-dependencies.jar
```

#### Avec NetBeans
1. Ouvrir le projet dans NetBeans
2. Clic droit sur le projet → "Clean and Build"
3. Clic droit sur le projet → "Run"

## 👤 Utilisation

### Première Connexion

L'application inclut des utilisateurs de test avec le mot de passe `password123` :

#### Professeurs
- Email: `jean.dupont@email.com` - Spécialiste Java/Spring
- Email: `marie.martin@email.com` - Spécialiste Base de données
- Email: `pierre.bernard@email.com` - Spécialiste Mathématiques
- Email: `sophie.durand@email.com` - Spécialiste Anglais

#### Étudiants
- Email: `thomas.leroy@email.com`
- Email: `emma.moreau@email.com`
- Email: `lucas.petit@email.com`
- Email: `lea.roux@email.com`

### Interface de Connexion

1. **Connexion** : Entrez votre email et mot de passe
2. **Inscription** : Créez un nouveau compte (Professeur ou Étudiant)

### Fonctionnalités par Type d'Utilisateur

#### Pour les Professeurs
1. **Créer un cours** - Onglet "Gestion des Cours"
2. **Ajouter des chapitres** et des ressources PDF
3. **Gérer la visibilité** progressive du contenu
4. **Envoyer des annonces** aux étudiants inscrits
5. **Répondre aux questions** des étudiants

#### Pour les Étudiants
1. **Rechercher des cours** - Utilisez les filtres de recherche
2. **S'inscrire à un cours** - Cliquez sur "S'inscrire"
3. **Accéder aux ressources** - Téléchargez les PDF disponibles
4. **Poser des questions** aux professeurs
5. **Suivre votre progrès** d'apprentissage

## 📁 Structure du Projet

```
src/
├── main/
│   ├── java/com/gestion/cours/
│   │   ├── dao/           # Accès aux données
│   │   ├── model/         # Modèles de données
│   │   ├── ui/            # Interfaces utilisateur
│   │   ├── util/          # Utilitaires et services
│   │   └── Main.java      # Point d'entrée
│   └── resources/
│       ├── database.properties      # Configuration DB
│       └── database_schema.sql     # Schéma de la DB
└── pom.xml               # Configuration Maven
```

## 🔧 Configuration Avancée

### Personnalisation de la Base de Données
Modifiez `database.properties` pour utiliser un serveur distant :
```properties
db.url=jdbc:mysql://votre-serveur:3306/gestion_cours_db
db.username=votre_utilisateur
db.password=votre_mot_de_passe
```

### Logs
Les logs sont configurés dans `src/main/resources/logback.xml` (optionnel).

## 🐛 Dépannage

### Erreurs Courantes

#### "Connexion à la base de données impossible"
- Vérifiez que MySQL est démarré
- Vérifiez les paramètres dans `database.properties`
- Assurez-vous que la base de données `gestion_cours_db` existe

#### "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
- Exécutez `mvn clean install` pour télécharger les dépendances

#### "Access denied for user"
- Vérifiez le nom d'utilisateur et mot de passe MySQL
- Assurez-vous que l'utilisateur a les permissions sur la base

### Réinitialiser la Base de Données
```sql
DROP DATABASE IF EXISTS gestion_cours_db;
source src/main/resources/database_schema.sql;
```

## 📊 Base de Données

### Tables Principales
- `users` - Utilisateurs (professeurs et étudiants)
- `cours` - Cours avec détails complets
- `chapitres` - Organisation en sections
- `ressources` - Fichiers PDF et documents
- `inscriptions` - Inscriptions avec suivi du progrès
- `messages` - Communication entre utilisateurs
- `annonces` - Annonces des professeurs

## 🔐 Sécurité

- Mots de passe hashés avec BCrypt
- Validation des entrées utilisateur
- Protection contre l'injection SQL avec PreparedStatements
- Sessions utilisateur sécurisées
- Validation des emails et des données

## 🎨 Interface Utilisateur

- Interface moderne avec FlatLaf Look and Feel
- Design responsive et intuitif
- Navigation par onglets
- Messages d'erreur et de succès clairs
- Formulaires de validation en temps réel

## 📝 Données d'Exemple

L'application inclut des données de test :
- 6 catégories de cours
- 4 cours d'exemple avec chapitres et ressources
- 8 utilisateurs (4 professeurs, 4 étudiants)
- Inscriptions et messages d'exemple

## 🤝 Contribution

Pour contribuer au projet :
1. Forkez le repository
2. Créez une branche pour votre fonctionnalité
3. Committez vos changements
4. Soumettez une Pull Request

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 📞 Support

Pour toute question ou problème :
- Consultez la documentation
- Vérifiez les issues existantes
- Créez une nouvelle issue si nécessaire

---

**Gestion de Cours 2025** - Votre plateforme d'apprentissage moderne et sécurisée ! 🎓