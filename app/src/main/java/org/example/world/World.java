package org.example.world;

import java.util.List;

public class World {

    public World(int w, int h) {
        width = h;
        height = w;
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

    public int height;
    public int width;
    public List<Obstacle> obstacles;
}
