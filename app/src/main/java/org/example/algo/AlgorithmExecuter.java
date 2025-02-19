package org.example.algo;

import java.util.ArrayList;
import java.util.List;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public abstract class AlgorithmExecuter {
    protected String executable ;
    protected List<Point> pointsSelectionnes;
    protected List<Point> obstacles;
    protected int LIGNES;
    protected int COLONNES;

    public ArrayList<Point> executeAlgo() {
        ArrayList<Point> path = new ArrayList<>();
    
        try {
            // Création et démarrage du processus
            Point depart = pointsSelectionnes.get(0);
            Point arrivee = pointsSelectionnes.get(1);

            List<String> command = new ArrayList<>();
            command.add(executable);
            command.add(String.valueOf(LIGNES));
            command.add(String.valueOf(COLONNES));
            command.add(String.valueOf(depart.x));
            command.add(String.valueOf(depart.y));
            command.add(String.valueOf(arrivee.x));
            command.add(String.valueOf(arrivee.y));
            for (Point p : obstacles) {
                command.add(String.valueOf(p.getX()));
                command.add(String.valueOf(p.getY()));
            }
            System.out.println(command);
            ProcessBuilder pb = new ProcessBuilder(command);
            System.out.println("Démarrage de l'algo");
            Process process = pb.start();
    
            // Utilisation de try-with-resources après le démarrage du process
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        // Séparer les coordonnées et ajouter le point à la liste
                        String[] points = line.split(" ");
                        int x = Integer.parseInt(points[0].split(",")[0]);
                        int y = Integer.parseInt(points[0].split(",")[1]);
                        path.add(new Point(x, y));
                    } catch (NumberFormatException e) {
                        System.err.println("Erreur de formatage des coordonnées : " + line);
                    }
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Le processus s'est terminé avec un code d'erreur : " + exitCode);
                return new ArrayList<>();
            }
    
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
            return new ArrayList<>();
        }
    
        return path;
    }
    
    
}
