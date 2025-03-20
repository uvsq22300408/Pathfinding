package org.example.algo;

import java.awt.*;
import java.util.*;
import java.util.List;

public class JPS extends AStarAlgorithm {

    // Nombre maximal d'étapes pour trouver un jump point (à changer si nécessaire, en fct de la taille de la grille)
    private static final int MAX_STEPS = 1000;

    // Cache pour éviter de recalculer les mêmes jump points
    private Map<String, Integer> jumpPointCache = new HashMap<>();

    // Constructeur
    public JPS(List<Point> pointsSelectionnes, List<Point> obstacles, int lignes, int colonnes) {
        super(pointsSelectionnes, obstacles, lignes, colonnes);
    }

    /**** RECHERCHE ****/
    /*******************/

    // Retourne les voisins d'un sommet
    @Override
    protected ArrayList<Integer> getIndexVoisins(int sommet) {
        int[] directions = getDirectionFromParent(sommet);
        return getPrunedNeighbors(sommet, directions[0], directions[1]);
    }

    // Trouve le prochain jump point dans la direction donnée
    private int findJumpPoint(int current, int directionX, int directionY) {
        String key = current + "," + directionX + "," + directionY;
        if (jumpPointCache.containsKey(key)) {
            return jumpPointCache.get(key);
        }
    
        int x = current / colonnes;
        int y = current % colonnes;
        int previousIndex = current;
    
        // Si le premier pas est bloqué, on cherche immédiatement des alternatives
        if (isObstacle(x + directionX, y + directionY)) {
            // Vérifie d'abord les directions cardinales
            if (directionX != 0 && !isObstacle(x + directionX, y)) {
                int jumpPoint = findJumpPoint(current, directionX, 0);
                if (jumpPoint != -1) {
                    jumpPointCache.put(key, jumpPoint);
                    return jumpPoint;
                }
            }
            if (directionY != 0 && !isObstacle(x, y + directionY)) {
                int jumpPoint = findJumpPoint(current, 0, directionY);
                if (jumpPoint != -1) {
                    jumpPointCache.put(key, jumpPoint);
                    return jumpPoint;
                }
            }
            
            // Ensuite, vérifie les voisins forcés
            if (hasAnyForcedNeighbor(x, y, directionX, directionY)) {
                jumpPointCache.put(key, current);
                return current;
            }
            
            jumpPointCache.put(key, -1);
            return -1;
        }
    
        int steps = 0;
        while (steps++ < MAX_STEPS) {
            x += directionX;
            y += directionY;
    
            if (x < 0 || x >= lignes || y < 0 || y >= colonnes) {
                jumpPointCache.put(key, -1);
                return -1;
            }
    
            int currentIndex = getIndex(new Point(x, y));
    
            // Si on rencontre un obstacle
            if (isObstacle(x, y)) {
                // On vérifie si le point précédent a des voisins forcés
                if (hasAnyForcedNeighbor(x - directionX, y - directionY, directionX, directionY)) {
                    int previousPoint = getIndex(new Point(x - directionX, y - directionY));
                    jumpPointCache.put(key, previousPoint);
                    return previousPoint;
                }
                jumpPointCache.put(key, -1);
                return -1;
            }
    
            pred.put(currentIndex, previousIndex);
            previousIndex = currentIndex;
    
            if (currentIndex == indexArrivee) {
                jumpPointCache.put(key, currentIndex);
                return currentIndex;
            }
    
            // Vérification des voisins forcés au point courant
            if (hasAnyForcedNeighbor(x, y, directionX, directionY)) {
                jumpPointCache.put(key, currentIndex);
                return currentIndex;
            }
    
            // Vérification des directions cardinales pour les mouvements diagonaux
            if (directionX != 0 && directionY != 0) {
                // On vérifie d'abord si un chemin cardinal est possible
                if (!isObstacle(x + directionX, y) || !isObstacle(x, y + directionY)) {
                    if (findJumpPoint(currentIndex, directionX, 0) != -1 || 
                        findJumpPoint(currentIndex, 0, directionY) != -1) {
                        jumpPointCache.put(key, currentIndex);
                        return currentIndex;
                    }
                }
            }
        }
        
        jumpPointCache.put(key, -1);
        return -1;
    }

    /**** VOISINS ****/
    /*****************/
    
