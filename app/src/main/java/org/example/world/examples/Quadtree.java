package org.example.world.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.example.world.*;
import org.example.world.World.Obstacle;

public class Quadtree {
    
    public static int MAX_DIVISION = 6;
    public static double quadtree(World world) {
        // Divide regions in 4 if they contain an obstacle
        // until they get to a minimum size
        QuadtreeRegion reg = new QuadtreeRegion(0, 0, 1);
        reg.containsObstacle = containsObstacle(world, reg);
        Hashtable<Integer, ArrayList<QuadtreeRegion>> mapRegionsByX = new Hashtable<>();
        Hashtable<Integer, ArrayList<QuadtreeRegion>> mapRegionsByY = new Hashtable<>();
        divideRegion(world, reg, mapRegionsByX, mapRegionsByY, MAX_DIVISION);
        finalMapRegionsByX = mapRegionsByX;
        finalMapRegionsByY = mapRegionsByY;
        // Run A* to find a path along regions centers

        return -1;
    }

    /** Returns true if the region contains an obstacle. */
    public static boolean containsObstacle(World world, QuadtreeRegion reg) {
        // Check if an obstacle is in or overrun the region.
        List<Obstacle> obstacles = world.obstacles;
        int regwidth = world.width / reg.divisionFactor;
        int regheight = world.height / reg.divisionFactor;
        for (Obstacle o : obstacles) {
            if (o.x - o.radius < reg.x + regwidth && o.x + o.radius > reg.x) {
                if (o.y - o.radius < reg.y + regheight && o.y + o.radius > reg.y) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void divideRegion(World world, QuadtreeRegion reg, 
        Hashtable<Integer, ArrayList<QuadtreeRegion>> mapRegionsByX, 
        Hashtable<Integer, ArrayList<QuadtreeRegion>> mapRegionsByY,
        int divisionFactor) {
            // If reg contains an obstacle, it gets divided into four smaller regions
            if (reg.containsObstacle) {
                if (divisionFactor == MAX_DIVISION) {
                    // Cannot divide anymore, region reached its minimum size
                    return;
                }
                divisionFactor += 1;
                int width = world.width / divisionFactor;
                int height = world.height / divisionFactor;
                QuadtreeRegion topLeft = new QuadtreeRegion(reg.x, reg.y + height, 
                     divisionFactor);
                topLeft.containsObstacle = containsObstacle(world, topLeft);
                QuadtreeRegion topRight = new QuadtreeRegion(reg.x + width, reg.y  + height,
                     divisionFactor);
                topRight.containsObstacle = containsObstacle(world, topRight);
                QuadtreeRegion bottomLeft = new QuadtreeRegion(reg.x, reg.y, divisionFactor);
                bottomLeft.containsObstacle = containsObstacle(world, bottomLeft);
                QuadtreeRegion bottomRight = new QuadtreeRegion(reg.x + width, reg.y, 
                     divisionFactor);
                bottomRight.containsObstacle = containsObstacle(world, bottomRight);
                divideRegion(world, topLeft, mapRegionsByX, mapRegionsByY, divisionFactor);
                divideRegion(world, topRight, mapRegionsByX, mapRegionsByY, divisionFactor);
                divideRegion(world, bottomLeft, mapRegionsByX, mapRegionsByY, divisionFactor);
                divideRegion(world, bottomRight, mapRegionsByX, mapRegionsByY, divisionFactor);
            }
            // If reg does not contain an obstacle, it is added to the map of known
            // regions.
            else {
                ArrayList<QuadtreeRegion> regionsByX = mapRegionsByX.get(reg.x);
                ArrayList<QuadtreeRegion> regionsByY = mapRegionsByY.get(reg.y);
                if (regionsByX == null) {
                    ArrayList<QuadtreeRegion> newRegionsByX = new ArrayList<>();
                    newRegionsByX.add(reg);
                    mapRegionsByX.put(reg.x, newRegionsByX);
                } else {
                    regionsByX.add(reg);
                    mapRegionsByX.put(reg.x, regionsByX);
                }
                if (regionsByY == null) {
                    ArrayList<QuadtreeRegion> newRegionsByY = new ArrayList<>();
                    newRegionsByY.add(reg);
                    mapRegionsByY.put(reg.y, newRegionsByY);
                } else {
                    regionsByY.add(reg);
                    mapRegionsByY.put(reg.y, regionsByY);
                }
            }
    }

    public static class QuadtreeRegion {
        public QuadtreeRegion(int x, int y, int divisionFactor) {
            this.x = x;
            this.y = y;
            this.divisionFactor = divisionFactor;
            this.containsObstacle = false;
        }

        public int x;
        public int y;
        public int divisionFactor;
        public boolean containsObstacle;
    }

    public static Hashtable<Integer, ArrayList<QuadtreeRegion>> finalMapRegionsByX;
    public static Hashtable<Integer, ArrayList<QuadtreeRegion>> finalMapRegionsByY;
}
