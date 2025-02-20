package org.example.world;

import java.util.Iterator;
import java.util.Random;

import org.example.world.World.ERegionType;

public class RandomWorld {
    public final static int SEED = 10;
    public static World randomWorld(int width, int height, int nbObstacles, int taille_region) {
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