    // Retourne les voisins valides d'un sommet en fonction de la direction du parent
    private ArrayList<Integer> getPrunedNeighbors(int sommet, int dx, int dy) {
        ArrayList<Integer> indexVoisins = new ArrayList<>();
        int x = sommet / colonnes;
        int y = sommet % colonnes;
        
        if (dx == 0 && dy == 0) {
            // Point de départ : essaie d'abord les directions cardinales
            int[][] cardinalDirections = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
            boolean foundPath = false;
            for (int[] dir : cardinalDirections) {
                if (!isObstacle(x + dir[0], y + dir[1])) {
                    checkDirection(sommet, dir[0], dir[1], indexVoisins);
                    if (!indexVoisins.isEmpty()) foundPath = true;
                }
            }
            
            // Si aucun chemin cardinal n'est trouvé, essaie les diagonales
            if (!foundPath) {
                int[][] diagonalDirections = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
                for (int[] dir : diagonalDirections) {
                    if (!isObstacle(x + dir[0], y + dir[1])) {
                        checkDirection(sommet, dir[0], dir[1], indexVoisins);
                    }
                }
            }
        } else {
            // Essaie d'abord la direction naturelle
            if (!isObstacle(x + dx, y + dy)) {
                checkDirection(sommet, dx, dy, indexVoisins);
            }
            
            // Pour les mouvements diagonaux, vérifie toujours les directions cardinales
            if (dx != 0 && dy != 0) {
                if (!isObstacle(x + dx, y)) checkDirection(sommet, dx, 0, indexVoisins);
                if (!isObstacle(x, y + dy)) checkDirection(sommet, 0, dy, indexVoisins);
            }
            
            // Si aucun chemin n'est trouvé, vérifie les voisins forcés
            if (indexVoisins.isEmpty() && hasAnyForcedNeighbor(x, y, dx, dy)) {
                // Ajoute les directions forcées possibles
                if (dx != 0) {
                    checkForcedNeighbors(sommet, x, y, dx, 1, indexVoisins);
                    checkForcedNeighbors(sommet, x, y, dx, -1, indexVoisins);
                }
                if (dy != 0) {
                    checkForcedNeighbors(sommet, x, y, 1, dy, indexVoisins);
                    checkForcedNeighbors(sommet, x, y, -1, dy, indexVoisins);
                }
            }
        }
        
        return indexVoisins;
    }

    /**** SECONDAIRES ****/
    /*********************/

    // Vérifie si un sommet a un voisin forcé
    private boolean hasAnyForcedNeighbor(int x, int y, int directionX, int directionY) {
        if (directionX != 0 && directionY != 0) {
            // Mouvement Diagonal
            return (isObstacle(x - directionX, y) && !isObstacle(x - directionX, y + directionY)) ||
                   (isObstacle(x, y - directionY) && !isObstacle(x + directionX, y - directionY));
        } else if (directionX != 0) {
            // Mouvement Horizontal
            return (isObstacle(x, y + 1) && !isObstacle(x + directionX, y + 1)) ||
                 (isObstacle(x, y - 1) && !isObstacle(x + directionX, y - 1));
      } else if (directionY != 0) {
          // Mouvement Vertical
          return (isObstacle(x + 1, y) && !isObstacle(x + 1, y + directionY)) ||
                 (isObstacle(x - 1, y) && !isObstacle(x - 1, y + directionY));
      }
        return false;
    }

    // Vérifie les voisins forcés d'un sommet
    private void checkForcedNeighbors(int sommet, int x, int y, int dx, int dy, ArrayList<Integer> indexVoisins) {
        if (!isObstacle(x + dx, y + dy)) {
            checkDirection(sommet, dx, dy, indexVoisins);
        }
    }
    
    // Vérifie si un jump point est atteignable dans une direction donnée
    private void checkDirection(int sommet, int dx, int dy, ArrayList<Integer> neighbors) {
        int jumpPoint = findJumpPoint(sommet, dx, dy);
        if (jumpPoint != -1) {
            neighbors.add(jumpPoint);
        }
    }

    // Vérifie si un sommet est un obstacle
    private boolean isObstacle(int x, int y) {
        if (x < 0 || x >= lignes || y < 0 || y >= colonnes) {
            return true;
        }
        return grille.get(getIndex(new Point(x, y))) == 3;
    }


    // Récupère la direction du parent d'un sommet
    private int[] getDirectionFromParent(int current) {
        int parentIndex = getParentIndex(current);
        if (parentIndex == -1) {
            return new int[]{0, 0}; // Noeud de départ
        }
        
        int currentX = current / colonnes;
        int currentY = current % colonnes;
        int parentX = parentIndex / colonnes;
        int parentY = parentIndex % colonnes;
        
        return new int[]{
            Integer.compare(currentX - parentX, 0),
            Integer.compare(currentY - parentY, 0)
        };
    }
    
    // Récupère l'index du parent d'un sommet
    private int getParentIndex(int sommet) {
        return pred.getOrDefault(sommet, -1);
    }

}