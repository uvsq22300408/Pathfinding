import java.awt.Point;
import java.util.*;

import javax.swing.JOptionPane;

public class Pathfinding {

    public static class Node implements Comparable<Node> {
        Triangle triangle;
        Node parent;
        double g; // coût parcouru depuis le départ
        @SuppressWarnings("unused")
        double h; // heuristique
        double f; // coût total = g + h

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

    public static List<Point> findPath(Point start, Point end, List<Triangle> triangles) {
        if (start == null || end == null) {
            JOptionPane.showMessageDialog(null, "Veuillez sélectionner un point de départ et un point d'arrivée.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Triangle startTriangle = findContainingTriangle(start, triangles);
        Triangle endTriangle = findContainingTriangle(end, triangles);

        if (startTriangle == null || endTriangle == null) {
            JOptionPane.showMessageDialog(null, "Point de départ ou d'arrivée en dehors du maillage.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Triangle> closedSet = new HashSet<>();
        openSet.add(new Node(startTriangle, null, 0, startTriangle.heuristic(endTriangle)));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.triangle == endTriangle) {
                // ✅ Étape 1 : Reconstruire le chemin triangulaire
                List<Point> path = reconstructPath(current);
                // ✅ Étape 2 : Connecter le chemin au startPoint et endPoint
                path.add(0, start); // Start → Premier triangle
                path.add(end); // Dernier triangle → End
                return path;
            }

            closedSet.add(current.triangle);

            for (Triangle neighbor : findNeighbors(current.triangle, triangles)) {
                if (closedSet.contains(neighbor))
                    continue;

                double tentativeG = current.g + current.triangle.distanceTo(neighbor);

                boolean isBetterPath = true;
                for (Node node : openSet) {
                    if (node.triangle == neighbor && tentativeG >= node.g) {
                        isBetterPath = false;
                        break;
                    }
                }

                if (isBetterPath) {
                    openSet.add(new Node(neighbor, current, tentativeG, neighbor.heuristic(endTriangle)));
                }
            }
        }

        JOptionPane.showMessageDialog(null, "Aucun chemin trouvé !", "Erreur", JOptionPane.ERROR_MESSAGE);
        return null;
    }

    private static List<Point> reconstructPath(Node node) {
        List<Point> path = new ArrayList<>();
        while (node != null) {
            path.add(node.triangle.getCentroid());
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    // ======== Trouver le triangle contenant le point ========
    private static Triangle findContainingTriangle(Point p, List<Triangle> triangles) {
        for (Triangle t : triangles) {
            if (isPointInTriangle(p, t.a, t.b, t.c)) {
                return t;
            }
        }
        return null;
    }

    // ======== Trouver les triangles voisins ========
    private static List<Triangle> findNeighbors(Triangle triangle, List<Triangle> triangles) {
        List<Triangle> neighbors = new ArrayList<>();
        for (Triangle t : triangles) {
            if (triangle != t) {
                int sharedEdges = 0;
                for (Edge e : triangle.getEdges()) {
                    if (t.hasEdge(e)) {
                        sharedEdges++;
                    }
                }
                if (sharedEdges == 1) {
                    neighbors.add(t);
                }
            }
        }
        return neighbors;
    }

    // ======== Vérification d'un point dans un triangle ========
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
}
