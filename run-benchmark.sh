#!/bin/bash

echo "Nettoyage et compilation du projet..."
./gradlew clean build -x test

echo "Execution des benchmarks..."
echo "Cela peut prendre un certain temps, veuillez patienter..."
./gradlew jmh

echo "Affichage des resultats :"
if [ -f build/results/jmh/results.txt ]; then
    cat build/results/jmh/results.txt
else
    echo "Aucun resultat trouve dans build/results/jmh/results.txt"
fi

read -pr "Appuyez sur Entrée pour continuer..."