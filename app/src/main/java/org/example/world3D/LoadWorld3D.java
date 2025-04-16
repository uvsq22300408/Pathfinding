package org.example.world3D;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class LoadWorld3D {   
    public static World3D loadWorld(String SAVE_LOCATION) {
        try {
            List<World3D.Obstacle3D> obstacles = new ArrayList<>();
            int width = 0;
            int height = 0;
            int depth = 0;
            int tr = 0;
            World3D.Point3D start = new World3D.Point3D(0, 0, 0);
            World3D.Point3D destination = new World3D.Point3D(0, 0, 0);
            List<String> lines = Files.readAllLines(Path.of(SAVE_LOCATION));
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
                    case "depth":
                        depth = str_to_int(fields_and_values[1].strip());
                        break;
                    case "taille_region":
                        tr = str_to_int(fields_and_values[1].strip());
                        break;
                    case "start":
                        int startx = str_to_int(fields_and_values[1].strip());
                        int starty = str_to_int(fields_and_values[2].strip());
                        int startz = str_to_int(fields_and_values[3].strip());
                        start = new World3D.Point3D(startx, starty, startz);
                        break;
                    case "destination":
                        int destinationx = str_to_int(fields_and_values[1].strip());
                        int destinationy = str_to_int(fields_and_values[2].strip());
                        int destinationz = str_to_int(fields_and_values[3].strip());
                        destination = new World3D.Point3D(destinationx, destinationy, destinationz);
                        break;
                    case "obstacle":
                        int obstaclex = str_to_int(fields_and_values[1].strip());
                        int obstacley = str_to_int(fields_and_values[2].strip());
                        int obstaclez = str_to_int(fields_and_values[3].strip());
                        int obstacleradius = str_to_int(fields_and_values[3].strip());
                        obstacles.add(new World3D.Obstacle3D(obstaclex, obstacley, obstaclez, obstacleradius));
                        break;
                    default:
                        break;
                }
            }
            World3D world = new World3D(width, height, start, destination, tr, depth);
            for (World3D.Obstacle3D o : obstacles) {
                world.addObstacle(o.x, o.y, o.z, o.radius);
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
