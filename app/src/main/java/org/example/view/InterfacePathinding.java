package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

class InterfacePathindingPanel extends JPanel {
    private final ArrayList<Shape> shapes = new ArrayList<>();
    private final Random random = new Random();

    public void generateShapes() {
        shapes.clear();
        int numberOfShapes = random.nextInt(5) + 3; // Génère entre 3 et 7 formes
        
        for (int i = 0; i < numberOfShapes; i++) {
            int x = random.nextInt(getWidth() - 50);
            int y = random.nextInt(getHeight() - 50);
            int size = random.nextInt(50) + 20;
            boolean isCircle = random.nextBoolean();
            shapes.add(new Shape(x, y, size, isCircle));
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
            JFrame frame = new JFrame("Shape Generator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

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
