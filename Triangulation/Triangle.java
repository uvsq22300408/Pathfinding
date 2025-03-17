import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Triangle {
    Point a, b, c;

    public Triangle(Point a, Point b, Point c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public List<Edge> getEdges() {
        return Arrays.asList(new Edge(a, b), new Edge(b, c), new Edge(c, a));
    }

    public boolean hasEdge(Edge e) {
        return getEdges().contains(e);
    }

    public boolean isPointInCircumcircle(Point p) {
        double ax = a.x - p.x, ay = a.y - p.y;
        double bx = b.x - p.x, by = b.y - p.y;
        double cx = c.x - p.x, cy = c.y - p.y;
        double det = (ax * ax + ay * ay) * (bx * cy - by * cx) -
                (bx * bx + by * by) * (ax * cy - ay * cx) +
                (cx * cx + cy * cy) * (ax * by - ay * bx);
        return det > 0;
    }

    public boolean hasVertex(Point p) {
        return a.equals(p) || b.equals(p) || c.equals(p);
    }

    public Point getCentroid() {
        int x = (a.x + b.x + c.x) / 3;
        int y = (a.y + b.y + c.y) / 3;
        return new Point(x, y);
    }

    public double distanceTo(Triangle other) {
        Point centroid1 = this.getCentroid();
        Point centroid2 = other.getCentroid();
        return centroid1.distance(centroid2);
    }

    public List<Triangle> getNeighbors(List<Triangle> triangles) {
        List<Triangle> neighbors = new ArrayList<>();
        for (Triangle t : triangles) {
            if (t != this && sharesEdge(t)) {
                neighbors.add(t);
            }
        }
        return neighbors;
    }

    boolean sharesEdge(Triangle other) {
        int sharedEdges = 0;
        for (Edge e1 : this.getEdges()) {
            for (Edge e2 : other.getEdges()) {
                if (e1.equals(e2)) {
                    sharedEdges++;
                }
            }
        }
        return sharedEdges == 1;
    }

    public double heuristic(Triangle target) {
        return this.distanceTo(target);
    }

    public boolean contains(Point p) {
        double area = triangleArea(a, b, c);
        double area1 = triangleArea(p, b, c);
        double area2 = triangleArea(a, p, c);
        double area3 = triangleArea(a, b, p);

        // Si la somme des sous-aires est égale à l'aire totale → Le point est à
        // l'intérieur
        return Math.abs(area - (area1 + area2 + area3)) < 1e-5;
    }

    private double triangleArea(Point p1, Point p2, Point p3) {
        return Math.abs((p1.x * (p2.y - p3.y) +
                p2.x * (p3.y - p1.y) +
                p3.x * (p1.y - p2.y)) / 2.0);
    }

}
