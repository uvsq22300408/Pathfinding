// SansGrillePathfinding.java
package org.example.view;

import org.example.algo.NavMesh;
import org.example.algo.Triangle;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SansGrillePathfinding extends JFrame {
    private DrawingPanel drawingPanel;
    private Point startPoint = null;
    private Point endPoint = null;
    private NavMesh navMesh;
    private List<Point> path = new ArrayList<>();

    public SansGrillePathfinding() {
        setTitle("Pathfinding Sans Grille");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        drawingPanel = new DrawingPanel();
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                placePoint(e.getPoint());
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        JButton generateButton = new JButton("Générer Obstacles");
        JButton resetButton = new JButton("Réinitialiser Interface");
        JButton algorithmButton = new JButton("Lancer Algorithme");

        generateButton.addActionListener(e -> generateObstacles());
        resetButton.addActionListener(e -> resetInterface());
        algorithmButton.addActionListener(e -> runAlgorithm());

        controlPanel.add(generateButton);
        controlPanel.add(resetButton);
        controlPanel.add(algorithmButton);

        add(drawingPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void generateObstacles() {
        Obstacle.generateRandomObstacles(drawingPanel.getWidth(), drawingPanel.getHeight(), 5);
        startPoint = null;
        endPoint = null;
        path.clear();
        initializeNavMesh();
        drawingPanel.repaint();
    }

    private void resetInterface() {
        Obstacle.getObstacles().clear();
        startPoint = null;
        endPoint = null;
        path.clear();
        initializeNavMesh();
        drawingPanel.repaint();
    }

    private void placePoint(Point p) {
        if (startPoint == null) {
            startPoint = p;
        } else if (endPoint == null) {
            endPoint = p;
        }
        drawingPanel.repaint();
    }

    private void runAlgorithm() {
        if (startPoint == null || endPoint == null) {
            System.out.println("Placez les points de départ et d'arrivée.");
            return;
        }

        path = navMesh.findPath(startPoint, endPoint);
        drawingPanel.repaint();
    }

    private void initializeNavMesh() {
        navMesh = new NavMesh(Obstacle.getObstacles(), drawingPanel.getWidth(), drawingPanel.getHeight());
    }

    private class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Obstacle.drawObstacles(g);

            g.setColor(Color.LIGHT_GRAY);
            if (navMesh != null) {
                for (Triangle t : navMesh.getTriangles()) {
                    int[] xPoints = {(int) t.a.x, (int) t.b.x, (int) t.c.x};
                    int[] yPoints = {(int) t.a.y, (int) t.b.y, (int) t.c.y};
                    g.drawPolygon(xPoints, yPoints, 3);
                }
            }

            if (startPoint != null) {
                g.setColor(Color.GREEN);
                g.fillOval(startPoint.x - 5, startPoint.y - 5, 10, 10);
            }

            if (endPoint != null) {
                g.setColor(Color.RED);
                g.fillOval(endPoint.x - 5, endPoint.y - 5, 10, 10);
            }

            if (!path.isEmpty()) {
                g.setColor(Color.BLUE);
                for (int i = 0; i < path.size() - 1; i++) {
                    Point p1 = path.get(i);
                    Point p2 = path.get(i + 1);
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }
    }
}
