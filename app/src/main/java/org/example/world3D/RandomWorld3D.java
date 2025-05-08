package org.example.world3D;

import java.time.Instant;
import java.util.Random;

public class RandomWorld3D {
    public final static int SEED = 88;
    public static World3D randomWorld3D(int w, int h, int d, int nbObstacles, int obstacleRadius,
        int tailleRegion) {
            // Generate start
            Random rng = new Random(Instant.now().toEpochMilli());
            int startx = 0;
            int starty = rng.nextInt(h);
            int startz = rng.nextInt(d);
            World3D.Point3D start = new World3D.Point3D(startx, starty, startz);
            // Generate destination
            int destx = w - 1;
            int desty = rng.nextInt(h);
            int destz = rng.nextInt(d);
            World3D.Point3D destination = new World3D.Point3D(destx, desty, destz);
            World3D world = new World3D(w, h, start, destination, tailleRegion, d);
            // Generate obstacles
            for (int obsi = 0; obsi < nbObstacles; obsi++) {
                int obsx = rng.nextInt(w - 2 * tailleRegion) + tailleRegion;
                int obsy = rng.nextInt(h - 2 * tailleRegion) + tailleRegion;
                int obsz = rng.nextInt(d - 2 * tailleRegion) + tailleRegion;
                world.addObstacle(obsx, obsy, obsz, obstacleRadius);
            }
            return world;
    }
}
