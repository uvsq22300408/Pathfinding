package org.example;

import org.example.View.Grille2D.*;
import org.example.View.SansGrille2D.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // new GrillePathfinding();
            new SansGrillePathfinding();
        });
    }
}
