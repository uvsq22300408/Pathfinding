package org.example.view;

import javax.swing.*;

import org.example.algo.AStarAlgorithm;
import org.example.algo.Algorithme;
import org.example.algo.DijkstraAlgorithm;
import org.example.algo.JPS;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GrillePathfinding extends JFrame {
    private static final int LIGNES = 20; // Nombre de lignes
    private static final int COLONNES = 20; // Nombre de colonnes
    private final JButton[][] boutonsGrille = new JButton[LIGNES][COLONNES]; // Boutons de la grille
    private final List<Point> pointsSelectionnes = new ArrayList<>(); // Points sélectionnés (départ/arrivée)
    private final List<Point> obstacles = new ArrayList<>(); // Obstacles

    public GrillePathfinding() {
        setTitle("Grille de Pathfinding");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Créer la grille
        JPanel panneauGrille = new JPanel(new GridLayout(LIGNES, COLONNES, 0, 0));
        for (int i = 0; i < LIGNES; i++) {
            for (int j = 0; j < COLONNES; j++) {
                JButton bouton = creerBoutonGrille(i, j);
                boutonsGrille[i][j] = bouton;
                panneauGrille.add(bouton);
            }
        }

        // Panneau des contrôles
        JPanel panneauControle = creerPanneauControle();

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
                    gererClicDroit(bouton, ligne, colonne);
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

        JButton boutonDijkstra = new JButton("Lancer Dijkstra");
        JButton boutonAstar = new JButton("Lancer A*");
        JButton boutonJPS = new JButton("Lancer JPS");

        boutonDijkstra.addActionListener(e -> executerAlgorithme(new DijkstraAlgorithm(pointsSelectionnes, obstacles, LIGNES, COLONNES)));
        boutonAstar.addActionListener(e -> executerAlgorithme(new AStarAlgorithm(pointsSelectionnes, obstacles, LIGNES, COLONNES)));
        boutonJPS.addActionListener(e -> executerAlgorithme(new JPS(pointsSelectionnes, obstacles, LIGNES, COLONNES)));

        boutonAstar.setPreferredSize(new Dimension(100, 40));
        boutonDijkstra.setPreferredSize(new Dimension(100, 40));
        boutonJPS.setPreferredSize(new Dimension(100, 40));

        panneau.add(boutonDijkstra);
        panneau.add(boutonAstar);
        panneau.add(boutonJPS);

        JButton boutonReinitialiser = new JButton("Réinitialiser la grille");
        boutonReinitialiser.setPreferredSize(new Dimension(100, 40));
        boutonReinitialiser.addActionListener(e -> reinitialiserGrille());
        panneau.add(boutonReinitialiser);

        return panneau;
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
    private void gererClicDroit(JButton bouton, int ligne, int colonne) {
        if (bouton.getBackground() == Color.WHITE) {
            bouton.setBackground(Color.BLACK); // Ajout d'un obstacle
            obstacles.add(new Point(ligne, colonne));
        } else if (bouton.getBackground() == Color.BLACK) {
            bouton.setBackground(Color.WHITE); // Suppression d'un obstacle
            obstacles.remove(new Point(ligne, colonne));
        }
    }

    /**
     * Exécute l'algorithme de pathfinding sélectionné.
     */
    private void executerAlgorithme(Algorithme algo) {

        // Retirer le chemin précédent
        for (int i = 0; i < LIGNES; i++) {
            for (int j = 0; j < COLONNES; j++) {
                if (boutonsGrille[i][j].getBackground() == Color.GRAY) {
                    boutonsGrille[i][j].setBackground(Color.WHITE);
                }
            }
        }

        if (pointsSelectionnes.size() % 2 != 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un nombre pair de points (départ et arrivée) !");
            return;
        }
        long startTime = System.nanoTime();
        ArrayList<Point> chemin = algo.calculChemin();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("Execution time in nanoseconds: " + duration);

        afficheChemin(chemin);
    }


    /**
     * Affiche le chemin donné.
     * @param chemin
     */
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
        obstacles.clear();
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