package org.example.benchmark;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.Scanner;

import org.example.world3D.World3D;
import org.example.world.LoadWorld;
import org.example.world.World;
import org.example.world.examples.Astar3D;
import org.example.world.examples.Quadtree3D;
import org.example.world3D.LoadWorld3D;
import org.example.world3D.RandomWorld3D;
import org.example.world3D.SaveWorld3D;

public class Benchmark3D {
    public static void benchmarkGenerate3D() {
        try {
            Console in = System.console();
            // Demande à l'utilisateur les caractéristiques du monde à générer
            System.out.println("World width: ");
            int worldwidth = intFromString(in.readLine());
            System.out.println("World height: ");
            int worldheight = intFromString(in.readLine());
            System.out.println("World depth: ");
            int worlddepth = intFromString(in.readLine());
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
                World3D world = RandomWorld3D.randomWorld3D(worldwidth, worldheight, worlddepth,
                     nbObstacles, obstacleRadius, regionSize);
                SaveWorld3D.saveWorld(world, "world" + worldi, "./" + saveLocation);
            }
        } catch (Exception e) {
            System.err.println("benchmarkGenerate3D: [ERROR] : " + e.getMessage());
        }
    }

    public static void benchmarkAll() {
        try {
            Console console = System.console();
            System.out.println("In which folder are the graphs you want to benchmark ?");
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
            writer.write("nomGraphe,nbRegions,nbObstacles,Algorithm,Length,Time\n");
            for (int gx = 0; gx < nbGraphes; gx++) {
                System.out.println("opening: " + graphsPath + "/" + graphes[gx].getName());
                World3D world = LoadWorld3D.loadWorld(graphsPath + "/" + graphes[gx].getName());
                // ============== Astar3D
                writer.write(graphes[gx].getName() + "," + world.getNbRegion() + "," + world.obstacles.size() + ",");
                writer.write("astar3D,");
                Instant before = Instant.now();
                double length = Astar3D.astar(world);
                Instant after = Instant.now();
                long timeElapsed = Duration.between(before, after).toMillis();
                if (length < 0) {
                    writer.write("-1,");
                } else {
                    writer.write(length + ",");
                }
                writer.write(timeElapsed + "\n");
                // ============== Quadtree (Octree)
                writer.write(graphes[gx].getName() + "," + world.getNbRegion() + "," + world.obstacles.size() + ",");
                writer.write("quadtree3D,");
                before = Instant.now();
                length = Quadtree3D.quadtree(world);
                after = Instant.now();
                timeElapsed = Duration.between(before, after).toMillis();
                if (length < 0) {
                    writer.write("-1,");
                } else {
                    writer.write(length + ",");
                }
                writer.write(timeElapsed + "\n");
                writer.flush();
            }
            writer.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
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
}
