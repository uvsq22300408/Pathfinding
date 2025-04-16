package org.example.benchmark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import org.example.world.World;
import org.example.world.SaveWorld;
import org.example.world.LoadWorld;
import org.example.world.RandomWorld;
import org.example.world.examples.Astar;
import org.example.world.examples.AstarGrid;
import org.example.world.examples.Dijkstra;
import org.example.world.examples.JPSGrid;
import org.example.world.examples.Quadtree;

public class Benchmark {
    public static void benchmarkall() {
        try {
            Path file = Path.of("benchmark-results.csv");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile(),
            false));
        File dossier = new File(SaveWorld.SAVE_LOCATION);
        File[] graphes = dossier.listFiles();
        int nbGraphes = graphes.length;
        // Ajouter les autres algorithmes

        // Dijkstra : ,DijkstraLength,DijkstraTimeMs
        writer.write("nomGraphe,AstarLength,AstarTimeMs,QuadtreeLength,QuadtreeTimeMs,JPSLength,JPSTimeMs\n");
        for (int gx = 0; gx < nbGraphes; gx++) {
            System.out.println("opening: " + graphes[gx].getName());
            World world = LoadWorld.loadWorld(graphes[gx].getName());
            writer.write(graphes[gx].getName() + ",");
            // =============== Astar
            System.out.println("running astar");
            long timeElapsed = AstarGrid.benchmark(world);
            double longueurAst = AstarGrid.distanceForBenchmark;
            if (longueurAst <= 0) {
                writer.write("-1,"); // => Pas de chemin
            } else {
                writer.write(longueurAst + ",");
            }
            writer.write(timeElapsed + ",");
            // Autres algos
            // =================== Quadtree
            System.out.println("running quadtree");
            Instant before = Instant.now();
            double longueurQuadtree = Quadtree.quadtree(world);
            Instant after = Instant.now();
            timeElapsed = Duration.between(before, after).toMillis();
            if (longueurQuadtree <= 0) {
                writer.write("-1,"); // => Pas de chemin
            } else {
                writer.write(longueurQuadtree + ",");
            }
            writer.write(timeElapsed + ",");
            // Autres algos
            // ======================= JPS
            System.out.println("running JPS");
            // (world.width * world.height) / (world.tailleReg * world.tailleReg) <= 100000000
            if (true) {
                before = Instant.now();
                double longueurJPS = JPSGrid.JPS(world);
                after = Instant.now();
                timeElapsed = Duration.between(before, after).toMillis();
                if (longueurQuadtree <= 0) {
                    writer.write("-1,"); // => Pas de chemin
                } else {
                    writer.write(longueurJPS + ",");
                }
                writer.write(timeElapsed + ",");
            }
            writer.write("\n");
            writer.flush();
        }
        writer.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
    }

    public static void benchmarkGenerate() {
        int[] Awidths = {100, 200, 500, 1000, 8000};
        int[] Aheights = {100, 200, 500, 1000, 8000};
        int[] Ataille_regions = {10, 20, 50, 100};
        int[] Anb_obstacle = {3, 5, 10, 40};
        for (int width_ix = 0; width_ix < Awidths.length; width_ix += 1) {
            for (int height_ix = 0; height_ix < Aheights.length; height_ix += 1) {
                for (int taille_ix = 0; taille_ix < Ataille_regions.length; taille_ix += 1) {
                    for (int obs_ix = 0; obs_ix < Anb_obstacle.length; obs_ix += 1) {
                        int width = Awidths[width_ix];
                        int height = Aheights[height_ix];
                        int nb_obstacles = Anb_obstacle[obs_ix];
                        int taille_region = Ataille_regions[taille_ix];
                        if (width <= 2 * taille_region || height <= 2 * taille_region) {
                            continue;
                        }
                        World world = RandomWorld
                            .randomWorld(width, height, nb_obstacles, taille_region);
                        SaveWorld.saveWorld(world, 
                            "benchw" + width + "h" + height + "o" + nb_obstacles 
                            + "t" + taille_region);
                    }
                }
            }
        }
    }
}
