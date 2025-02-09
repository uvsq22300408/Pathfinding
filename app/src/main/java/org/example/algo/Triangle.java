package org.example.algo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Triangle {
    public Point[] vertices;
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
