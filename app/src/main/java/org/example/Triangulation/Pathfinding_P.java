package org.example.Triangulation;

import java.awt.Point;
import java.util.*;

import javax.swing.JOptionPane;

public class Pathfinding_P {

    private static class Node implements Comparable<Node> {
        Triangle triangle;
        Node parent;
        double g;
        double h;
        double f;

        Node(Triangle triangle, Node parent, double g, double h) {
            this.triangle = triangle;
            this.parent = parent;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.f, other.f);
        }
    }

    // Trouver le chemin avec A*
    public static List<Point> findPath(Point start, Point end, List<Triangle> triangles, List<Obstacle> obstacles) {
        Triangle startTriangle = findContainingTriangle(start, triangles);
        Triangle endTriangle = findContainingTriangle(end, triangles);

        if (startTriangle == null || endTriangle == null) {
            JOptionPane.showMessageDialog(null, "Point de départ ou d'arrivée invalide.");
            return null;
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Triangle> closedSet = new HashSet<>();
        openSet.add(new Node(startTriangle, null, 0, startTriangle.heuristic(endTriangle)));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.triangle == endTriangle) {
                List<Point> path = reconstructPath(current);
                path.add(0, start);
                path.add(end);

                // Étape 2 : Insérer les Steiner Points si nécessaire
                path = insertSteinerPoints(path, obstacles);

                // Étape 3 : Simplification finale
                path = simplifyPath(path, obstacles);

                return path;
            }

            closedSet.add(current.triangle);

            for (Triangle neighbor : findNeighbors(current.triangle, triangles)) {
                if (closedSet.contains(neighbor)) continue;

                double tentativeG = current.g + current.triangle.distanceTo(neighbor);

                openSet.add(new Node(neighbor, current, tentativeG, neighbor.heuristic(endTriangle)));
            }
        }

        return null;
    }

    // Insérer des Steiner Points entre deux points si la ligne est bloquée par un obstacle
    private static List<Point> insertSteinerPoints(List<Point> path, List<Obstacle> obstacles) {
        List<Point> optimizedPath = new ArrayList<>();
        optimizedPath.add(path.get(0));

        for (int i = 1; i < path.size(); i++) {
            Point p1 = optimizedPath.get(optimizedPath.size() - 1);
            Point p2 = path.get(i);

            if (!canConnectDirectly(p1, p2, obstacles)) {
                // Si la ligne directe est bloquée → on insère un Steiner Point
                Point steiner = generateSteinerPoint(p1, p2, obstacles);
                optimizedPath.add(steiner);
            }

            optimizedPath.add(p2);
        }

        return optimizedPath;
    }

    // Créer un Steiner Point entre deux points si un obstacle bloque le passage
    private static Point generateSteinerPoint(Point p1, Point p2, List<Obstacle> obstacles) {
        int midX = (p1.x + p2.x) / 2;
        int midY = (p1.y + p2.y) / 2;
        Point steiner = new Point(midX, midY);

        // Ajuster la position du Steiner Point en fonction des obstacles
        for (Obstacle obs : obstacles) {
            if (obs.contains(steiner)) {
                // Si le point est à l'intérieur d'un obstacle, on le pousse à l'extérieur
                steiner = adjustPoint(steiner, obs);
            }
        }

        return steiner;
    }

    // Ajuster la position du point Steiner s'il tombe dans un obstacle
    private static Point adjustPoint(Point steiner, Obstacle obs) {
        int offset = 5; // On pousse légèrement le point vers l'extérieur
        int x = steiner.x;
        int y = steiner.y;

        while (obs.contains(new Point(x, y))) {
            x += offset;
            y += offset;
        }

        return new Point(x, y);
    }

    // Vérification si une ligne directe est possible (sans croiser un obstacle)
    private static boolean canConnectDirectly(Point p1, Point p2, List<Obstacle> obstacles) {
        for (Obstacle obs : obstacles) {
            if (obs.intersects(p1, p2)) {
                return false;
            }
        }
        return true;
    }

    // Reconstruction du chemin brut à partir des triangles
    private static List<Point> reconstructPath(Node node) {
        List<Point> path = new ArrayList<>();
        while (node != null) {
            path.add(node.triangle.getCentroid());
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    // Simplification du chemin après insertion des Steiner Points
    private static List<Point> simplifyPath(List<Point> path, List<Obstacle> obstacles) {
        boolean hasChanged = true;

        while (hasChanged) {
            hasChanged = false;
            for (int i = 0; i < path.size() - 2; i++) {
                Point p1 = path.get(i);
                Point p3 = path.get(i + 2);

                if (canConnectDirectly(p1, p3, obstacles)) {
                    // Si le segment direct est possible → on supprime le point intermédiaire
                    path.remove(i + 1);
                    hasChanged = true;
                    break;
                }
            }
        }

        return path;
    }

    // Trouver le triangle contenant un point
    private static Triangle findContainingTriangle(Point p, List<Triangle> triangles) {
        for (Triangle t : triangles) {
            if (isPointInTriangle(p, t.a, t.b, t.c)) {
                return t;
            }
        }
        return null;
    }

    private static List<Triangle> findNeighbors(Triangle triangle, List<Triangle> triangles) {
        List<Triangle> neighbors = new ArrayList<>();
        for (Triangle t : triangles) {
            if (triangle != t && triangle.sharesEdge(t)) {
                neighbors.add(t);
            }
        }
        return neighbors;
    }

    private static boolean isPointInTriangle(Point p, Point a, Point b, Point c) {
        double area = triangleArea(a, b, c);
        double area1 = triangleArea(p, b, c);
        double area2 = triangleArea(a, p, c);
        double area3 = triangleArea(a, b, p);
        return Math.abs(area - (area1 + area2 + area3)) < 1e-5;
    }

    private static double triangleArea(Point a, Point b, Point c) {
        return Math.abs((a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y)) / 2.0);
    }

    // Trouver le chemin avec A*
    public static List<Point> findPathNoDisplay(Point start, Point end, List<Triangle> triangles, List<Obstacle> obstacles) {
        Triangle startTriangle = findContainingTriangle(start, triangles);
        Triangle endTriangle = findContainingTriangle(end, triangles);

        if (startTriangle == null) {
            // On prend le triangle le plus proche de start et on s'y déplace
            double min = -1;
            for (Triangle t : triangles) {
                Point centroid = t.getCentroid();
                double d = centroid.distance(start);
                if (min == -1 || d < min) {
                    min = d;
                    startTriangle = t;
                }
            }
        }
        if (endTriangle == null) {
            // On prend le triangle le plus proche de end et on s'y déplace
            double min = -1;
            for (Triangle t : triangles) {
                Point centroid = t.getCentroid();
                double d = centroid.distance(end);
                if (min == -1 || d < min) {
                    min = d;
                    endTriangle = t;
                }
            }
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Triangle> closedSet = new HashSet<>();
        openSet.add(new Node(startTriangle, null, 0, startTriangle.heuristic(endTriangle)));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.triangle == endTriangle) {
                List<Point> path = reconstructPath(current);
                path.add(0, start);
                path.add(end);

                // Étape 2 : Insérer les Steiner Points si nécessaire
                path = insertSteinerPoints(path, obstacles);

                // Étape 3 : Simplification finale
                path = simplifyPath(path, obstacles);

                return path;
            }

            closedSet.add(current.triangle);

            for (Triangle neighbor : findNeighbors(current.triangle, triangles)) {
                if (closedSet.contains(neighbor)) continue;

                double tentativeG = current.g + current.triangle.distanceTo(neighbor);

                openSet.add(new Node(neighbor, current, tentativeG, neighbor.heuristic(endTriangle)));
            }
        }

        return null;
    }
}
