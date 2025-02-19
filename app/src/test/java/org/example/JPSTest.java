package org.example;

import java.util.ArrayList;
import java.util.List;
import java.awt.Point;

import org.example.algo.JPS;

public class JPSTest {
    public static void main(String[] args) {
        List<Point> pointsSelectionnes = new ArrayList<>();
        pointsSelectionnes.add(new Point(0, 0)); // Point de départ
        pointsSelectionnes.add(new Point(3, 4)); // Point d'arrivée

        List<Point> obstacles = new ArrayList<>();
        obstacles.add(new Point(1, 2));
        obstacles.add(new Point(2, 2));
        obstacles.add(new Point(3, 2));

        int lignes = 5;
        int colonnes = 5;

        JPS jps = new JPS(pointsSelectionnes, obstacles, lignes, colonnes);
        ArrayList<Point> chemin = jps.calculChemin();

        if (chemin.isEmpty()) {
            System.out.println("Aucun chemin trouvé.");
        } else {
            System.out.println("Chemin trouvé : " + chemin);
        }
    }
}
