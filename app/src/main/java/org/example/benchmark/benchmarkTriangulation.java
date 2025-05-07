package org.example.benchmark;

import org.example.Triangulation.Triangle;
import org.example.world.World;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.time.Duration;
import java.time.Instant;

import org.example.Triangulation.DelaunayTriangulation;
import org.example.Triangulation.Obstacle;
import org.example.Triangulation.Pathfinding;

public class benchmarkTriangulation {
    public static List<Obstacle> obstacles;
    public static List<Triangle> triangles;
    public static List<Point> path;
    public static int pathLength;

    // Convertit world et exécute l'algorithme de triangulation dessus.
    // Calcule le chemin et renvoie le temps mis
    public static long benchmark(World world, int numPoints) {
        List<Point> points = new ArrayList<>();
        List<Triangle> triangles = new ArrayList<>();
        obstacles = new ArrayList<>();
        // Conversion des world Obstacles en Triangulation Obstacle (octogone)
        for(World.Obstacle o : world.obstacles) {
            Obstacle octogone = Obstacle.fixedObstacle(Math.round(o.x), Math.round(o.y), 
                Math.round(o.radius));
            obstacles.add(octogone);
        }

        Instant before = Instant.now();
        // Ajouter les 4 points fixes
        points.add(new Point(0, 0));
        points.add(new Point(world.width, 0));
        points.add(new Point(0, world.height));
        points.add(new Point(world.width, world.height));
        
        // Ajoute un point devant Start pour le mettre dans un triangle
        points.add(new Point(Math.round(world.start.x) + 1, Math.round(world.start.y)));

        for(Obstacle o : obstacles) {
            points.addAll(o.getVertices());
        }
        
        // Ajouter des points aléatoires hors des obstacles
        Random rand = new Random();
        for (int i = 0; i < numPoints; i++) {
            Point newPoint;
            do {
                newPoint = new Point(rand.nextInt(world.width), rand.nextInt(world.height));
            } while (isInsideObstacle(newPoint));
            points.add(newPoint);
        }
        DelaunayTriangulation.compute(points, triangles, world.width, world.height);
        // Supprimer les triangles qui sont dans un obstacle
        triangles.removeIf(benchmarkTriangulation::isTriangleInsideObstacle);
        Point start = new Point(Math.round(world.start.x), Math.round(world.start.y));
        Point end = new Point(Math.round(world.destination.x), Math.round(world.destination.y));
        List<Point> path = Pathfinding.findPathNoDisplay(start, end, triangles);

        int pathLength = 0;
        if (path == null) {
            pathLength = -1;
        } else {
            for (int nodeindex = 0; nodeindex < path.size() - 1; nodeindex++) {
                pathLength += path.get(nodeindex + 1).distance(path.get(nodeindex));
            }
        }
        benchmarkTriangulation.path = path;
        benchmarkTriangulation.pathLength = pathLength;
        benchmarkTriangulation.triangles = triangles;
        Instant after = Instant.now();
        return Duration.between(before, after).toMillis();
    }

    private static boolean isInsideObstacle(Point p) {
        for (Obstacle obs : obstacles) {
            if (obs.contains(p))
                return true;
        }
        return false;
    }

    private static boolean isTriangleInsideObstacle(Triangle t) {
        for (Obstacle obs : obstacles) {
            if (obs.contains(t.getCentroid())) {
                return true;
            }
        }
        return false;
    }
}
