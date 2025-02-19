package org.example.algo;

import java.awt.Point;
import java.awt.Polygon;
import java.util.List;

public class Triangle {
    public Point a, b, c;

    public Triangle(Point a, Point b, Point c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public boolean contains(Point p) {
        Polygon poly = new Polygon(
            new int[]{a.x, b.x, c.x}, 
            new int[]{a.y, b.y, c.y}, 
            3
        );
        return poly.contains(p);
    }

    public boolean intersectsObstacle(List<Polygon> obstacles) {
        for (Polygon obs : obstacles) {
            if (obs.intersects(a.x, a.y, b.x - a.x, b.y - a.y) ||
                obs.intersects(b.x, b.y, c.x - b.x, c.y - b.y) ||
                obs.intersects(c.x, c.y, a.x - c.x, a.y - c.y)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Triangle[(" + a.x + "," + a.y + "), (" + b.x + "," + b.y + "), (" + c.x + "," + c.y + ")]";
    }
}
