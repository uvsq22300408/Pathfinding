package org.example.algo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class TriangulationMesh {
    private List<Triangle> triangles;

    public TriangulationMesh(List<Point> points) {
        triangles = new ArrayList<>();
        generateTriangulation(points);
    }

    private void generateTriangulation(List<Point> points) {
        if (points.size() < 3) return;
        for (int i = 0; i < points.size() - 2; i++) {
            triangles.add(new Triangle(points.get(i), points.get(i + 1), points.get(i + 2)));
        }
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }
}
