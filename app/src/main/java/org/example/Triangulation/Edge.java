package org.example.Triangulation;
import java.awt.Point;

public class Edge {
    Point p1, p2;

    public Edge(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Edge)) return false;
        Edge other = (Edge) obj;
        return (p1.equals(other.p1) && p2.equals(other.p2)) || (p1.equals(other.p2) && p2.equals(other.p1));
    }
}
