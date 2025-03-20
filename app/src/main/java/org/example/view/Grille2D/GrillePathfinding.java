package org.example.view.Grille2D;

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
    private static final int LIGNES = 30; // Nombre de lignes
    private static final int COLONNES = 30; // Nombre de colonnes
    private final CasePanel[][] cases = new CasePanel[LIGNES][COLONNES]; // Boutons de la grille
    private final List<Point> pointsSelectionnes = new ArrayList<>(); // Points sélectionnés (départ/arrivée)
    private final List<Point> obstacles = new ArrayList<>(); // Obstacles


    public GrillePathfinding() {
        setTitle("Grille 2D Pathfinding");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Créer la grille
        JPanel panneauGrille = new JPanel(new GridLayout(LIGNES, COLONNES, 0, 0));
        for (int i = 0; i < LIGNES; i++) {
            for (int j = 0; j < COLONNES; j++) {
                CasePanel casePanel = new CasePanel(i, j);
                cases[i][j] = casePanel;
                panneauGrille.add(casePanel);
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

    private void ajouterCercleObstacle(int centreX, int centreY, int rayon) {
        for (int i = -rayon; i <= rayon; i++) {
            for (int j = -rayon; j <= rayon; j++) {
                int x = centreX + i;
                int y = centreY + j;
                if (x >= 0 && x < LIGNES && y >= 0 && y < COLONNES && i * i + j * j <= rayon * rayon) {
                    cases[x][y].setBackground(Color.BLACK);
                    obstacles.add(new Point(x, y));
                }
            }
        }
    }


    private class CasePanel extends JPanel {
        private final Color[] colors = {Color.WHITE, Color.GREEN, Color.RED, Color.BLACK};
        private int colorIndex = 0;
        private final int ligne, colonne;
    
        public CasePanel(int ligne, int colonne) {
            this.ligne = ligne;
            this.colonne = colonne;
            setBackground(colors[colorIndex]);
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        gererClicGauche();
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        gererClicDroit(e.isShiftDown());
                    }
                }
            });
        }
    
        private void gererClicGauche() {
            if (getBackground() == Color.WHITE) {
                if (pointsSelectionnes.size() % 2 == 0) {
                    setBackground(Color.GREEN);
                } else {
                    setBackground(Color.RED);
                }
                pointsSelectionnes.add(new Point(ligne, colonne));
            } else if (getBackground() == Color.GREEN || getBackground() == Color.RED) {
                pointsSelectionnes.remove(new Point(ligne, colonne));
                setBackground(Color.WHITE);
            }
        }
    
        private void gererClicDroit(boolean isShiftPressed) {
            if (isShiftPressed) {
                ajouterCercleObstacle(ligne, colonne, 10);
            } else {
                if (getBackground() == Color.WHITE) {
                    setBackground(Color.BLACK);
                    obstacles.add(new Point(ligne, colonne));
                } else if (getBackground() == Color.BLACK) {
                    setBackground(Color.WHITE);
                    obstacles.remove(new Point(ligne, colonne));
                }
            }
        }
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
     * Exécute l'algorithme de pathfinding sélectionné.
     */
    private void executerAlgorithme(Algorithme algo) {

        // Retirer le chemin précédent
        for (int i = 0; i < LIGNES; i++) {
            for (int j = 0; j < COLONNES; j++) {
                if (cases[i][j].getBackground() == Color.GRAY) {
                    cases[i][j].setBackground(Color.WHITE);
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
            cases[p.x][p.y].setBackground(Color.GRAY);
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
                cases[i][j].setBackground(Color.WHITE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GrillePathfinding::new);
    }
}