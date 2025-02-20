package org.example.world;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;

public class SaveWorld {
    public final static String SAVE_LOCATION = "./benchmark/";
    public static void saveWorld(World world, String filename) {
        try {
            // If folder does not exist create it
            Path dossier = Paths.get(SAVE_LOCATION);
            if (!Files.exists(dossier)) {
                Files.createDirectories(dossier);
            }
            Path file = Path.of(SAVE_LOCATION + filename);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile(), false));
            writer.write("width:" + world.width + "\n");
            writer.write("height:" + world.height + "\n");
            writer.write("taille_region:" + world.tailleReg + "\n");
            writer.write("start:" + Math.round(world.start.x) + ":" + Math.round(world.start.y) 
                + "\n");
            writer.write("destination:" + Math.round(world.destination.x) + ":" 
                + Math.round(world.destination.y) + "\n");
            for (World.Obstacle o : world.obstacles) {
                writer.write("obstacle:" + Math.round(o.x) + ":" + Math.round(o.y) + ":" 
                    + Math.round(o.radius) + "\n");
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.err.println("SaveWorld.saveWorld: could not save the world " + e.getMessage());
        }
    }
}