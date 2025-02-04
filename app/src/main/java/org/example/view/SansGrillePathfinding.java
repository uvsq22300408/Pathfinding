package org.example.view;

import org.example.Algo.NavMesh;
import org.example.Algo.Triangle;
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

        // Initialisation du panneau de dessin
        drawingPanel = new DrawingPanel();
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                placePoint(e.getPoint());
            }
        });

        // Panneau de contrôle
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        JButton generateButton = new JButton("Générer Formes");
        JButton resetButton = new JButton("Réinitialiser Interface");
        JButton algorithmButton = new JButton("Lancer Algorithme X");

        generateButton.addActionListener(e -> generateShapes());
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

        initializeNavMesh();
    }

    private void generateShapes() {
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
        List<Triangle> triangles = new ArrayList<>();
        triangles.add(new Triangle(new Point(100, 100), new Point(200, 50), new Point(150, 200)));
        navMesh = new NavMesh(triangles);
    }

    private class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Obstacle.drawObstacles(g);

            // Dessiner les triangles
            if (navMesh != null) {
                g.setColor(Color.LIGHT_GRAY);
                for (Triangle t : navMesh.getTriangles()) {
                    int[] xPoints = {t.vertices[0].x, t.vertices[1].x, t.vertices[2].x};
                    int[] yPoints = {t.vertices[0].y, t.vertices[1].y, t.vertices[2].y};
                    g.drawPolygon(xPoints, yPoints, 3);
                }
            }

            // Dessiner le point de départ (vert)
            if (startPoint != null) {
                g.setColor(Color.GREEN);
                g.fillOval(startPoint.x - 5, startPoint.y - 5, 10, 10);
            }

            // Dessiner le point d’arrivée (rouge)
            if (endPoint != null) {
                g.setColor(Color.RED);
                g.fillOval(endPoint.x - 5, endPoint.y - 5, 10, 10);
            }

            // Dessiner le chemin trouvé
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
