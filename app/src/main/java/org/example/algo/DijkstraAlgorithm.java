package org.example.algo;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.awt.*;

public class DijkstraAlgorithm implements Algorithme { 
    
    int colonnes;
    int lignes;
    Set<Point> obstacles;
    int indexDepart;
    int indexArrivee;
    Point pointDepart;
    Point pointArrivee;
    ArrayList<Integer> grille = new ArrayList<>();

    public DijkstraAlgorithm(List<Point> pointsSelectionnes, Set<Point> obstacles, int lignes, int colonnes) {
        this.obstacles = obstacles;
        this.colonnes = colonnes;
        this.lignes = lignes;
        this.pointDepart = pointsSelectionnes.get(0);
        this.pointArrivee = pointsSelectionnes.get(1);
        this.indexDepart = getIndex(pointsSelectionnes.get(0));
        this.indexArrivee = getIndex(pointsSelectionnes.get(1));
    }

    @Override
    public void setObstacles(Set<Point> obstacles) {
        this.obstacles = obstacles;
    }

    private ArrayList<Integer> getIndexVoisins(int sommet) {
        ArrayList<Integer> indexVoisins = new ArrayList<>();
        int[] coord = getCoord(sommet);

        for (int i = -1; i < 2; i++) {
            int xVoisin = coord[0] + i;

            if ((xVoisin >= 0) && (xVoisin <= lignes - 1)) {
                
                for (int j = -1; j < 2; j++) {
                    int yVoisin = coord[1] + j;

                    if ((yVoisin >= 0) && (yVoisin <= colonnes - 1)) {
                    
                        if ((coord[0] != xVoisin) || (coord[1] != yVoisin)) {
                            int[] coordVoisin = {xVoisin, yVoisin};
                            int indexVoisin = getIndex(coordVoisin);

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

    private int minDistIndex(ArrayList<Double> d, ArrayList<Boolean> P) {
        int indexMin = -1;
        double distMin = lignes*colonnes*10000;

        for (int i = 0; i < colonnes * lignes; i++) {
            if (!P.get(i)) {
                if (d.get(i) < distMin) {
                    distMin = d.get(i);
                    indexMin = i;
                }
            }
        }
        return indexMin;
    }

    private int[] getCoord(int index) {
        int[] coord = {index/colonnes, index % colonnes};
        return coord;
    }

    private Point getPoint(int index) {
        return new Point(index/colonnes, index % colonnes);
    }

    private int getIndex(Point p) {
        return ((p.x * colonnes) + p.y);
    }

    private int getIndex(int[] p) {
        return (p[0] * colonnes + p[1]);
    }

    private double getDist(int i1, int i2) {
        int[] p1 = getCoord(i1);
        int[] p2 = getCoord(i2);
        int dltX = p1[0] - p2[0];
        int dltY = p1[1] - p2[1];

        return Math.sqrt(dltX * dltX + dltY * dltY);
    }


    private void initialise() {
        for (int i = 0; i < lignes * colonnes; i++) {
            grille.add(0);
        }

        for (Point p : obstacles) {
            grille.set(getIndex(p), 3);
        }

        grille.set(indexDepart, 1);
        grille.set(indexArrivee, 2);
    }

    private ArrayList<Point> cheminPoints(ArrayList<Integer> pred) {
        ArrayList<Point> chemin = new ArrayList<>();
        Integer indexCourrant = indexArrivee;
        Point pointCourrant = pointArrivee;
        while (indexCourrant != indexDepart) {
            indexCourrant = pred.get(indexCourrant);
            pointCourrant = getPoint(indexCourrant);
            chemin.add(pointCourrant);
        }
        chemin.remove(chemin.size() - 1);
        return chemin;

    }

    @Override
    public ArrayList<Point> calculChemin() {
        initialise();

        ArrayList<Boolean> P = new ArrayList<>();
        ArrayList<Integer> pred = new ArrayList<>();
        ArrayList<Double> d = new ArrayList<>(); { for (int i = 0; i < lignes * colonnes; i++) 
                                                       { d.add((double) ((lignes*colonnes*10000)));
                                                         pred.add(null);
                                                         P.add(false);} }
        d.set(indexDepart, 0.0);

        while (!P.get(indexArrivee)) {

            int a = minDistIndex(d, P);
            if (a < 0) {
                return new ArrayList<>();
            }

            P.set(a, true);
            ArrayList<Integer> voisinsA = getIndexVoisins(a);

            for (int v : voisinsA) {
                Double distParA = d.get(a) + getDist(a, v);

                if (d.get(v) > distParA) {
                    d.set(v, distParA);
                    pred.set(v, a);
                }
            }
        }
       
        ArrayList<Point> chemin = cheminPoints(pred);
        System.out.println("Distance de (" + pointDepart.x + ", " + pointDepart.y + ") vers (" + pointArrivee.x + ", " + pointArrivee.y + ") : " + d.get(indexArrivee));
        return chemin;
    }
}
