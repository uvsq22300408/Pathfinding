package org.example.algo;

import java.awt.*;
import java.util.*;
import java.util.List;

public class JPS extends AStarAlgorithm {

    // Nombre maximal d'étapes pour trouver un jump point (à changer si nécessaire, en fct de la taille de la grille)
    private static final int MAX_STEPS = 1000;

    // Cache pour éviter de recalculer les mêmes jump points
    private Map<String, Integer> jumpPointCache = new HashMap<>();

    public JPS(List<Point> pointsSelectionnes, List<Point> obstacles, int lignes, int colonnes) {
        super(pointsSelectionnes, obstacles, lignes, colonnes);
    }

    /*
     * Cette méthode trouve le prochain jump point dans une direction donnée. Elle
     * est appelée pour chaque direction possible depuis un noeud.
     */
    private int findJumpPoint(int current, int directionX, int directionY) {
        String key = current + "," + directionX + "," + directionY;
        if (jumpPointCache.containsKey(key)) {
            return jumpPointCache.get(key);
        }
    
        int x = current / colonnes;
        int y = current % colonnes;
        
        int steps = 0;
        while (true) {
            steps += 1;
            if (steps > MAX_STEPS) {
                int result = -1;
                jumpPointCache.put(key, result);
                return result;
            }
    
            x += directionX;
            y += directionY;
    
            // Vérifie les limites de la grille
            if (x < 0 || x >= lignes || y < 0 || y >= colonnes) {
                int result = -1;
                jumpPointCache.put(key, result);
                return result;
            }
    
            int currentIndex = getIndex(new Point(x, y));
    
            // Obstacle ?
            if (grille.get(currentIndex) == 3) {
                // Alors retourne le jump point précédent
                if (steps > 1) {
                    int result = getIndex(new Point(x - directionX, y - directionY));
                    jumpPointCache.put(key, result);
                    return result;
                }
                // Si step == 1, la direction est bloqué entièrement
                int result = -1;
                jumpPointCache.put(key, result);
                return result;
            }
    
            // Point d'arrivée ?
            if (currentIndex == indexArrivee) {
                jumpPointCache.put(key, currentIndex);
                return currentIndex;
            }
    
            // Changements de direction forcés ?
            if (hasAnyForcedNeighbor(x, y, directionX, directionY)) {
                jumpPointCache.put(key, currentIndex);
                return currentIndex;
            }
    
            // Jump points diagonaux ?
            if (directionX != 0 && directionY != 0) {
                if (findJumpPoint(currentIndex, directionX, 0) != -1 || 
                    findJumpPoint(currentIndex, 0, directionY) != -1) {
                    jumpPointCache.put(key, currentIndex);
                    return currentIndex;
                }
            }
        }
    }
  
    /*
     * Vérifie si un sommet a un voisin forcé dans une direction donnée.
     */
    private boolean hasAnyForcedNeighbor(int x, int y, int directionX, int directionY) {
      if (directionX != 0 && directionY != 0) {
          // Diagonale
          return (isObstacle(x - directionX, y) && !isObstacle(x - directionX, y + directionY)) ||
                 (isObstacle(x, y - directionY) && !isObstacle(x + directionX, y - directionY));
      } else if (directionX != 0) {
          // Horizontale
          return (isObstacle(x, y + 1) && !isObstacle(x + directionX, y + 1)) ||
                 (isObstacle(x, y - 1) && !isObstacle(x + directionX, y - 1));
      } else if (directionY != 0) {
          // Verticale
          return (isObstacle(x + 1, y) && !isObstacle(x + 1, y + directionY)) ||
                 (isObstacle(x - 1, y) && !isObstacle(x - 1, y + directionY));
      }
      return false;
    }
  
    /*
     * Vérifie si un sommet est un obstacle.
     */
    private boolean isObstacle(int x, int y) {
      if (x < 0 || x >= lignes || y < 0 || y >= colonnes) {
          return true;
      }
      return grille.get(getIndex(new Point(x, y))) == 3;
    }

    /*
     * Cette méthode utilise la méthode findJumpPoint pour identifier les
     * jump points dans chaque direction d'un sommet.
     * Optimisation possible : ne pas vérifier les directions déjà explorées par un noeud parent
     */
    @Override
    protected ArrayList<Integer> getIndexVoisins(int sommet) {
        ArrayList<Integer> indexVoisins = new ArrayList<>();

        // Directions possibles (cardinales et diagonales)
        int[][] directions = {
                { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 },
                { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
        };

        for (int[] direction : directions) {
            int jumpPoint = findJumpPoint(sommet, direction[0], direction[1]);
            if (jumpPoint != -1) {
                indexVoisins.add(jumpPoint);
            }
        }

        return indexVoisins;
    }
}