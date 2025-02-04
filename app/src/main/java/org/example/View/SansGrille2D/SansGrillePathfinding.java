package org.example.View.SansGrille2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SansGrillePathfinding extends JFrame {
    private final DrawingPanel drawingPanel;
    private Point startPoint = null;
    private Point endPoint = null;

    public SansGrillePathfinding() {
        setTitle("Pathfinding Sans Grille");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panneau de dessin
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
    }

    /**
     * Génére des obstacles aléatoires et réinitialise les points de départ et d'arrivée
     */
    private void generateShapes() {
        Obstacle.generateRandomObstacles(drawingPanel.getWidth(), drawingPanel.getHeight(), 5);
        startPoint = null;
        endPoint = null;
        drawingPanel.repaint();
    }

    /**
     * Réinitialise l'interface en supprimant les obstacles et les points
     */
    private void resetInterface() {
        Obstacle.getObstacles().clear();
        startPoint = null;
        endPoint = null;
        drawingPanel.repaint();
    }

    /**
     * Place les points de départ et d’arrivée en fonction du clic
     */
    private void placePoint(Point p) {
        if (startPoint == null) {
            startPoint = p;
            System.out.println("Départ placé : " + p);
        } else if (endPoint == null) {
            endPoint = p;
            System.out.println("Arrivée placée : " + p);
        }
        drawingPanel.repaint();
    }

    /**
     * Exécution de l'algorithme de pathfinding (non implémenté)
     */
    private void runAlgorithm() {
        if (startPoint == null || endPoint == null) {
            System.out.println("Veuillez placer les points de départ et d'arrivée.");
            return;
        }
        System.out.println("Lancement du pathfinding de " + startPoint + " à " + endPoint);
        // Implémentation de l'algorithme à venir
    }

    private class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Obstacle.drawObstacles(g);

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
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SansGrillePathfinding::new);
    }
}
