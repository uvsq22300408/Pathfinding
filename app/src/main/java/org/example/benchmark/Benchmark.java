package org.example.benchmark;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
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
import org.example.world3D.RandomWorld3D;
import org.example.world3D.SaveWorld3D;
import org.example.world3D.World3D;

public class Benchmark {
    public static void benchmarkall() {
        try {
        Console console = System.console();
        System.out.println("In what folder are the graphs you want to benchmark ?");
        String graphsPath = console.readLine();
        File dossier = new File(graphsPath);
        File[] graphes = dossier.listFiles();
        int nbGraphes = graphes.length;
        System.out.println(nbGraphes + " graphs were found.");
        System.out.println("Wherer do you want to save the file ? (path/filename)");
        String resultsPath = console.readLine();
        Path file = Path.of(resultsPath + ".csv");
        if (!Files.exists(file.getParent())) {
            System.out.println("Creating save path : " + file.getParent());
            Files.createDirectories(file.getParent());
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile(),
            false));
        
        // Ajouter les autres algorithmes

        // Dijkstra : ,DijkstraLength,DijkstraTimeMs
        writer.write("nomGraphe,AstarLength,AstarTime,QuadtreeLength,QuadtreeTime,JPSLength,JPSTime\n");
        for (int gx = 0; gx < nbGraphes; gx++) {
            System.out.println("opening: " + graphsPath + "/" + graphes[gx].getName());
            World world = LoadWorld.loadWorld(graphsPath + "/" + graphes[gx].getName());
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
            System.err.println("Error: " + e.getMessage());
        }
        
    }

    public static void benchmarkGenerate() {
        try {
            Console in = System.console();
            // Demande à l'utilisateur les caractéristiques du monde à générer
            System.out.println("World width: ");
            int worldwidth = intFromString(in.readLine());
            System.out.println("World height: ");
            int worldheight = intFromString(in.readLine());
            System.out.println("Number of obstacles: ");
            int nbObstacles = intFromString(in.readLine());
            System.out.println("Obstacle radius: ");
            int obstacleRadius = intFromString(in.readLine());
            System.out.println("Region size: ");
            int regionSize = intFromString(in.readLine());
            // Nombre de mondes
            System.out.println("Number of world to generate: ");
            int nbWorlds = intFromString(in.readLine());
            // Emplacement pour sauvegarder les fichiers
            System.out.println("Save location: ");
            String saveLocation = in.readLine();
    
            for (int worldi = 0; worldi < nbWorlds; worldi++) {
                World world = RandomWorld.randomWorld(worldwidth, worldheight,
                     nbObstacles, obstacleRadius, regionSize);
                SaveWorld.saveWorld(world, "world" + worldi, "./" + saveLocation);
            }
        } catch (Exception e) {
            System.err.println("benchmarkGenerate3D: [ERROR] : " + e.getMessage());
        }
    }

    private static int intFromString(String s) {
        System.out.println("intFromString: " + s);
        int value = 0;
        for (char c : s.toCharArray()) {
            if (c >= '0' && c <= '9') {
                value *= 10;
                value += c - '0';
            }
        }
        return value;
    }

    public static void oldBenchmarkGenerate(String saveLocation) {
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
                            .oldRandomWorld(width, height, nb_obstacles, taille_region);
                        SaveWorld.saveWorld(world, 
                            "benchw" + width + "h" + height + "o" + nb_obstacles 
                            + "t" + taille_region, saveLocation);
                    }
                }
            }
        }
    }
}
