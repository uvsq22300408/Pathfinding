package org.example.Triangulation;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class DelaunayTriangulationApp extends JFrame {
    private static final int MARGIN = 20;
    private JTextField pointsField;
    private JButton generateButton, clearButton, addObstacleButton, pathfindingButton, precisePathfindingButton;
    private DrawPanel drawPanel;
    private List<Point> points = new ArrayList<>();
    private List<Triangle> triangles = new ArrayList<>();
    private List<Obstacle> obstacles = new ArrayList<>();
    private Point startPoint = null;
    private Point endPoint = null;

    public DelaunayTriangulationApp() {
        setTitle("Triangulation de Delaunay avec Obstacles & Pathfinding");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de contrôle avec contour noir
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(new LineBorder(Color.BLACK, 2));
        controlPanel.setLayout(new FlowLayout());

        controlPanel.add(new JLabel("Nombre de points :"));
        pointsField = new JTextField(5);
        controlPanel.add(pointsField);

        generateButton = new JButton("Lancer Maillage");
        clearButton = new JButton("Clear");
        addObstacleButton = new JButton("Ajouter Obstacle");
        pathfindingButton = new JButton("Pathfinding");
        precisePathfindingButton = new JButton("Pathfinding Précis");

        controlPanel.add(generateButton);
        controlPanel.add(addObstacleButton);
        controlPanel.add(pathfindingButton);
        controlPanel.add(precisePathfindingButton);
        controlPanel.add(clearButton);
        add(controlPanel, BorderLayout.NORTH);

        // Panneau de dessin
        drawPanel = new DrawPanel();
        drawPanel.setBorder(new LineBorder(Color.BLACK, 2));
        drawPanel.setPreferredSize(new Dimension(800, 600));
        add(drawPanel, BorderLayout.CENTER);

        // Gestion des événements
        generateButton.addActionListener(e -> generateTriangulation());
        clearButton.addActionListener(e -> clearTriangulation());
        addObstacleButton.addActionListener(e -> addObstacle());
        pathfindingButton.addActionListener(e -> runPathfinding());
        precisePathfindingButton.addActionListener(e -> runPrecisePathfinding());

        // Ajout du MouseListener pour gérer les clics droits
        drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    handleRightClick(e.getPoint());
                }
            }
        });
    }

    private void generateTriangulation() {
        int numPoints;
        try {
            numPoints = Integer.parseInt(pointsField.getText());
            if (numPoints < 0) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre positif.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        points.clear();
        triangles.clear();
        int maxX = drawPanel.getWidth() - MARGIN;
        int maxY = drawPanel.getHeight() - MARGIN;

        // Ajouter les 4 points fixes
        points.add(new Point(MARGIN, MARGIN));
        points.add(new Point(maxX, MARGIN));
        points.add(new Point(MARGIN, maxY));
        points.add(new Point(maxX, maxY));

        // Ajouter les points des obstacles
        for (Obstacle obs : obstacles) {
            points.addAll(obs.getVertices());
        }

        // Ajouter des points aléatoires hors des obstacles
        Random rand = new Random();
        for (int i = 0; i < numPoints; i++) {
            Point newPoint;
            do {
                newPoint = new Point(rand.nextInt(maxX - MARGIN) + MARGIN, rand.nextInt(maxY - MARGIN) + MARGIN);
            } while (isInsideObstacle(newPoint));
            points.add(newPoint);
        }

        // Générer la triangulation en respectant les obstacles
        DelaunayTriangulation.compute(points, triangles, maxX, maxY);
        // Supprimer les triangles qui sont dans un obstacle
        triangles.removeIf(this::isTriangleInsideObstacle);

        drawPanel.repaint();
    }

    private boolean isTriangleInsideObstacle(Triangle t) {
        for (Obstacle obs : obstacles) {
            if (obs.contains(t.getCentroid())) {
                return true;
            }
        }
        return false;
    }

    private void addObstacle() {
        int maxX = drawPanel.getWidth() - MARGIN;
        int maxY = drawPanel.getHeight() - MARGIN;
        obstacles.add(new Obstacle(maxX, maxY));
        drawPanel.repaint();
    }

    private void clearTriangulation() {
        points.clear();
        triangles.clear();
        obstacles.clear();
        startPoint = null;
        endPoint = null;
        drawPanel.repaint();
    }

    private void handleRightClick(Point p) {
        if (isInsideObstacle(p)) {
            return; // Ne pas placer le point s'il est à l'intérieur d'un obstacle
        }

        if (startPoint == null) {
            startPoint = p;
        } else if (endPoint == null) {
            endPoint = p;
        } else {
            // Déplacer les points : L'ancien startPoint est supprimé, endPoint devient
            // startPoint, et le nouveau clic devient endPoint
            startPoint = endPoint;
            endPoint = p;
        }

        drawPanel.repaint();
    }

    private boolean isInsideObstacle(Point p) {
        for (Obstacle obs : obstacles) {
            if (obs.contains(p))
                return true;
        }
        return false;
    }

    public void runPathfinding() {
        List<Point> path = Pathfinding.findPath(startPoint, endPoint, triangles);
        if (path != null) {
            drawPanel.setPath(path);
        }
    }

    public void runPrecisePathfinding() {
        List<Point> path = Pathfinding_P.findPath(startPoint, endPoint, triangles, obstacles);
        if (path != null) {
            drawPanel.setPath(path);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DelaunayTriangulationApp().setVisible(true));
    }

    class DrawPanel extends JPanel {
        private List<Point> path; // Ajouter une liste de points

        public void setPath(List<Point> path) {
            this.path = path;
            repaint(); // Redessine le panel avec le nouveau chemin
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int maxX = getWidth() - MARGIN;
            int maxY = getHeight() - MARGIN;

            // Dessiner les obstacles
            g2d.setColor(Color.BLACK);
            g2d.drawRect(MARGIN, MARGIN, maxX - MARGIN, maxY - MARGIN);
            for (Obstacle obs : obstacles) {
                obs.draw(g2d);
            }

            // Dessiner le maillage
            g2d.setColor(Color.BLUE);
            for (Triangle t : triangles) {
                int[] xPoints = { t.a.x, t.b.x, t.c.x };
                int[] yPoints = { t.a.y, t.b.y, t.c.y };
                g2d.drawPolygon(xPoints, yPoints, 3);
            }

            // Dessiner le point de départ en vert
            if (startPoint != null) {
                g2d.setColor(Color.GREEN);
                g2d.fillOval(startPoint.x - 4, startPoint.y - 4, 8, 8);
            }

            // Dessiner le point d'arrivée en bleu
            if (endPoint != null) {
                g2d.setColor(Color.CYAN);
                g2d.fillOval(endPoint.x - 4, endPoint.y - 4, 8, 8);
            }

            // === 👉 Dessiner le chemin trouvé ===
            if (path != null && !path.isEmpty()) {
                g2d.setColor(Color.RED);
                for (int i = 0; i < path.size() - 1; i++) {
                    Point p1 = path.get(i);
                    Point p2 = path.get(i + 1);
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }
    }
}
