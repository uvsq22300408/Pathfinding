package org.example.algo;

import java.awt.*;
import java.util.*;
import java.util.List;

public class JPS extends AStarAlgorithm {

  // Nombre maximal d'étapes pour trouver un jump point (à changer si nécessaire,
  // en fct de la taille de la grille)
  private static final int MAX_STEPS = 1000;

  // Constante pour le noeud parent
  private static final int[] NO_DIRECTION = { 0, 0 };

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
      // System.out.println("Exploring node: " + current);

      ArrayList<Integer> jumpPoints = getIndexVoisins(current);
      // System.out.println("Found jump points: " + jumpPoints);

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

    // Ajout des voisins naturels
    List<int[]> directions = getNaturalDirections(dx, dy);
    for (int[] dir : directions) {
      int nx = x + dir[0];
      int ny = y + dir[1];
      if (!isObstacle(nx, ny)) {
        int jump = findJumpPoint(sommet, dir[0], dir[1]);
        if (jump != -1)
          indexVoisins.add(jump);
      }
    }

    // Ajout des voisins forcés
    List<int[]> forcedDirs = getForcedDirections(x, y, dx, dy);
    for (int[] f : forcedDirs) {
      int nx = x + f[0];
      int ny = y + f[1];
      if (!isObstacle(nx, ny)) {
        int idx = getIndex(new Point(nx, ny));
        if (!indexVoisins.contains(idx)) {
          indexVoisins.add(idx);
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

  private List<int[]> getForcedDirections(int x, int y, int dx, int dy) {
    List<int[]> forcedDirs = new ArrayList<>();

    // Cas diagonaux
    if (dx != 0 && dy != 0) {
      // Si des obstacles bloquent le passage direct, les coins deviennent forcés
      if (isObstacle(x - dx, y) && !isObstacle(x - dx, y + dy)) {
        forcedDirs.add(new int[] { -dx, dy });
      }
      if (isObstacle(x, y - dy) && !isObstacle(x + dx, y - dy)) {
        forcedDirs.add(new int[] { dx, -dy });
      }
    }

    // Cas horizontaux
    else if (dx != 0) {
      if (isObstacle(x, y + 1) && !isObstacle(x + dx, y + 1)) {
        forcedDirs.add(new int[] { dx, 1 });
      }
      if (isObstacle(x, y - 1) && !isObstacle(x + dx, y - 1)) {
        forcedDirs.add(new int[] { dx, -1 });
      }
    }

    // Cas verticaux
    else if (dy != 0) {
      if (isObstacle(x + 1, y) && !isObstacle(x + 1, y + dy)) {
        forcedDirs.add(new int[] { 1, dy });
      }
      if (isObstacle(x - 1, y) && !isObstacle(x - 1, y + dy)) {
        forcedDirs.add(new int[] { -1, dy });
      }
    }

    return forcedDirs;
  }

  private List<int[]> getNaturalDirections(int dx, int dy) {
    List<int[]> directions = new ArrayList<>();
    if (dx != 0 && dy != 0) {
      directions.add(new int[] { dx, dy });
      directions.add(new int[] { dx, 0 });
      directions.add(new int[] { 0, dy });
    } else if (dx != 0) {
      directions.add(new int[] { dx, 0 });
    } else if (dy != 0) {
      directions.add(new int[] { 0, dy });
    } else {
      // Départ -> toutes les directions
      int[][] allDirs = {
          { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
          { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
      };
      directions.addAll(Arrays.asList(allDirs));
    }
    return directions;
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
      return NO_DIRECTION; // Noeud de départ
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