@echo off
REM Script de lancement pour l'application Gestion de Cours (Windows)
REM Version 1.0.0

setlocal enabledelayedexpansion

set JAR_NAME=gestion-cours-app-1.0.0-jar-with-dependencies.jar
set MAIN_CLASS=com.gestion.cours.Main
set LOG_DIR=logs
set CONFIG_FILE=src\main\resources\database.properties

echo === Gestion de Cours d'Apprentissage ===
echo Version 1.0.0
echo.

if "%1"=="" goto help
if "%1"=="help" goto help
if "%1"=="--help" goto help
if "%1"=="-h" goto help
if "%1"=="compile" goto compile
if "%1"=="package" goto package
if "%1"=="run" goto run
if "%1"=="clean" goto clean
if "%1"=="setup" goto setup
if "%1"=="test-db" goto test-db

echo Option inconnue: %1
echo.
goto help

:help
echo Usage: %0 [OPTION]
echo.
echo Options:
echo   compile    Compiler le projet
echo   run        Lancer l'application (compile si necessaire)
echo   package    Creer le JAR executable
echo   clean      Nettoyer les fichiers temporaires
echo   test-db    Tester la connexion a la base de donnees
echo   setup      Configuration initiale
echo   help       Afficher cette aide
echo.
echo Exemples:
echo   %0 run        # Lancer l'application
echo   %0 compile    # Compiler seulement
echo   %0 package    # Creer un JAR
goto end

:check_requirements
echo Verification des prerequis...

where java >nul 2>nul
if errorlevel 1 (
    echo ERREUR: Java n'est pas installe ou pas dans le PATH
    echo Installez Java 11 ou superieur
    exit /b 1
)
echo OK: Java detecte

where mvn >nul 2>nul
if errorlevel 1 (
    echo ERREUR: Maven n'est pas installe ou pas dans le PATH
    echo Installez Apache Maven
    exit /b 1
)
echo OK: Maven detecte

where mysql >nul 2>nul
if errorlevel 1 (
    echo ATTENTION: MySQL non detecte (necessaire pour l'execution)
) else (
    echo OK: MySQL detecte
)
echo.
goto :eof

:compile
call :check_requirements
if errorlevel 1 goto end

echo Compilation du projet...
call mvn clean compile
if errorlevel 1 (
    echo ERREUR: Echec de la compilation
    goto end
)
echo OK: Compilation reussie
goto end

:package
call :check_requirements
if errorlevel 1 goto end

echo Creation du JAR executable...
call mvn clean package
if errorlevel 1 (
    echo ERREUR: Echec de la creation du JAR
    goto end
)
echo OK: JAR cree: target\%JAR_NAME%
goto end

:run
call :check_requirements
if errorlevel 1 goto end

REM Creer le repertoire de logs
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

REM Verifier si le JAR existe
if exist "target\%JAR_NAME%" (
    echo Lancement via JAR...
    java -jar "target\%JAR_NAME%"
) else (
    echo JAR non trouve, lancement via Maven...
    call mvn exec:java -Dexec.mainClass="%MAIN_CLASS%"
)
goto end

:test-db
echo Test de la connexion a la base de donnees...

if not exist "%CONFIG_FILE%" (
    echo ERREUR: Fichier de configuration non trouve: %CONFIG_FILE%
    goto end
)

echo Configuration trouvee dans %CONFIG_FILE%
echo Verifiez manuellement les parametres de connexion
goto end

:setup
call :check_requirements
if errorlevel 1 goto end

echo Configuration initiale...

REM Creer les repertoires necessaires
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
echo OK: Repertoire de logs cree

REM Verifier le fichier de configuration
if not exist "%CONFIG_FILE%" (
    echo ERREUR: Fichier de configuration manquant: %CONFIG_FILE%
    echo Creez ce fichier avec les parametres de votre base de donnees
    goto end
) else (
    echo OK: Fichier de configuration trouve
)

REM Test de compilation
echo Test de compilation...
call mvn clean compile
if errorlevel 1 (
    echo ERREUR: Echec du test de compilation
    goto end
)

echo OK: Configuration terminee
echo.
echo Prochaines etapes:
echo 1. Configurez votre base de donnees MySQL
echo 2. Editez %CONFIG_FILE% avec vos parametres
echo 3. Lancez: %0 run
goto end

:clean
echo Nettoyage...
call mvn clean
if exist "%LOG_DIR%\*.log" del /q "%LOG_DIR%\*.log"
echo OK: Nettoyage termine
goto end

:end
pause