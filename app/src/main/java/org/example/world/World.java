package org.example.world;

import java.util.ArrayList;
import java.util.List;

public class World {

    public World(int w, int h, ERegionType regtype, Point _start, Point _destination,
        int _tailleRegion) {
        width = h;
        height = w;
        regiontype = regtype;
        tailleReg = _tailleRegion;
        start = _start;
        destination = _destination;
        info = new InfoWorld();
        Region startReg, destinationRegion;
        switch(regtype) {
            case OCTILE: {
                startReg = new Region.Octile(start.x, start.y, tailleReg);
                destinationRegion = new Region.Octile(destination.x, destination.y, tailleReg);
                break;
            }
            default: {
                startReg = new Region.Octile(start.x, start.y, tailleReg);
                destinationRegion = new Region.Octile(destination.x, destination.y, tailleReg);
            }
        }
        passThrough = new int[info.widthInRegion * info.heightInRegion];
        for (int ix = 0; ix < info.widthInRegion * info.heightInRegion; ix++) {
            passThrough[ix] = 0;
        }
        passThrough[(startReg.x / tailleReg) * info.heightInRegion + (startReg.y / tailleReg)] = InnerWorld.START;
        passThrough[(destinationRegion.x / tailleReg) *  info.heightInRegion + (destinationRegion.y / tailleReg)] = InnerWorld.DESTINATION;
        
    }

    public enum ERegionType {
        OCTILE,
        TRIANGLE,
        CONTINUOUS
    }

    public static class Region {
        public Region(float x, float y, int tailleReg) {
            int regionStartX = Math.round(x) / tailleReg;
            int regionStartY = Math.round(y) / tailleReg;
            this.x = regionStartX * tailleReg;
            this.y = regionStartY * tailleReg;
            this.distance = 0;
        }

        public Region(int x, int y, int tailleReg, double dist) {
            this.x = x;
            this.y = y;
            this.distance = dist;
        }

        public static class Octile extends Region {
            public Octile(float x, float y, int tailleReg) {
                super(x, y, tailleReg);
            }
        }

        public static class Triangle {
            public Triangle() {

            }
        }

        public final int x;
        public final int y;
        public final double distance;
    }

    public static class Obstacle {
        public Obstacle(float _x,  float _y, float r) {
            x = _x;
            y = _y;
            radius = r;
        }

        public float x;
        public float y;
        public float radius;
    }

    public static class Point {
        public Point(float _x, float _y) {
            x = _x;
            y = _y;
        }

        public float x;
        public float y;
    }

    public void addObstacle(float x, float y, float r) {
        Obstacle ob = new Obstacle(x, y, r);
        obstacles.add(ob);
    }

    public static class InnerWorld {
        public static int OBSTACLE = -100;
        public static int START = -80;
        public static int DESTINATION = -90;
    }

    public class InfoWorld {
        public InfoWorld() {

            switch (regiontype) {
                case OCTILE:
                    widthInRegion = width / tailleReg;
                    heightInRegion = height / tailleReg;
                    nbRegion = widthInRegion * heightInRegion;
                    break;
                default:
                    widthInRegion = 0;
                    heightInRegion = 0;
            }
        }

        public List<Region> getAdjacent(Region r) {
            List<Region> neighborhood = new ArrayList<>();
            switch (regiontype) {
                case OCTILE:
                    // We go through each adjacent region
                    // and store those that are not obstacles
                    for (int x = r.x - 1; x < r.x + 2; x++) {
                        if (x <= 0 || x >= widthInRegion) continue;
                        for (int y = r.y - 1; y < r.y - 2; y++) {
                            if (y <= 0 || y >= heightInRegion) continue;
                            Region neigh = new Region(x, y, tailleReg, Math.sqrt(x + y));
                            neighborhood.add(neigh);
                        }
                    }
                    break;
                default:
                    return null;
            }
            return neighborhood;
        }

        public int widthInRegion;
        public int heightInRegion;
        public int nbRegion;
    }
    

    public int tailleReg;
    public int height;
    public int width;
    public List<Obstacle> obstacles;
    public ERegionType regiontype;
    public Point start;
    public Point destination;
    public Region startReg;
    public Region destinationReg;
    public int[] passThrough;
    public InfoWorld info; 
}
