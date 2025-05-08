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

        cheminFinal.addFirst(pointDepart);
        cheminFinal.addLast(pointArrivee);
        // Calcul de distance
        for (int pointIndex = 0; pointIndex < cheminFinal.size() - 1; pointIndex++) {
            ag.distance += cheminFinal.get(pointIndex).distance(cheminFinal.get(pointIndex + 1));
        }
        return ag.distance * world.tailleReg;
    }
    public static JPSGrid init(World world) {
        Point start = new Point(( ((int)world.start.x)  / world.tailleReg),
             ((int)world.start.y) / world.tailleReg);
        Point dest = new Point(( ((int)world.destination.x) / world.tailleReg),
        ((int)world.destination.y) / world.tailleReg);
        pointDepart = start;
        pointArrivee = dest;
        JPSGrid.points = new ArrayList<>();
        points.addAll(List.of(start, dest));
        obstacles = new ArrayList<>();
        for (int regionId = 0; regionId < world.passThrough.length; regionId++) {
            if (world.passThrough[regionId] == World.InnerWorld.OBSTACLE) {
                obstacles.add(new Point(regionId / world.info.heightInRegion, 
                    regionId % world.info.heightInRegion));
            }
        }
        lignes = world.width / world.tailleReg;
        colonnes = world.height / world.tailleReg;
        int indexDepart = start.x * colonnes + start.y;
        int indexArrivee = dest.x * colonnes + dest.y;
        Point pointDepart = start;
        Point pointArrivee = dest;
        return new JPSGrid(JPSGrid.points, obstacles, lignes, colonnes,
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

      JPS jps = new JPS(points, obstacles, lignes, colonnes);
      System.out.println("exec JPS.calculChemin()");
      cheminFinal = jps.calculChemin();
      System.out.println("fin de JPS");
      return cheminFinal;
    }

    static class Node {
        int index;
        double fScore;

        Node(int index, double fScore) {
            this.index = index;
            this.fScore = fScore;
        }
    }

    private static int lignes;
    private static int colonnes;
    private int indexDepart;
    private int indexArrivee;
    private static Point pointDepart;
    private static Point pointArrivee;
    public double distance;
    public static ArrayList<Point> cheminFinal;
    public static List<Point> obstacles;
    public static ArrayList<Point> points;
}
