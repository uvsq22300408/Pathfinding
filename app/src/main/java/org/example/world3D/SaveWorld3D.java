package org.example.world3D;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;

public class SaveWorld3D {
    //public final static String SAVE_LOCATION = "./benchmark3D/";
    public static void saveWorld(World3D world, String filename, String SAVE_LOCATION) {
        try {
            // If folder does not exist create it
            Path dossier = Paths.get(SAVE_LOCATION);
            if (!Files.exists(dossier)) {
                Files.createDirectories(dossier);
            }
            Path file = Path.of(SAVE_LOCATION + "/" + filename);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile(), false));
            writer.write("width:" + world.width + "\n");
            writer.write("height:" + world.height + "\n");
            writer.write("depth:" + world.depth + "\n");
            writer.write("taille_region:" + world.tailleReg + "\n");
            writer.write("start:" + Math.round(world.start.x) + ":" + Math.round(world.start.y) 
                + ":" + Math.round(world.start.z) + "\n");
            writer.write("destination:" + Math.round(world.destination.x) + ":" 
                + Math.round(world.destination.y) + ":" + Math.round(world.destination.z) + "\n");
            for (World3D.Obstacle3D o : world.obstacles) {
                writer.write("obstacle:" + Math.round(o.x) + ":" + Math.round(o.y) + ":" 
                    + Math.round(o.z) + ":" + Math.round(o.radius) + "\n");
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.err.println("SaveWorld.saveWorld: could not save the world " + e.getMessage());
        }
    }
}

