package org.example.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Obstacle {
    private static final List<Polygon> obstacles = new ArrayList<>();

    public static void generateRandomObstacles(int width, int height, int count) {
        obstacles.clear();
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            int x = rand.nextInt(width - 50);
            int y = rand.nextInt(height - 50);
            int size = 50;
            Polygon poly = new Polygon(
                new int[]{x, x + size, x + size, x},
                new int[]{y, y, y + size, y + size},
                4
            );
            obstacles.add(poly);
        }
    }

    public static List<Polygon> getObstacles() {
        return obstacles;
    }

    public static void drawObstacles(Graphics g) {
        g.setColor(Color.BLACK);
        for (Polygon poly : obstacles) {
            g.fillPolygon(poly);
        }
    }
}
