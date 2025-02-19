package org.example;

import org.example.view.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // new GrillePathfinding();
            new SansGrillePathfinding();
        });
    }
}
