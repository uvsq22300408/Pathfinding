package org.example.View.Grille2D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.example.algo.DijkstraAlgorithm;

public class GrillePathfinding extends javax.swing.JFrame {
    private static final int LIGNES = 20; // Nombre de lignes
    private static final int COLONNES = 20; // Nombre de colonnes
    private final javax.swing.JButton[][] boutonsGrille = new javax.swing.JButton[LIGNES][COLONNES]; // Boutons de la grille
    private final List<Point> pointsSelectionnes = new ArrayList<>(); // Points sélectionnés (départ/arrivée)

    public GrillePathfinding() {
        setTitle("Grille de Pathfinding");
        setSize(600, 400);
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Créer la grille
        javax.swing.JPanel panneauGrille = new javax.swing.JPanel(new GridLayout(LIGNES, COLONNES, 0, 0));
        for (int i = 0; i < LIGNES; i++) {
            for (int j = 0; j < COLONNES; j++) {
                javax.swing.JButton bouton = creerBoutonGrille(i, j);
                boutonsGrille[i][j] = bouton;
                panneauGrille.add(bouton);
            }
        }

        // Panneau des contrôles
        javax.swing.JPanel panneauControle = creerPanneauControle();

        // Ajouter les panneaux à la fenêtre
        add(panneauGrille, BorderLayout.CENTER);
        add(panneauControle, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Crée un bouton de la grille avec les événements de clic gauche et droit.
     */
    private JButton creerBoutonGrille(int ligne, int colonne) {
        JButton bouton = new JButton();
        bouton.setBackground(Color.WHITE);
        bouton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        bouton.setMargin(new Insets(0, 0, 0, 0));

        bouton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    gererClicGauche(bouton, ligne, colonne);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    gererClicDroit(bouton);
                }
            }
        });

        return bouton;
    }

    /**
     * Crée le panneau contenant les boutons de contrôle (exécuter algorithmes, réinitialiser).
     */
    private JPanel creerPanneauControle() {
        JPanel panneau = new JPanel(new GridLayout(5, 1, 5, 5));
        panneau.setPreferredSize(new Dimension(150, 0));

        ajouterBoutonControle(panneau, "Lancer A*", "algo.AStarAlgorithm");
        ajouterBoutonControle(panneau, "Lancer Dijkstra", "algo.DijkstraAlgorithm");

        JButton boutonReinitialiser = new JButton("Réinitialiser la grille");
        boutonReinitialiser.setPreferredSize(new Dimension(100, 40));
        boutonReinitialiser.addActionListener(e -> reinitialiserGrille());
        panneau.add(boutonReinitialiser);

        return panneau;
    }

    /**
     * Ajoute un bouton pour exécuter un algorithme spécifique.
     */
    private void ajouterBoutonControle(JPanel panneau, String label, String classeAlgorithme) {
        JButton bouton = new JButton(label);
        bouton.setPreferredSize(new Dimension(100, 40));
        bouton.addActionListener(e -> executerAlgorithme(classeAlgorithme));
        panneau.add(bouton);
    }

    /**
     * Gère le clic gauche sur un bouton de la grille (ajout/suppression de départ ou arrivée).
     */
    private void gererClicGauche(JButton bouton, int ligne, int colonne) {
        if (bouton.getBackground() == Color.WHITE) {
            if (pointsSelectionnes.size() % 2 == 0) {
                bouton.setBackground(Color.GREEN); // Point de départ
            } else {
                bouton.setBackground(Color.RED); // Point d'arrivée
            }
            pointsSelectionnes.add(new Point(ligne, colonne));
        } else if (bouton.getBackground() == Color.GREEN || bouton.getBackground() == Color.RED) {
            pointsSelectionnes.remove(new Point(ligne, colonne));
            bouton.setBackground(Color.WHITE);
        }
    }

    /**
     * Gère le clic droit sur un bouton de la grille (ajout/suppression d'obstacles).
     */
    private void gererClicDroit(JButton bouton) {
        if (bouton.getBackground() == Color.WHITE) {
            bouton.setBackground(Color.BLACK); // Ajout d'un obstacle
        } else if (bouton.getBackground() == Color.BLACK) {
            bouton.setBackground(Color.WHITE); // Suppression d'un obstacle
        }
    }

    /**
     * Exécute l'algorithme de pathfinding sélectionné.
     */
    private void executerAlgorithme(String classeAlgorithme) {
        if (pointsSelectionnes.size() % 2 != 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un nombre pair de points (départ et arrivée) !");
            return;
        }

        Set<Point> obstacles = new HashSet<>();
        for (int i = 0; i < LIGNES; i++) {
            for (int j = 0; j < COLONNES; j++) {
                if (boutonsGrille[i][j].getBackground() == Color.BLACK) {
                    obstacles.add(new Point(i, j));
                }
            }
        }

        if (classeAlgorithme == "algo.DijkstraAlgorithm") {
            DijkstraAlgorithm algo = new org.example.algo.DijkstraAlgorithm(pointsSelectionnes, obstacles, LIGNES, COLONNES);
            long startTime = System.nanoTime();
            ArrayList<Point> chemin = algo.calculChemin();
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            System.out.println("Execution time in nanoseconds: " + duration);

            afficheChemin(chemin);
        }

    }

    private void afficheChemin(ArrayList<Point> chemin) {
        for (Point p : chemin) {
            boutonsGrille[p.x][p.y].setBackground(Color.GRAY);
        }
    }

    /**
     * Réinitialise la grille (vide tous les points et obstacles).
     */
    private void reinitialiserGrille() {
        pointsSelectionnes.clear();
        for (int i = 0; i < LIGNES; i++) {
            for (int j = 0; j < COLONNES; j++) {
                boutonsGrille[i][j].setBackground(Color.WHITE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GrillePathfinding::new);
    }
}