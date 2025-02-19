package org.example.algo;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

public class NavMesh {
    private List<Triangle> triangles = new ArrayList<>();

    public NavMesh(List<Polygon> obstacles, int width, int height) {
        List<Point> points = new ArrayList<>();

        // Ajouter les sommets des obstacles
        for (Polygon obs : obstacles) {
            for (int i = 0; i < obs.npoints; i++) {
                points.add(new Point(obs.xpoints[i], obs.ypoints[i]));
            }
        }

        // Ajouter les coins de la carte
        points.add(new Point(0, 0));
        points.add(new Point(width, 0));
        points.add(new Point(0, height));
        points.add(new Point(width, height));

        // Générer les triangles
        generateTriangles(points, obstacles);
    }

    private void generateTriangles(List<Point> points, List<Polygon> obstacles) {
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                for (int k = j + 1; k < points.size(); k++) {
                    Triangle triangle = new Triangle(points.get(i), points.get(j), points.get(k));
                    if (!triangle.intersectsObstacle(obstacles)) {
                        triangles.add(triangle);
                    }
                }
            }
        }
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

    public List<Point> findPath(Point start, Point end) {
        List<Point> path = new ArrayList<>();
        path.add(start);
        path.add(end);
        return path; // Implémentation simple pour l'instant
    }
}
