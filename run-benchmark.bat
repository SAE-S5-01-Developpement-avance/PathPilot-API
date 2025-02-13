@echo off
echo Nettoyage et compilation du projet...
call ./gradlew clean build -x test

echo Execution des benchmarks...
echo Cela peut prendre un certain temps...
call ./gradlew jmh

echo Affichage des resultats :
if exist build\reports\jmh\results.txt (
    type build\reports\jmh\results.txt
) else (
    echo Aucun resultat trouve dans build\reports\jmh\results.txt
)

pause