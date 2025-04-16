package org.example.benchmark;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Scanner;

import org.example.world3D.World3D;
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
