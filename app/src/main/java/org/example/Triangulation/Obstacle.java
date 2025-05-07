package org.example.Triangulation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Obstacle {
    private List<Point> vertices;
    private static final int MIN_SIZE = 30; // Taille minimale de l'octogone
    private static final int MAX_SIZE = 80; // Taille maximale de l'octogone

    public Obstacle(int maxX, int maxY) {
        Random rand = new Random();
        int size = rand.nextInt(MAX_SIZE - MIN_SIZE) + MIN_SIZE;
        int x = rand.nextInt(maxX - size - 20) + 10; // Position aléatoire
        int y = rand.nextInt(maxY - size - 20) + 10;

        this.vertices = generateOctagon(x, y, size);
    }

    public Obstacle(List<Point> vertices) {
        this.vertices = vertices;
    }

    // Create an obstacle with a specified position
    public static Obstacle fixedObstacle(int x, int y, int size) {
        List<Point> vertices = generateOctagon(x, y, size);
        return new Obstacle(vertices);
    }

    private static List<Point> generateOctagon(int x, int y, int size) {
        List<Point> points = new ArrayList<>();
        int offset = size / 3; // Permet de donner une forme plus équilibrée

        points.add(new Point(x + offset, y));
        points.add(new Point(x + size - offset, y));
        points.add(new Point(x + size, y + offset));
        points.add(new Point(x + size, y + size - offset));
        points.add(new Point(x + size - offset, y + size));
        points.add(new Point(x + offset, y + size));
        points.add(new Point(x, y + size - offset));
        points.add(new Point(x, y + offset));

        return points;
    }

    public List<Point> getVertices() {
        return vertices;
    }

    public boolean contains(Point p) {
        Polygon polygon = new Polygon();
        for (Point vertex : vertices) {
            polygon.addPoint(vertex.x, vertex.y);
        }
        return polygon.contains(p);
    }

    public void draw(Graphics2D g2d) {
        int[] xPoints = vertices.stream().mapToInt(v -> v.x).toArray();
        int[] yPoints = vertices.stream().mapToInt(v -> v.y).toArray();

        g2d.fillPolygon(xPoints, yPoints, vertices.size());
    }

    public boolean intersects(Point p1, Point p2) {
        Polygon polygon = new Polygon();
        for (Point vertex : vertices) {
            polygon.addPoint(vertex.x, vertex.y);
        }
        return polygon.intersects(
                Math.min(p1.x, p2.x),
                Math.min(p1.y, p2.y),
                Math.abs(p2.x - p1.x),
                Math.abs(p2.y - p1.y));
    }

}