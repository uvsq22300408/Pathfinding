package org.example.Algo;

import java.awt.*;
import java.util.*;
import java.util.List;

public class AStarAlgorithm implements Algorithme {

    int colonnes;
    int lignes;
    List<Point> obstacles;
    int indexDepart;
    int indexArrivee;
    Point pointDepart;
    Point pointArrivee;
    ArrayList<Integer> grille = new ArrayList<>();

    public AStarAlgorithm(List<Point> pointsSelectionnes, List<Point> obstacles, int lignes, int colonnes) {
        this.obstacles = obstacles;
        this.colonnes = colonnes;
        this.lignes = lignes;
        this.pointDepart = pointsSelectionnes.get(0);
        this.pointArrivee = pointsSelectionnes.get(1);
        this.indexDepart = getIndex(pointsSelectionnes.get(0));
        this.indexArrivee = getIndex(pointsSelectionnes.get(1));
    }

    protected ArrayList<Integer> getIndexVoisins(int sommet) {
        ArrayList<Integer> indexVoisins = new ArrayList<>();
        int[] coord = getCoord(sommet);

        for (int i = -1; i < 2; i++) {
            int xVoisin = coord[0] + i;

            if ((xVoisin >= 0) && (xVoisin < lignes)) {
                for (int j = -1; j < 2; j++) {
                    int yVoisin = coord[1] + j;

                    if ((yVoisin >= 0) && (yVoisin < colonnes)) {
                        if ((coord[0] != xVoisin) || (coord[1] != yVoisin)) {
                            int indexVoisin = getIndex(new int[] { xVoisin, yVoisin });
                            if (grille.get(indexVoisin) != 3) {
                                indexVoisins.add(indexVoisin);
                            }
                        }
                    }
                }
            }
        }
        return indexVoisins;
    }

    protected int[] getCoord(int index) {
        return new int[] { index / colonnes, index % colonnes };
    }

    protected Point getPoint(int index) {
        return new Point(index / colonnes, index % colonnes);
    }

    protected int getIndex(Point p) {
        return (p.x * colonnes) + p.y;
    }

    protected int getIndex(int[] p) {
        return (p[0] * colonnes + p[1]);
    }

    protected double getDist(int i1, int i2) {
        int[] p1 = getCoord(i1);
        int[] p2 = getCoord(i2);
        int dltX = p1[0] - p2[0];
        int dltY = p1[1] - p2[1];

        return Math.sqrt(dltX * dltX + dltY * dltY);
    }

    protected double heuristique(int index) {
        int[] coord = getCoord(index);
        int dx = coord[0] - pointArrivee.x;
        int dy = coord[1] - pointArrivee.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    protected void initialise() {
        for (int i = 0; i < lignes * colonnes; i++) {
            grille.add(0);
        }

        for (Point p : obstacles) {
            grille.set(getIndex(p), 3);
        }

        grille.set(indexDepart, 1);
        grille.set(indexArrivee, 2);
    }

    protected ArrayList<Point> cheminPoints(Map<Integer, Integer> pred) {
        ArrayList<Point> chemin = new ArrayList<>();
        Integer indexCourant = indexArrivee;

        while (indexCourant != indexDepart) {
            indexCourant = pred.get(indexCourant);
            if (indexCourant == null)
                return new ArrayList<>();
            chemin.add(getPoint(indexCourant));
        }
        chemin.remove(chemin.size() - 1);
        Collections.reverse(chemin);
        return chemin;
    }

    static class Node {
        int index;
        double fScore;

        Node(int index, double fScore) {
            this.index = index;
            this.fScore = fScore;
        }
    }

    @Override
    public ArrayList<Point> calculChemin() {
        initialise();

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Map<Integer, Double> gScore = new HashMap<>();
        Map<Integer, Integer> pred = new HashMap<>();
        Set<Integer> closedSet = new HashSet<>();

        gScore.put(indexDepart, 0.0);
        openSet.add(new Node(indexDepart, heuristique(indexDepart)));

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();
            int current = currentNode.index;

            if (current == indexArrivee) {
                ArrayList<Point> chemin = cheminPoints(pred);
                System.out.println("Distance de (" + pointDepart.x + ", " + pointDepart.y + ") vers (" + pointArrivee.x
                        + ", " + pointArrivee.y + ") : " + gScore.get(indexArrivee));
                return chemin;
            }

            closedSet.add(current);

            for (int voisin : getIndexVoisins(current)) {
                if (closedSet.contains(voisin))
                    continue;

                double tentativeGScore = gScore.getOrDefault(current, Double.MAX_VALUE) + getDist(current, voisin);

                if (tentativeGScore < gScore.getOrDefault(voisin, Double.MAX_VALUE)) {
                    pred.put(voisin, current);
                    gScore.put(voisin, tentativeGScore);

                    openSet.removeIf(node -> node.index == voisin);
                    openSet.add(new Node(voisin, tentativeGScore + heuristique(voisin)));
                }
            }
        }

        return new ArrayList<>();
    }
}
