@echo off
chcp 65001 >nul

REM Script de lancement de l'application Gestion de Cours 2025
REM Usage: run_app.bat

echo 🎓 Gestion de Cours 2025 - Plateforme d'Apprentissage en Ligne
echo ==============================================================

REM Vérifier que Java est installé
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java n'est pas installé. Veuillez installer Java JDK 11 ou supérieur.
    pause
    exit /b 1
)

REM Vérifier que Maven est installé
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven n'est pas installé. Veuillez installer Maven 3.6 ou supérieur.
    pause
    exit /b 1
)

echo ✅ Java et Maven détectés

REM Vérifier MySQL
mysql --version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ MySQL détecté
) else (
    echo ⚠️  MySQL non détecté. Assurez-vous que MySQL est installé et démarré.
)

REM Compiler le projet
echo.
echo 🔨 Compilation du projet...
call mvn clean compile -q

if %errorlevel% neq 0 (
    echo ❌ Erreur lors de la compilation
    pause
    exit /b 1
)

echo ✅ Compilation réussie

REM Exécuter l'application
echo.
echo 🚀 Lancement de l'application...
echo 📝 Comptes de test disponibles:
echo    Professeurs: jean.dupont@email.com, marie.martin@email.com
echo    Étudiants: thomas.leroy@email.com, emma.moreau@email.com
echo    Mot de passe pour tous: password123
echo.

call mvn exec:java -Dexec.mainClass="com.gestion.cours.Main" -q

echo.
echo 👋 Application fermée. Merci d'avoir utilisé Gestion de Cours 2025 !
pause