package org.example.Triangulation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class DelaunayTriangulation {
    public static void compute(List<Point> points, List<Triangle> triangles, int width, int height) {
        // Super-triangle couvrant toute la zone
        Point p1 = new Point(-width, -height);
        Point p2 = new Point(2 * width, -height);
        Point p3 = new Point(width / 2, 2 * height);
        Triangle superTriangle = new Triangle(p1, p2, p3);
        triangles.add(superTriangle);

        // Ajout des points un par un
        for (Point p : points) {
            List<Triangle> badTriangles = new ArrayList<>();
            for (Triangle t : triangles) {
                if (t.isPointInCircumcircle(p)) {
                    badTriangles.add(t);
                }
            }

            List<Edge> polygon = new ArrayList<>();
            for (Triangle t : badTriangles) {
                for (Edge e : t.getEdges()) {
                    boolean shared = false;
                    for (Triangle other : badTriangles) {
                        if (other != t && other.hasEdge(e)) {
                            shared = true;
                            break;
                        }
                    }
                    if (!shared) {
                        polygon.add(e);
                    }
                }
            }

            triangles.removeAll(badTriangles);
            for (Edge e : polygon) {
                triangles.add(new Triangle(e.p1, e.p2, p));
            }
        }

        triangles.removeIf(t -> t.hasVertex(p1) || t.hasVertex(p2) || t.hasVertex(p3));
    }
}
