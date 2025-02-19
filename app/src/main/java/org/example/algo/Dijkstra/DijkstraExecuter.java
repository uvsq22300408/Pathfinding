package org.example.algo.Dijkstra;

import java.awt.Point;
import java.util.List;

import org.example.algo.AlgorithmExecuter;

public class DijkstraExecuter extends AlgorithmExecuter {



    public DijkstraExecuter(List<Point> pointsSelectionnes, List<Point> obstacles, int LIGNES, int COLONNES) {
        this.pointsSelectionnes = pointsSelectionnes;
        this.obstacles = obstacles;
        this.LIGNES = LIGNES;
        this.COLONNES = COLONNES;
        this.executable =  "./src/main/java/org/example/algo/Dijkstra/DijkstraAlgo";
    }
}
