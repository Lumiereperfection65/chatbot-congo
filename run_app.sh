#!/bin/bash

# Script de lancement de l'application Gestion de Cours 2025
# Usage: ./run_app.sh

echo "🎓 Gestion de Cours 2025 - Plateforme d'Apprentissage en Ligne"
echo "=============================================================="

# Vérifier que Java est installé
if ! command -v java &> /dev/null; then
    echo "❌ Java n'est pas installé. Veuillez installer Java JDK 11 ou supérieur."
    exit 1
fi

# Vérifier que Maven est installé
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven n'est pas installé. Veuillez installer Maven 3.6 ou supérieur."
    exit 1
fi

echo "✅ Java et Maven détectés"

# Vérifier si MySQL est en cours d'exécution
if command -v mysql &> /dev/null; then
    echo "✅ MySQL détecté"
else
    echo "⚠️  MySQL non détecté. Assurez-vous que MySQL est installé et démarré."
fi

# Compiler le projet
echo ""
echo "🔨 Compilation du projet..."
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la compilation"
    exit 1
fi

echo "✅ Compilation réussie"

# Exécuter l'application
echo ""
echo "🚀 Lancement de l'application..."
echo "📝 Comptes de test disponibles:"
echo "   Professeurs: jean.dupont@email.com, marie.martin@email.com"
echo "   Étudiants: thomas.leroy@email.com, emma.moreau@email.com" 
echo "   Mot de passe pour tous: password123"
echo ""

mvn exec:java -Dexec.mainClass="com.gestion.cours.Main" -q

echo ""
echo "👋 Application fermée. Merci d'avoir utilisé Gestion de Cours 2025 !"