@echo off
echo Nettoyage et compilation du projet...
call ./gradlew clean build -x test

echo Execution des benchmarks...
echo Cela peut prendre un certain temps...
call ./gradlew jmh

echo Affichage des resultats :
if exist build\results\jmh\results.txt (
    type build\results\jmh\results.txt
) else (
    echo Aucun resultat trouve dans build\results\jmh\results.txt
)

pause