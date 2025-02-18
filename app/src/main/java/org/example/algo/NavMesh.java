package org.example.Algo;

import java.awt.Point;
import java.util.*;

public class NavMesh {
    private List<Triangle> triangles;

    public NavMesh(List<Triangle> triangles) {
        this.triangles = triangles;
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

    public Triangle findContainingTriangle(Point p) {
        for (Triangle t : triangles) {
            if (t.contains(p)) {
                return t;
            }
        }
        return null;
    }

    public List<Point> findPath(Point start, Point end) {
        Triangle startTriangle = findContainingTriangle(start);
        Triangle endTriangle = findContainingTriangle(end);

        if (startTriangle == null || endTriangle == null) {
            System.out.println("Point hors de la navigation mesh");
            return Collections.emptyList();
        }

        PriorityQueue<Triangle> openSet = new PriorityQueue<>(Comparator.comparingDouble(t -> t.cost));
        Map<Triangle, Triangle> cameFrom = new HashMap<>();
        Map<Triangle, Double> gScore = new HashMap<>();
        
        for (Triangle t : triangles) {
            gScore.put(t, Double.MAX_VALUE);
        }
        
        gScore.put(startTriangle, 0.0);
        openSet.add(startTriangle);

        while (!openSet.isEmpty()) {
            Triangle current = openSet.poll();
            if (current == endTriangle) {
                return reconstructPath(cameFrom, endTriangle, start, end);
            }

            for (Triangle neighbor : current.neighbors) {
                double tentativeGScore = gScore.get(current) + current.center.distance(neighbor.center);
                if (tentativeGScore < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    neighbor.cost = tentativeGScore + heuristic(neighbor.center, end);
                    openSet.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<Point> reconstructPath(Map<Triangle, Triangle> cameFrom, Triangle current, Point start, Point end) {
        List<Point> path = new ArrayList<>();
        path.add(end);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current.center);
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }

    private double heuristic(Point a, Point b) {
        return a.distance(b);
    }
}
