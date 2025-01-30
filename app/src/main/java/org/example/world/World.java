package org.example.world;

import java.util.List;

public class World {

    public World(int w, int h, ERegionType regtype, Point _start, Point _destination,
        int _tailleRegion) {
        width = h;
        height = w;
        regiontype = regtype;
        start = _start;
        destination = _destination;
        tailleReg = _tailleRegion;
        passThrough = new int[w * h];
        for (int ix = 0; ix < w * h; ix++) {
            passThrough[ix] = 0;
        }
    }

    public enum ERegionType {
        OCTILE,
        TRIANGLE,
        CONTINUOUS
    }

    public abstract class Region {
        public class Octile {

        }

        public class Triangle {

        }

        public int x;
        public int y;
    }

    public class Obstacle {
        public Obstacle(float _x,  float _y, float r) {
            x = _x;
            y = _y;
            radius = r;
        }

        public float x;
        public float y;
        public float radius;
    }

    public class Point {
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

    public static int OBSTACLE = -100;

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
