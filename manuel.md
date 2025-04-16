# Compilation

Pour télécharger les dépendences et compiler:
./gradlew install OU gradle install

La commande créé un exécutable appelé 'app' dans le dossier app/build/install/app/bin.

Pour exécuter :
./app/build/install/app/bin/app [program] [options] 

(voir section 'Usage et options') pour les choix possibles.

# Usage et options
usage: [program] [options]

PROGRAM :

- world : raylib rendering
- grille : 2D grid Swing
- sansGrille : Swing without grid

OPTIONS WORLD:

## BENCHMARK
- benchmark-generate: generate random 2D graphs

- benchmark-run: Run all algorithms against 2D graphs

- benchmark-generate3d: generate random 3D graphs

- benchmark-run3d: Run all algorithms against 3D graphs

## DRAW
- draw-astar GRAPHLOCATION : execute A* on GRAPHLOCATION

- draw-astar3d GRAPHLOCATION : execute A* on GRAPHLOCATION

- draw-dijkstra GRAPHLOCATION : execute dijkstra on GRAPHLOCATION

- draw-quadtree GRAPHLOCATION : execute quadtree on GRAPHLOCATION

OPTIONS GRILLE:

OPTIONS SANS_GRILLE:

exemple : 
- Pour générer les graphes 2D du benchmark :
	./app/build/install/app/bin/app world benchmark-generate
- Pour générer les résultats du benchmark :
	./app/build/install/app/bin/app world benchmark-generate
