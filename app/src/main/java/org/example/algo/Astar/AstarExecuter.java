package org.example.algo.Astar;

import java.awt.Point;
import java.util.List;

import org.example.algo.AlgorithmExecuter;

public class AstarExecuter extends AlgorithmExecuter {

    public AstarExecuter(List<Point> pointsSelectionnes, List<Point> obstacles, int LIGNES, int COLONNES) {
        this.executable =  "./src/main/java/org/example/algo/Dijkstra/AstarAlgo";
    }

}


//  pointsSelectionnes, obstacles, LIGNES, COLONNES