package org.example.world;

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
        Region startReg, destinationRegion;
        switch(regtype) {
            case OCTILE: {
                startReg = new Region.Octile(start.x, start.y, tailleReg);
                destinationRegion = new Region.Octile(destination.x, destination.y, tailleReg);
            }
            default: {
                startReg = new Region.Octile(start.x, start.y, tailleReg);
                destinationRegion = new Region.Octile(destination.x, destination.y, tailleReg);
            }
        }
        passThrough = new int[(w / _tailleRegion) * (h / _tailleRegion)];
        for (int ix = 0; ix < (w / _tailleRegion) * (h / _tailleRegion); ix++) {
            passThrough[ix] = 0;
        }
        passThrough[(startReg.x / tailleReg) * (h / tailleReg) + (startReg.y / tailleReg)] = InnerWorld.START;
        passThrough[(destinationRegion.x / tailleReg) * (h / tailleReg) + (destinationRegion.y / tailleReg)] = InnerWorld.DESTINATION;
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

        public int x;
        public int y;
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
}
