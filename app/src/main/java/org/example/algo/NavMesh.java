package org.example.Algo;

import java.awt.Point;
import java.util.*;

public class NavMesh {
    private List<Triangle> triangles;

    public NavMesh(List<Triangle> triangles) {
        this.triangles = triangles;
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

class Triangle {
    Point[] vertices;
    Point center;
    List<Triangle> neighbors;
    double cost;

    public Triangle(Point a, Point b, Point c) {
        this.vertices = new Point[]{a, b, c};
        this.center = new Point((a.x + b.x + c.x) / 3, (a.y + b.y + c.y) / 3);
        this.neighbors = new ArrayList<>();
    }

    public boolean contains(Point p) {
        return isPointInTriangle(p, vertices[0], vertices[1], vertices[2]);
    }

    public void addNeighbor(Triangle neighbor) {
        if (!neighbors.contains(neighbor)) {
            neighbors.add(neighbor);
        }
    }

    private boolean isPointInTriangle(Point p, Point a, Point b, Point c) {
        double area = triangleArea(a, b, c);
        double area1 = triangleArea(p, b, c);
        double area2 = triangleArea(a, p, c);
        double area3 = triangleArea(a, b, p);
        return Math.abs(area - (area1 + area2 + area3)) < 1e-6;
    }

    private double triangleArea(Point a, Point b, Point c) {
        return Math.abs(a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y)) / 2.0;
    }
}
