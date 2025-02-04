package org.example.View.SansGrille2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;

public class Obstacle {
    private int x, y, size;
    private ShapeType type;
    private static final Random random = new Random();
    private static final List<Obstacle> obstacles = new ArrayList<>();

    public enum ShapeType {
        CARRE, HEXAGONE, DECAGONE
    }

    public Obstacle(int x, int y, int size, ShapeType type) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.type = type;
    }

    public static void generateRandomObstacles(int maxWidth, int maxHeight, int count) {
        obstacles.clear();
        for (int i = 0; i < count; i++) {
            int x = random.nextInt(maxWidth - 50);
            int y = random.nextInt(maxHeight - 50);
            int size = random.nextInt(50) + 20; // Taille entre 20 et 70 pixels
            ShapeType type = ShapeType.values()[random.nextInt(ShapeType.values().length)];
            obstacles.add(new Obstacle(x, y, size, type));
        }
    }

    public static void drawObstacles(Graphics g) {
        for (Obstacle obstacle : obstacles) {
            obstacle.draw(g);
        }
    }

    private void draw(Graphics g) {
        switch (type) {
            case CARRE:
                g.fillRect(x, y, size, size);
                break;
            case HEXAGONE:
                drawPolygon(g, 6);
                break;
            case DECAGONE:
                drawPolygon(g, 10);
                break;
        }
    }

    private void drawPolygon(Graphics g, int sides) {
        int[] xPoints = new int[sides];
        int[] yPoints = new int[sides];
        double angleStep = 2 * Math.PI / sides;
        for (int i = 0; i < sides; i++) {
            xPoints[i] = (int) (x + size * Math.cos(i * angleStep));
            yPoints[i] = (int) (y + size * Math.sin(i * angleStep));
        }
        g.fillPolygon(xPoints, yPoints, sides);
    }

    public static List<Obstacle> getObstacles() {
        return obstacles;
    }
}
