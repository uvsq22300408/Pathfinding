package org.example.world;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.example.world.World.ERegionType;

public class LoadWorld {
    public static World loadWorld(String filename) {
        try {
            List<World.Obstacle> obstacles = new ArrayList<>();
            int width = 0;
            int height = 0;
            int tr = 0;
            World.Point start = new World.Point(0, 0);
            World.Point destination = new World.Point(0, 0);
            List<String> lines = Files.readAllLines(Path.of(SaveWorld.SAVE_LOCATION + filename));
            for (String l : lines) {
                String[] fields_and_values = l.strip().split(":");
                String attribute = fields_and_values[0];
                
                switch (attribute) {
                    case "width":
                        width = str_to_int(fields_and_values[1].strip());
                        break;
                    case "height":
                        height = str_to_int(fields_and_values[1].strip());
                        break;
                    case "taille_region":
                        tr = str_to_int(fields_and_values[1].strip());
                        break;
                    case "start":
                        int startx = str_to_int(fields_and_values[1].strip());
                        int starty = str_to_int(fields_and_values[2].strip());
                        start = new World.Point(startx, starty);
                        break;
                    case "destination":
                        int destinationx = str_to_int(fields_and_values[1].strip());
                        int destinationy = str_to_int(fields_and_values[2].strip());
                        destination = new World.Point(destinationx, destinationy);
                        break;
                    case "obstacle":
                        int obstaclex = str_to_int(fields_and_values[1].strip());
                        int obstacley = str_to_int(fields_and_values[2].strip());
                        int obstacleradius = str_to_int(fields_and_values[3].strip());
                        obstacles.add(new World.Obstacle(obstaclex, obstacley, obstacleradius));
                        break;
                    default:
                        break;
                }
            }
            World world = new World(width, height, ERegionType.OCTILE, start, destination, tr);
            for (World.Obstacle o : obstacles) {
                world.addObstacle(o.x, o.y, o.radius);
            }
            return world;     
        }
        catch (Exception e) {
            System.err.println("SaveWorld.loadWorld: could not load the world " + e.getMessage());
        }
        return null;
        
    }

    private static int str_to_int(String s) {
        int i = 0;
        for (int c : s.chars().toArray()) {
            i *= 10;
            i += c - '0';
        }
        return i;
    }
}
