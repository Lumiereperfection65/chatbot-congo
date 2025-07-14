# Guide d'Installation - Gestion de Cours d'Apprentissage

## 🔧 Installation étape par étape

### Étape 1 : Préparation de l'environnement

#### 1.1 Installer Java 11+
**Windows :**
```powershell
# Télécharger OpenJDK depuis https://adoptium.net/
# Ou utiliser Chocolatey
choco install openjdk11
```

**macOS :**
```bash
# Avec Homebrew
brew install openjdk@11

# Ajouter au PATH
echo 'export PATH="/opt/homebrew/opt/openjdk@11/bin:$PATH"' >> ~/.zshrc
```

**Linux (Ubuntu/Debian) :**
```bash
sudo apt update
sudo apt install openjdk-11-jdk
```

#### 1.2 Installer Maven
**Windows :**
```powershell
choco install maven
```

**macOS :**
```bash
brew install maven
```

**Linux :**
```bash
sudo apt install maven
```

#### 1.3 Installer MySQL 8.0+
**Windows :**
```powershell
choco install mysql
```

**macOS :**
```bash
brew install mysql
```

**Linux :**
```bash
sudo apt install mysql-server
```

### Étape 2 : Configuration de MySQL

#### 2.1 Démarrer MySQL
**Windows :**
```powershell
net start mysql
```

**macOS/Linux :**
```bash
sudo systemctl start mysql
# ou
sudo service mysql start
```

#### 2.2 Sécuriser l'installation (recommandé)
```bash
sudo mysql_secure_installation
```

#### 2.3 Créer un utilisateur pour l'application (optionnel)
```sql
mysql -u root -p

CREATE USER 'gestion_cours'@'localhost' IDENTIFIED BY 'motdepasse_securise';
GRANT ALL PRIVILEGES ON gestion_cours_db.* TO 'gestion_cours'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### Étape 3 : Clonage et compilation du projet

#### 3.1 Cloner le repository
```bash
git clone <url-du-repository>
cd gestion-cours-app
```

#### 3.2 Initialiser la base de données
```bash
mysql -u root -p < src/main/resources/database_schema.sql
```

#### 3.3 Configurer la connexion
Éditer `src/main/resources/database.properties` :
```properties
# Configuration de base
db.url=jdbc:mysql://localhost:3306/gestion_cours_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=root
db.password=votre_mot_de_passe
db.driver=com.mysql.cj.jdbc.Driver

# Si vous avez créé un utilisateur spécifique
db.username=gestion_cours
db.password=motdepasse_securise
```

#### 3.4 Compiler le projet
```bash
mvn clean compile
```

### Étape 4 : Premier lancement

#### 4.1 Lancement en mode développement
```bash
mvn exec:java -Dexec.mainClass="com.gestion.cours.Main"
```

#### 4.2 Créer un JAR exécutable
```bash
mvn clean package
java -jar target/gestion-cours-app-1.0.0-jar-with-dependencies.jar
```

## 🔍 Vérifications post-installation

### Test de connexion MySQL
```bash
mysql -u root -p
USE gestion_cours_db;
SHOW TABLES;
SELECT COUNT(*) FROM professeurs;
```

### Test de l'application
1. Lancer l'application
2. Vérifier la barre de statut (connexion DB)
3. Naviguer vers l'onglet "Professeurs"
4. Vérifier que les données de test apparaissent

## 🐛 Résolution des problèmes courants

### Problème : "Access denied for user"
**Solution :**
```sql
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'nouveau_mot_de_passe';
FLUSH PRIVILEGES;
```

### Problème : "Communications link failure"
**Causes possibles :**
1. MySQL n'est pas démarré
2. Port 3306 bloqué
3. Configuration firewall

**Solutions :**
```bash
# Vérifier le statut MySQL
sudo systemctl status mysql

# Vérifier le port
netstat -tlnp | grep :3306

# Redémarrer MySQL
sudo systemctl restart mysql
```

### Problème : "Package com.mysql.cj.jdbc does not exist"
**Solution :**
```bash
mvn clean install
mvn dependency:resolve
```

### Problème : "Java heap space"
**Solution :**
```bash
export MAVEN_OPTS="-Xmx1024m"
mvn exec:java -Dexec.mainClass="com.gestion.cours.Main"
```

## 🔧 Configuration pour production

### 1. Utiliser un utilisateur MySQL dédié
```sql
CREATE USER 'gestion_cours_prod'@'localhost' IDENTIFIED BY 'mot_de_passe_complexe';
GRANT SELECT, INSERT, UPDATE, DELETE ON gestion_cours_db.* TO 'gestion_cours_prod'@'localhost';
```

### 2. Configurer les logs
Créer `src/main/resources/logback.xml` :
```xml
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/gestion-cours.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

### 3. Script de démarrage automatique
**Linux (systemd) :**
```ini
# /etc/systemd/system/gestion-cours.service
[Unit]
Description=Gestion de Cours Application
After=mysql.service

[Service]
Type=simple
User=gestion-cours
ExecStart=/usr/bin/java -jar /opt/gestion-cours/gestion-cours-app-1.0.0-jar-with-dependencies.jar
Restart=always

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl enable gestion-cours
sudo systemctl start gestion-cours
```

## 📦 Déploiement

### 1. Préparer le package de déploiement
```bash
mvn clean package
mkdir deploy
cp target/gestion-cours-app-1.0.0-jar-with-dependencies.jar deploy/
cp src/main/resources/database_schema.sql deploy/
cp src/main/resources/database.properties deploy/database.properties.template
```

### 2. Script d'installation automatique
```bash
#!/bin/bash
# install.sh

echo "Installation de Gestion de Cours..."

# Vérifier Java
if ! command -v java &> /dev/null; then
    echo "Java n'est pas installé. Installation..."
    sudo apt install openjdk-11-jdk
fi

# Vérifier MySQL
if ! command -v mysql &> /dev/null; then
    echo "MySQL n'est pas installé. Installation..."
    sudo apt install mysql-server
fi

# Créer la base de données
echo "Configuration de la base de données..."
mysql -u root -p < database_schema.sql

# Configurer l'application
echo "Configuration de l'application..."
cp database.properties.template database.properties
echo "Veuillez éditer database.properties avec vos paramètres MySQL"

echo "Installation terminée !"
```

## 🔄 Mise à jour

### 1. Sauvegarde avant mise à jour
```bash
mysqldump -u root -p gestion_cours_db > backup_$(date +%Y%m%d).sql
```

### 2. Appliquer la mise à jour
```bash
git pull origin main
mvn clean package
# Arrêter l'ancienne version
# Démarrer la nouvelle version
```

## 📞 Support technique

### Collecte d'informations pour le debug
```bash
# Version Java
java -version

# Version MySQL
mysql --version

# Logs de l'application
tail -f logs/gestion-cours.log

# Test de connexion réseau
telnet localhost 3306
```

### Fichiers de configuration à vérifier
- `src/main/resources/database.properties`
- `src/main/resources/logback.xml` (si existant)
- `/etc/mysql/my.cnf` (configuration MySQL)

---

Pour toute assistance supplémentaire, consultez le fichier README.md ou ouvrez une issue sur le repository.