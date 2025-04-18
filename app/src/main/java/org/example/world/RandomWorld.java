package org.example.world;

import java.util.Iterator;
import java.util.Random;

import org.example.world.World.ERegionType;
import org.example.world3D.World3D;

public class RandomWorld {
    public final static int SEED = 10;

public static World randomWorld(int w, int h, int nbObstacles, int obstacleRadius,
        int tailleRegion) {
            // Generate start
            Random rng = new Random(88);
            int startx = 0;
            int starty = rng.nextInt(h);
            World.Point start = new World.Point(startx, starty);
            // Generate destination
            int destx = w - 1;
            int desty = rng.nextInt(h);
            World.Point destination = new World.Point(destx, desty);
            World world = new World(w, h, World.ERegionType.OCTILE, start, destination,
                 tailleRegion);
            // Generate obstacles
            for (int obsi = 0; obsi < nbObstacles; obsi++) {
                int obsx = rng.nextInt(w - 2 * tailleRegion) + tailleRegion;
                int obsy = rng.nextInt(h - 2 * tailleRegion) + tailleRegion;
                world.addObstacle(obsx, obsy, obstacleRadius);
            }
            return world;
    }

    public static World oldRandomWorld(int width, int height, int nbObstacles, int taille_region) {
        Random rng = new Random(SEED);
        // Place start in first column
        int startx = 0;
        int starty = rng.nextInt(height);
        World.Point start = new World.Point(startx, starty);
        // Place destination in last column
        int destx = width - 1;
        int desty = rng.nextInt(height);
        World.Point destination = new World.Point(destx, desty);
        World world = new World(width, height, ERegionType.OCTILE, start, destination,
             taille_region);
        // Place the obstacles between first and last columns
        for (int obsi = 0; obsi < nbObstacles; obsi++) {
            int obsx = rng.nextInt(width - 2 * taille_region) + taille_region;
            int obsy = rng.nextInt(height - 2 * taille_region) + taille_region; // Avoid obstacle covering Out-Of-Bond
            // Le radius est entre taille_region / 2 et taille_region * 2;
            int radius = rng.nextInt(2 * taille_region - taille_region / 2) + taille_region / 2;
            world.addObstacle(obsx, obsy, radius);
        }
        return world;
    }
}
