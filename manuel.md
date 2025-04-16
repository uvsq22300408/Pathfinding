# Compilation

Pour télécharger les dépendences :
./gradlew install
gradle install

Pour compiler :
./gradlew build
gradle build

Pour exécuter :
./gradlew run --args "[program] [options]" (voir section 'Usage et options')
gradle run --args "[program] [options]" (voir section 'Usage et options')

# Usage et options
usage: [program] [options]

PROGRAM :

- world : raylib rendering
- grille : 2D grid Swing
- sansGrille : Swing without grid

OPTIONS WORLD:

- benchmark-generate: generate random graphs in benchmark folder

- benchmark-run: Run all algorithms against the graphs in app/benchmark/

- draw-astar GRAPHNAME : execute A* on GRAPHNAME from app/benchmarks/

- draw-dijkstra GRAPHNAME : execute dijkstra on GRAPHNAME from app/benchmarks/

- draw-quadtree GRAPHNAME : execute quadtree on GRAPHNAME from app/benchmarks/

OPTIONS GRILLE:

OPTIONS SANS_GRILLE:

exemple : 
- Pour générer les graphes du benchmark :
	./gradlew run --args "world benchmark-generate"
- Pour générer les résultats du benchmark :
	./gradlew run --args "world benchmark-run"
