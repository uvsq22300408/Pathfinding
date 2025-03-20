package org.example.world.examples;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.example.algo.JPS;
import org.example.world.World;
import java.awt.Point;

public class JPSGrid extends JPS {
    public static double JPS(World world) {
        JPSGrid ag = init(world);
        JPSGrid.cheminFinal = ag.calculChemin();
        return ag.distance * world.tailleReg;
    }
    public static JPSGrid init(World world) {
        Point start = new Point(( ((int)world.start.x) / world.tailleReg),
             ((int)world.start.y) / world.tailleReg);
        Point dest = new Point(( ((int)world.destination.x) / world.tailleReg),
        ((int)world.destination.y) / world.tailleReg);
        List<Point> pointsSelectionnes = List.of(start, dest);
        List<Point> obstacles = new ArrayList<>();
        for (int regionId = 0; regionId < world.passThrough.length; regionId++) {
            if (world.passThrough[regionId] == World.InnerWorld.OBSTACLE) {
                obstacles.add(new Point(regionId / world.info.heightInRegion, 
                    regionId % world.info.heightInRegion));
            }
        }
        int lignes = world.width / world.tailleReg;
        int colonnes = world.height / world.tailleReg;
        int indexDepart = start.x * colonnes + start.y;
        int indexArrivee = dest.x * colonnes + dest.y;
        Point pointDepart = start;
        Point pointArrivee = dest;
        return new JPSGrid(pointsSelectionnes, obstacles, lignes, colonnes,
            indexDepart, indexArrivee, pointDepart, pointArrivee);
    }

    public JPSGrid(List<Point> _points, List<Point> _obstacles, int _lignes, int _colonnes,
    int indexDepart, int indexArrivee, Point pointDepart, Point pointArrivee) {
        super(_points, _obstacles, _lignes, _colonnes);
        this.indexDepart = indexDepart;
        this.indexArrivee = indexArrivee;
        this.pointDepart = pointDepart;
        this.pointArrivee = pointArrivee;
    }

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
                ArrayList<Point> chemin = cheminPoints(pred);
                this.distance = gScore.get(indexArrivee);
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
        System.out.println("Pas de chemin trouvé");
        return new ArrayList<>();
    }

    static class Node {
        int index;
        double fScore;

        Node(int index, double fScore) {
            this.index = index;
            this.fScore = fScore;
        }
    }

    private int indexDepart;
    private int indexArrivee;
    private Point pointDepart;
    private Point pointArrivee;
    public double distance;
    public static List<Point> cheminFinal;
}
