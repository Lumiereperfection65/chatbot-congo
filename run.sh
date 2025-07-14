#!/bin/bash

# Script de lancement pour l'application Gestion de Cours
# Version 1.0.0

set -e

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
JAR_NAME="gestion-cours-app-1.0.0-jar-with-dependencies.jar"
MAIN_CLASS="com.gestion.cours.Main"
LOG_DIR="logs"
CONFIG_FILE="src/main/resources/database.properties"

echo -e "${BLUE}=== Gestion de Cours d'Apprentissage ===${NC}"
echo -e "${BLUE}Version 1.0.0${NC}"
echo ""

# Fonction pour afficher l'aide
show_help() {
    echo "Usage: $0 [OPTION]"
    echo ""
    echo "Options:"
    echo "  compile    Compiler le projet"
    echo "  run        Lancer l'application (compile si nécessaire)"
    echo "  package    Créer le JAR exécutable"
    echo "  clean      Nettoyer les fichiers temporaires"
    echo "  test-db    Tester la connexion à la base de données"
    echo "  setup      Configuration initiale"
    echo "  help       Afficher cette aide"
    echo ""
    echo "Exemples:"
    echo "  $0 run        # Lancer l'application"
    echo "  $0 compile    # Compiler seulement"
    echo "  $0 package    # Créer un JAR"
}

# Vérifier les prérequis
check_requirements() {
    echo -e "${YELLOW}Vérification des prérequis...${NC}"
    
    # Vérifier Java
    if ! command -v java &> /dev/null; then
        echo -e "${RED}❌ Java n'est pas installé ou pas dans le PATH${NC}"
        echo "Installez Java 11 ou supérieur"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo -e "${GREEN}✓ Java détecté: $JAVA_VERSION${NC}"
    
    # Vérifier Maven
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}❌ Maven n'est pas installé ou pas dans le PATH${NC}"
        echo "Installez Apache Maven"
        exit 1
    fi
    
    MVN_VERSION=$(mvn -version 2>&1 | head -n1 | awk '{print $3}')
    echo -e "${GREEN}✓ Maven détecté: $MVN_VERSION${NC}"
    
    # Vérifier MySQL (optionnel pour la compilation)
    if command -v mysql &> /dev/null; then
        MYSQL_VERSION=$(mysql --version | awk '{print $5}' | sed 's/,//')
        echo -e "${GREEN}✓ MySQL détecté: $MYSQL_VERSION${NC}"
    else
        echo -e "${YELLOW}⚠ MySQL non détecté (nécessaire pour l'exécution)${NC}"
    fi
}

# Compiler le projet
compile_project() {
    echo -e "${YELLOW}Compilation du projet...${NC}"
    mvn clean compile
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Compilation réussie${NC}"
    else
        echo -e "${RED}❌ Échec de la compilation${NC}"
        exit 1
    fi
}

# Créer le JAR
package_project() {
    echo -e "${YELLOW}Création du JAR exécutable...${NC}"
    mvn clean package
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ JAR créé: target/$JAR_NAME${NC}"
    else
        echo -e "${RED}❌ Échec de la création du JAR${NC}"
        exit 1
    fi
}

# Lancer l'application
run_application() {
    # Créer le répertoire de logs s'il n'existe pas
    mkdir -p "$LOG_DIR"
    
    # Vérifier si le JAR existe
    if [ -f "target/$JAR_NAME" ]; then
        echo -e "${YELLOW}Lancement via JAR...${NC}"
        java -jar "target/$JAR_NAME"
    else
        echo -e "${YELLOW}JAR non trouvé, lancement via Maven...${NC}"
        mvn exec:java -Dexec.mainClass="$MAIN_CLASS"
    fi
}

# Tester la connexion DB
test_database() {
    echo -e "${YELLOW}Test de la connexion à la base de données...${NC}"
    
    if [ ! -f "$CONFIG_FILE" ]; then
        echo -e "${RED}❌ Fichier de configuration non trouvé: $CONFIG_FILE${NC}"
        exit 1
    fi
    
    # Extraire les paramètres de connexion
    DB_URL=$(grep "db.url" "$CONFIG_FILE" | cut -d'=' -f2)
    DB_USER=$(grep "db.username" "$CONFIG_FILE" | cut -d'=' -f2)
    
    if [ -z "$DB_URL" ] || [ -z "$DB_USER" ]; then
        echo -e "${RED}❌ Configuration incomplète dans $CONFIG_FILE${NC}"
        exit 1
    fi
    
    echo "URL: $DB_URL"
    echo "Utilisateur: $DB_USER"
    
    # Test de base (nécessite mysql client)
    if command -v mysql &> /dev/null; then
        echo "Test de la connexion..."
        # Note: Ce test nécessitera le mot de passe
        mysql -u "$DB_USER" -p -e "SELECT 1;" 2>/dev/null && echo -e "${GREEN}✓ Connexion réussie${NC}" || echo -e "${RED}❌ Échec de la connexion${NC}"
    else
        echo -e "${YELLOW}⚠ Client MySQL non disponible pour le test${NC}"
    fi
}

# Configuration initiale
setup_application() {
    echo -e "${YELLOW}Configuration initiale...${NC}"
    
    # Créer les répertoires nécessaires
    mkdir -p "$LOG_DIR"
    echo -e "${GREEN}✓ Répertoire de logs créé${NC}"
    
    # Vérifier le fichier de configuration
    if [ ! -f "$CONFIG_FILE" ]; then
        echo -e "${RED}❌ Fichier de configuration manquant: $CONFIG_FILE${NC}"
        echo "Créez ce fichier avec les paramètres de votre base de données"
        exit 1
    else
        echo -e "${GREEN}✓ Fichier de configuration trouvé${NC}"
    fi
    
    # Test de compilation
    echo -e "${YELLOW}Test de compilation...${NC}"
    compile_project
    
    echo -e "${GREEN}✓ Configuration terminée${NC}"
    echo ""
    echo "Prochaines étapes:"
    echo "1. Configurez votre base de données MySQL"
    echo "2. Éditez $CONFIG_FILE avec vos paramètres"
    echo "3. Lancez: $0 run"
}

# Nettoyer
clean_project() {
    echo -e "${YELLOW}Nettoyage...${NC}"
    mvn clean
    rm -rf "$LOG_DIR"/*.log 2>/dev/null || true
    echo -e "${GREEN}✓ Nettoyage terminé${NC}"
}

# Script principal
main() {
    case "${1:-help}" in
        "compile")
            check_requirements
            compile_project
            ;;
        "package")
            check_requirements
            package_project
            ;;
        "run")
            check_requirements
            if [ ! -f "target/$JAR_NAME" ]; then
                echo -e "${YELLOW}JAR non trouvé, compilation...${NC}"
                package_project
            fi
            run_application
            ;;
        "test-db")
            test_database
            ;;
        "setup")
            check_requirements
            setup_application
            ;;
        "clean")
            clean_project
            ;;
        "help"|"--help"|"-h")
            show_help
            ;;
        *)
            echo -e "${RED}Option inconnue: $1${NC}"
            echo ""
            show_help
            exit 1
            ;;
    esac
}

# Point d'entrée
main "$@"