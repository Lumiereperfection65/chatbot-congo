# Gestion de Cours d'Apprentissage

Application complète de gestion de cours développée en Java avec interface graphique Swing et base de données MySQL.

## 🎯 Fonctionnalités

### ✅ Implémentées
- **Dashboard** avec statistiques en temps réel
- **Gestion des Cours** : création, modification, suppression avec détails complets
- **Gestion des Étudiants** : profils complets avec informations personnelles
- **Gestion des Professeurs** : données professionnelles et spécialités
- **Interface moderne** avec look-and-feel FlatLaf
- **Base de données MySQL** avec structure complète

### 🚧 En développement
- **Gestion des Inscriptions** : inscription des étudiants aux cours
- **Système de Notes** : évaluations et bulletins
- **Rapports et statistiques** avancés
- **Gestion des présences**

## 🛠️ Technologies utilisées

- **Java 11+** - Langage principal
- **Swing + FlatLaf** - Interface graphique moderne
- **MySQL 8.0+** - Base de données
- **Maven** - Gestion des dépendances
- **SLF4J + Logback** - Logging

## 📋 Prérequis

1. **Java 11 ou supérieur**
2. **MySQL 8.0 ou supérieur**
3. **Maven 3.6+**

## 🚀 Installation et Configuration

### 1. Cloner le projet
```bash
git clone <url-du-repo>
cd gestion-cours-app
```

### 2. Configurer MySQL

#### Démarrer MySQL
```bash
# Linux/macOS
sudo systemctl start mysql
# ou
sudo service mysql start

# Windows
net start mysql
```

#### Créer la base de données
```sql
mysql -u root -p
```

Puis exécuter le script SQL fourni :
```sql
source src/main/resources/database_schema.sql
```

### 3. Configurer la connexion

Modifier le fichier `src/main/resources/database.properties` :
```properties
db.url=jdbc:mysql://localhost:3306/gestion_cours_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=votre_utilisateur_mysql
db.password=votre_mot_de_passe_mysql
db.driver=com.mysql.cj.jdbc.Driver
```

### 4. Compiler et lancer

```bash
# Compiler le projet
mvn clean compile

# Lancer l'application
mvn exec:java -Dexec.mainClass="com.gestion.cours.Main"

# Ou créer un JAR exécutable
mvn clean package
java -jar target/gestion-cours-app-1.0.0-jar-with-dependencies.jar
```

## 📊 Structure de la base de données

### Tables principales :
- **professeurs** - Informations des enseignants
- **etudiants** - Profils des étudiants
- **categories** - Catégories de cours
- **cours** - Détails des formations
- **inscriptions** - Liens étudiants/cours
- **presences** - Suivi de présence
- **evaluations** - Examens et contrôles
- **notes** - Résultats des évaluations

### Données de test incluses :
- 4 professeurs exemple
- 4 étudiants exemple
- 5 catégories de cours
- 4 cours configurés

## 🎮 Utilisation

### Démarrage
1. Lancer l'application
2. Vérifier la connexion DB (barre de statut)
3. Naviguer entre les onglets

### Gestion des Cours
- **Nouveau** : Créer un cours avec tous les détails
- **Modifier** : Sélectionner un cours dans la liste
- **Supprimer** : Désactivation (soft delete)
- **Champs obligatoires** : Titre, durée, prix, niveau

### Gestion des Étudiants/Professeurs
- Interface similaire avec formulaires adaptés
- Validation des données
- Recherche et tri automatique

## 🔧 Configuration avancée

### Personnaliser les niveaux d'étude
Modifier dans `EtudiantPanel.java` :
```java
niveauCombo = new JComboBox<>(new String[]{"Bac", "Bac+1", "Bac+2", "Bac+3", "Bac+4", "Bac+5", "Autre"});
```

### Personnaliser les niveaux de cours
Modifier dans `CoursPanel.java` :
```java
niveauCombo = new JComboBox<>(new String[]{"Débutant", "Intermédiaire", "Avancé"});
```

### Logs
Configuration dans `src/main/resources/logback.xml` (à créer si nécessaire)

## 🐛 Dépannage

### Problème de connexion MySQL
1. Vérifier que MySQL est démarré
2. Tester la connexion : `mysql -u root -p`
3. Vérifier les paramètres dans `database.properties`
4. S'assurer que la base `gestion_cours_db` existe

### Erreur "ClassNotFoundException"
```bash
mvn clean compile exec:java -Dexec.mainClass="com.gestion.cours.Main"
```

### Look and Feel non supporté
L'application utilise automatiquement le look-and-feel système si FlatLaf n'est pas disponible.

## 📁 Structure du projet

```
src/
├── main/
│   ├── java/com/gestion/cours/
│   │   ├── dao/           # Accès aux données
│   │   ├── model/         # Entités métier
│   │   ├── ui/            # Interface utilisateur
│   │   │   └── panels/    # Panneaux spécialisés
│   │   ├── util/          # Utilitaires
│   │   └── Main.java      # Point d'entrée
│   └── resources/
│       ├── database_schema.sql
│       └── database.properties
└── test/java/             # Tests unitaires
```

## 🔄 Développement futur

### Prochaines fonctionnalités
1. **Module Inscriptions** complet
2. **Système de notifications**
3. **Export PDF/Excel** des rapports
4. **Calendrier** des cours
5. **Interface web** (Spring Boot)
6. **API REST** pour intégrations

### Contributions
Les contributions sont bienvenues ! Veuillez :
1. Fork le projet
2. Créer une branche feature
3. Commiter les changements
4. Ouvrir une Pull Request

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 👥 Auteurs

- Développeur Principal - Application de gestion de cours d'apprentissage

## 📞 Support

Pour toute question ou problème :
1. Vérifier la section dépannage ci-dessus
2. Consulter les logs de l'application
3. Ouvrir une issue sur le repository

---

**Version :** 1.0.0  
**Dernière mise à jour :** Décembre 2024