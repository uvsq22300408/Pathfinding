package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

class InterfacePathindingPanel extends JPanel {
    private final ArrayList<Shape> shapes = new ArrayList<>();
    private final Random random = new Random();
    private Point startPoint = null;
    private Point endPoint = null;

    public InterfacePathindingPanel() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (startPoint == null) {
                        startPoint = e.getPoint();
                        System.out.println("Point de départ défini à : (" + startPoint.x + ", " + startPoint.y + ")");
                    } else if (endPoint == null) {
                        endPoint = e.getPoint();
                        System.out.println("Point d'arrivée défini à : (" + endPoint.x + ", " + endPoint.y + ")");
                    }
                    repaint();
                }
            }
        });
    }

    public void generateShapes() {
        shapes.clear();
        int numberOfShapes = random.nextInt(5) + 3; // Génère entre 3 et 7 formes
        
        for (int i = 0; i < numberOfShapes; i++) {
            int x = random.nextInt(getWidth() - 50);
            int y = random.nextInt(getHeight() - 50);
            int size = random.nextInt(50) + 20;
            boolean isCircle = random.nextBoolean();
            shapes.add(new Shape(x, y, size, isCircle));
            System.out.println("Shape " + (isCircle ? "Circle" : "Square") + " at (" + x + ", " + y + ")");
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Shape shape : shapes) {
            if (shape.isCircle) {
                g.fillOval(shape.x, shape.y, shape.size, shape.size);
            } else {
                g.fillRect(shape.x, shape.y, shape.size, shape.size);
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

    private static class Shape {
        int x, y, size;
        boolean isCircle;

        Shape(int x, int y, int size, boolean isCircle) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.isCircle = isCircle;
        }
    }
}

public class InterfacePathinding {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pathfinding Interface");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setMaximumSize(new Dimension(800, 600));

            JPanel mainPanel = new JPanel(new BorderLayout());
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            
            InterfacePathindingPanel shapePanel = new InterfacePathindingPanel();
            shapePanel.setBackground(Color.WHITE);
            
            JButton generateButton = new JButton("Générer des formes");
            generateButton.addActionListener(e -> shapePanel.generateShapes());
            
            buttonPanel.add(generateButton);
            buttonPanel.add(Box.createVerticalGlue());
            
            mainPanel.add(buttonPanel, BorderLayout.WEST);
            mainPanel.add(shapePanel, BorderLayout.CENTER);
            
            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }
}
