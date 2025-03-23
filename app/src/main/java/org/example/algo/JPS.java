package org.example.algo;

import java.awt.*;
import java.util.*;
import java.util.List;

public class JPS extends AStarAlgorithm {

  // Nombre maximal d'étapes pour trouver un jump point (à changer si nécessaire,
  // en fct de la taille de la grille)
  private static final int MAX_STEPS = 1000;

  // Constructeur
  public JPS(List<Point> pointsSelectionnes, List<Point> obstacles, int lignes, int colonnes) {
    super(pointsSelectionnes, obstacles, lignes, colonnes);
  }

  /**** RECHERCHE ****/
  /*******************/

  // Fonction principale de recherche de chemin
  @Override
  public ArrayList<Point> calculChemin() {
      initialise();
      PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
      Map<Integer, Double> gScore = new HashMap<>();
      Set<Integer> closedSet = new HashSet<>();
      
      gScore.put(indexDepart, 0.0);
      openSet.add(new Node(indexDepart, heuristique(indexDepart)));
      
      while (!openSet.isEmpty()) {
          Node currentNode = openSet.poll();
          int current = currentNode.index;
          
          if (current == indexArrivee) {
              System.out.println("Distance : " + gScore.get(indexArrivee));
              return cheminPoints(pred);
          }
          
          if (closedSet.contains(current)) {
              continue;
          }
          
          closedSet.add(current);
          
          // Debug information
          //System.out.println("Exploring node: " + current);
          
          ArrayList<Integer> jumpPoints = getIndexVoisins(current);
          //System.out.println("Found jump points: " + jumpPoints);
          
          for (int jumpPoint : jumpPoints) {
              if (closedSet.contains(jumpPoint)) {
                  continue;
              }
              
              double newGScore = gScore.get(current) + getDist(current, jumpPoint);
              
              if (newGScore < gScore.getOrDefault(jumpPoint, Double.POSITIVE_INFINITY)) {
                  pred.put(jumpPoint, current);
                  gScore.put(jumpPoint, newGScore);
                  
                  // Supression du noeud existant si présent
                  openSet.removeIf(node -> node.index == jumpPoint);
                  openSet.add(new Node(jumpPoint, newGScore + heuristique(jumpPoint)));
                  
                  System.out.println("Updated node: " + jumpPoint + " with gScore: " + newGScore);
              }
          }
      }
      
      System.out.println("Pas de chemin trouvé");
      return new ArrayList<>();
  }

  // Retourne les voisins d'un sommet
  @Override
  protected ArrayList<Integer> getIndexVoisins(int sommet) {
    int[] directions = getDirectionFromParent(sommet);
    return getPrunedNeighbors(sommet, directions[0], directions[1]);
  }

  // Trouve le prochain jump point dans la direction donnée
  private int findJumpPoint(int current, int directionX, int directionY) {
    int x = current / colonnes;
    int y = current % colonnes;
    
    // Vérifie si le premier pas est possible
    if (isObstacle(x + directionX, y + directionY)) {
        return -1;
    }
    
    x += directionX;
    y += directionY;
    int currentIndex = getIndex(new Point(x, y));
    
    // Si on arrive à la destination
    if (currentIndex == indexArrivee) {
        return currentIndex;
    }
    
    // Si on a trouvé un voisin forcé
    if (hasAnyForcedNeighbor(x, y, directionX, directionY)) {
        return currentIndex;
    }
    
    // Pour les mouvements diagonaux
    if (directionX != 0 && directionY != 0) {
        int horizontal = findJumpPoint(currentIndex, directionX, 0);
        int vertical = findJumpPoint(currentIndex, 0, directionY);
        if (horizontal != -1 || vertical != -1) {
            return currentIndex;
        }
    }
    
    // Continue dans la même direction
    return findJumpPoint(currentIndex, directionX, directionY);
  }


  /**** VOISINS ****/
  /*****************/

  // Retourne les voisins valides d'un sommet en fonction de la direction du
  // parent
  private ArrayList<Integer> getPrunedNeighbors(int sommet, int dx, int dy) {
    ArrayList<Integer> indexVoisins = new ArrayList<>();
    int x = sommet / colonnes;
    int y = sommet % colonnes;

    if (dx == 0 && dy == 0) {
        // Point de départ : exploration dans toutes les directions
        int[][] directions = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0},  // Directions cardinales
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1} // Directions diagonales
        };
        
        for (int[] dir : directions) {
            if (!isObstacle(x + dir[0], y + dir[1])) {
                int jumpPoint = findJumpPoint(sommet, dir[0], dir[1]);
                if (jumpPoint != -1) {
                    indexVoisins.add(jumpPoint);
                }
            }
        }
    } else {
        // Direction naturelle
        int jumpPoint = findJumpPoint(sommet, dx, dy);
        if (jumpPoint != -1) {
            indexVoisins.add(jumpPoint);
        }

        // Voisins forcés
        if (dx != 0 && dy != 0) {
            // Pour les mouvements diagonaux
            if (!isObstacle(x + dx, y)) {
                int horizontalJump = findJumpPoint(sommet, dx, 0);
                if (horizontalJump != -1) indexVoisins.add(horizontalJump);
            }
            if (!isObstacle(x, y + dy)) {
                int verticalJump = findJumpPoint(sommet, 0, dy);
                if (verticalJump != -1) indexVoisins.add(verticalJump);
            }
        } else {
            // Pour les mouvements cardinaux
            if (dx != 0) {
                // Mouvement horizontal
                if (isObstacle(x, y + 1) && !isObstacle(x + dx, y + 1)) {
                    indexVoisins.add(getIndex(new Point(x + dx, y + 1)));
                }
                if (isObstacle(x, y - 1) && !isObstacle(x + dx, y - 1)) {
                    indexVoisins.add(getIndex(new Point(x + dx, y - 1)));
                }
            } else {
                // Mouvement vertical
                if (isObstacle(x + 1, y) && !isObstacle(x + 1, y + dy)) {
                    indexVoisins.add(getIndex(new Point(x + 1, y + dy)));
                }
                if (isObstacle(x - 1, y) && !isObstacle(x - 1, y + dy)) {
                    indexVoisins.add(getIndex(new Point(x - 1, y + dy)));
                }
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
      return new int[] { 0, 0 }; // Noeud de départ
    }

    int currentX = current / colonnes;
    int currentY = current % colonnes;
    int parentX = parentIndex / colonnes;
    int parentY = parentIndex % colonnes;

    return new int[] {
        Integer.compare(currentX - parentX, 0),
        Integer.compare(currentY - parentY, 0)
    };
  }

  // Récupère l'index du parent d'un sommet
  private int getParentIndex(int sommet) {
    return pred.getOrDefault(sommet, -1);
  }

}