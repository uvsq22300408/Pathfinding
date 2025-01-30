package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

class InterfacePathfindingPanel extends JPanel {
    private final ArrayList<Forme> formes = new ArrayList<>();
    private final Random random = new Random();
    private Point startPoint = null;
    private Point endPoint = null;

    public InterfacePathfindingPanel() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Point clickedPoint = e.getPoint();
                    
                    // Vérification si on reclique sur un point déjà existant
                    if (startPoint != null && startPoint.distance(clickedPoint) < 10) {
                        startPoint = null;
                        System.out.println("Point de départ supprimé.");
                    } else if (endPoint != null && endPoint.distance(clickedPoint) < 10) {
                        endPoint = null;
                        System.out.println("Point d'arrivée supprimé.");
                    } else if (startPoint == null && !isOnForme(clickedPoint)) {
                        startPoint = clickedPoint;
                        System.out.println("Point de départ défini à : (" + startPoint.x + ", " + startPoint.y + ")");
                    } else if (endPoint == null) {
                        endPoint = clickedPoint;
                        System.out.println("Point d'arrivée défini à : (" + endPoint.x + ", " + endPoint.y + ")");
                    } else {
                        System.out.println("Impossible de définir un nouveau point !");
                    }
                    repaint();
                }
            }
        });
    }

    public void generateFormes() {
        formes.clear();
        int numberOfFormes = random.nextInt(5) + 3; // Génère entre 3 et 7 formes

        for (int i = 0; i < numberOfFormes; i++) {
            int x, y, size;
            boolean isCircle;
            Forme nouvelleForme;
            
            do {
                x = random.nextInt(Math.max(1, getWidth() - 50));
                y = random.nextInt(Math.max(1, getHeight() - 50));
                size = random.nextInt(50) + 20;
                isCircle = random.nextBoolean();
                nouvelleForme = new Forme(x, y, size, isCircle);
            } while (nouvelleForme.contains(startPoint) || nouvelleForme.contains(endPoint)); // Évite superposition
            
            formes.add(nouvelleForme);
            System.out.println("Forme " + (isCircle ? "Cercle" : "Carré") + " à (" + x + ", " + y + ")");
        }
        repaint();
    }

    private boolean isOnForme(Point p) {
        for (Forme forme : formes) {
            if (forme.contains(p)) {
                System.out.println("Impossible de placer le point de départ sur une forme existante !");
                return true;
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.BLACK);
        for (Forme forme : formes) {
            if (forme.isCircle) {
                g.fillOval(forme.x, forme.y, forme.size, forme.size);
            } else {
                g.fillRect(forme.x, forme.y, forme.size, forme.size);
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
    }

    private static class Forme {
        int x, y, size;
        boolean isCircle;

        Forme(int x, int y, int size, boolean isCircle) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.isCircle = isCircle;
        }

        boolean contains(Point p) {
            if (p == null) return false;
            return (p.x >= x && p.x <= x + size && p.y >= y && p.y <= y + size);
        }
    }
}

class TriangularGrid {
    private final ArrayList<Triangle> triangles = new ArrayList<>();
    private final InterfacePathfindingPanel panel;

    public TriangularGrid(InterfacePathfindingPanel panel) {
        this.panel = panel;
    }

    public void generateGrid() {
        System.out.println("Génération d'une grille triangulaire...");
        // Implémentation de la triangulation en prenant en compte les obstacles
        // Ajout de l'algorithme A* pour le pathfinding
    }

    private static class Triangle {
        Point p1, p2, p3;
        
        Triangle(Point p1, Point p2, Point p3) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
        }
    }
}

public class InterfacePathfinding {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pathfinding Interface");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);
            frame.setMaximumSize(new Dimension(800, 600));

            JPanel mainPanel = new JPanel(new BorderLayout());
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            
            InterfacePathfindingPanel formePanel = new InterfacePathfindingPanel();
            formePanel.setBackground(Color.WHITE);
            
            JButton generateButton = new JButton("Générer des formes");
            generateButton.addActionListener(e -> formePanel.generateFormes());
            
            JButton hexGridButton = new JButton("Hexagonal Grid");
            hexGridButton.addActionListener(e -> System.out.println("Génération d'une grille hexagonale..."));
            
            JButton triGridButton = new JButton("Triangular Grid");
            triGridButton.addActionListener(e -> new TriangularGrid(formePanel).generateGrid());
            
            buttonPanel.add(generateButton);
            buttonPanel.add(hexGridButton);
            buttonPanel.add(triGridButton);
            buttonPanel.add(Box.createVerticalGlue());
            
            mainPanel.add(buttonPanel, BorderLayout.WEST);
            mainPanel.add(formePanel, BorderLayout.CENTER);
            
            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }
}