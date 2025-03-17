package org.example.world.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.example.world.*;
import org.example.world.World.Obstacle;
import org.example.world.World.Region;

public class Quadtree {
    public static double Infinity = -1;
    public static int MAX_DIVISION = pow(2, 6);

    public static double quadtree(World world) {
        // Divide regions in 4 if they contain an obstacle
        // until they get to a minimum size
        regionId = 0;
        QuadtreeRegion reg = new QuadtreeRegion(0, 0, 1);
        reg.containsObstacle = containsObstacle(world, reg);
        Hashtable<Integer, ArrayList<QuadtreeRegion>> mapRegionsByX = new Hashtable<>();
        Hashtable<Integer, ArrayList<QuadtreeRegion>> mapRegionsByY = new Hashtable<>();
        Hashtable<Integer, ArrayList<QuadtreeRegion>> mapRegionsByXPlusWidth = new Hashtable<>();
        Hashtable<Integer, ArrayList<QuadtreeRegion>> mapRegionsByYPlusHeight = new Hashtable<>();
        divideRegion(world, reg, mapRegionsByX, mapRegionsByY, mapRegionsByXPlusWidth,
             mapRegionsByYPlusHeight, 1);
        finalMapRegionsByX = mapRegionsByX;
        finalMapRegionsByY = mapRegionsByY;
        finalMapRegionsByXPlusWidth = mapRegionsByXPlusWidth;
        finalMapRegionsByYPlusHeight = mapRegionsByYPlusHeight;
        
        // Run A* to find a path along regions centers
        QuadtreeRegion[] fathers = new QuadtreeRegion[regionId];
        fathers[startRegion.id] = null;
        List<QuadtreeRegion> openSet = new ArrayList<>();
        openSet.add(startRegion);
        double[] gScore = new double[regionId];
        double[] fScore = new double[regionId];
        
        for (int rx = 0; rx < regionId; rx++) {
            gScore[rx] = Infinity;
            fScore[rx] = Infinity;
        }
        gScore[startRegion.id] = 0;
        fScore[startRegion.id] = h(startRegion, endRegion);
        while(!openSet.isEmpty()) {
            QuadtreeRegion current = getMinFScoreRegion(fScore, openSet, world);
            if (current == null) {
                return -1; // Pas de chemin.
            }
            if (current.id == endRegion.id) { 
                return gScore[endRegion.id];
            }
            openSet.remove(current);
            Set<QuadtreeRegion> adjacents = adjacents(current, world.width, world.height);
            for (QuadtreeRegion adj : adjacents) {
                // Calculate distance between currentRegion and adj
                double distance = 0;
                int adjwidth = world.width / adj.divisionFactor;
                int adjheight = world.height / adj.divisionFactor;
                double adjCenterX = adj.x + (adjwidth / 2.0);
                double adjCenterY = adj.y + (adjheight / 2.0);
                if (current.id == startRegion.id) {
                    // Return distance between start and neighbor's center
                    double dx = adjCenterX - world.start.x;
                    double dy = adjCenterY - world.start.y;
                    distance = Math.sqrt(dx * dx + dy *  dy);
                } else {
                    double currentRegWidth = world.width / current.divisionFactor;
                    double currentRegHeight = world.height / current.divisionFactor;
                    double currentCenterX = current.x + (currentRegWidth / 2.0);
                    double currentCenterY = current.y + (currentRegHeight / 2.0);
                    double dx = adjCenterX - currentCenterX;
                    double dy = adjCenterY - currentCenterY;
                    distance = Math.sqrt(dx * dx + dy * dy);
                }
                double tentativeGScore = gScore[adj.id] + distance;
                if (gScore[adj.id] == Infinity 
                        || tentativeGScore < gScore[adj.id]) {
                    fathers[adj.id] = current;
                    gScore[adj.id] = tentativeGScore;
                    fScore[adj.id] = tentativeGScore + h(adj, endRegion);
                    if (!openSet.contains(adj)) {
                        openSet.add(adj);
                    }
                }
            }
        }
        return -1;
    }

    /** Astar heuristic function. */
    private static double h(QuadtreeRegion start, QuadtreeRegion dest) {
        // Return heuristic distance between start and dest.
        // Here, euclidean distance
        return Math.sqrt(Math.abs(dest.x - start.x) + Math.abs(dest.y - start.y));
    }

    /** Return minreg, the region having the smallest fscore,
     * or null if no suitable candidate. */ 
    private static QuadtreeRegion getMinFScoreRegion(double[] fScore, List<QuadtreeRegion> openSet, World world) {
        double min = Infinity;
        QuadtreeRegion minreg = null;
        for (QuadtreeRegion openReg: openSet) {
            double currentFScore = fScore[openReg.id];
            if (currentFScore == Infinity) {
                continue;
            }
            if (min == Infinity || min > currentFScore) {
                min = currentFScore;
                minreg = openReg;
            }
        }
        return minreg;
    }

    /** Return the ids of neighboring regions to current.
     * width and height are the world's dimensions
     */
    public static Set<QuadtreeRegion> adjacents(QuadtreeRegion currentRegion, int width, int height) {
        int currentWidth = width / currentRegion.divisionFactor;
        int currentHeight = height / currentRegion.divisionFactor;
        Set<QuadtreeRegion> adj = new LinkedHashSet<>();
        // Select all adjacent regions left
        List<QuadtreeRegion> leftReg = finalMapRegionsByXPlusWidth.get(currentRegion.x);
        if (leftReg != null) {
            leftReg.forEach((r) -> {
                int rheight = height / r.divisionFactor;
                if (r.y <= currentRegion.y + currentHeight && r.y + rheight >= currentRegion.y) {
                    adj.add(r);
                }
            });
        }
        // Select all adjacent regions right
        List<QuadtreeRegion> rightReg = finalMapRegionsByX.get(currentRegion.x + currentWidth);
        if (rightReg != null) {
            rightReg.forEach((r) -> {
                int rheight = height / r.divisionFactor;
                if (r.y <= currentRegion.y + currentHeight && r.y + rheight >= currentRegion.y) {
                    adj.add(r);
                }
            });
        }
        // Select all adjacent regions up (above)
        List<QuadtreeRegion> upReg = finalMapRegionsByY.get(currentRegion.y + currentHeight);
        if (upReg != null) {
            upReg.forEach((r) -> {
                int rwidth = width / r.divisionFactor;
                if (r.x <= currentRegion.x + currentWidth && r.x + rwidth >= currentRegion.x) {
                    adj.add(r);
                }
            });
        }
        // Select all adjacent regions down (below)
        List<QuadtreeRegion> downReg = finalMapRegionsByYPlusHeight.get(currentRegion.y); 
            if (downReg != null) {
                downReg.forEach((r) -> {
                    int rwidth = width / r.divisionFactor;
                    if (r.x <= currentRegion.x + currentWidth && r.x + rwidth >= currentRegion.x) {
                        adj.add(r);
                    }
                });
            }
        System.out.println("Adjacents: " + adj.size());
        return adj;
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

    // Divide regions and set Start and Endpoints
    public static void divideRegion(World world, QuadtreeRegion reg, 
        Hashtable<Integer, ArrayList<QuadtreeRegion>> mapRegionsByX, 
        Hashtable<Integer, ArrayList<QuadtreeRegion>> mapRegionsByY,
        Hashtable<Integer, ArrayList<QuadtreeRegion>> mapRegionsByXPlusWidth,
        Hashtable<Integer, ArrayList<QuadtreeRegion>> mapRegionsByYPlusHeight,
        int divisionFactor) {
            // If reg contains an obstacle, it gets divided into four smaller regions
            if (reg.containsObstacle) {
                if (divisionFactor >= MAX_DIVISION) {
                    // Cannot divide anymore, region reached its minimum size
                    return;
                }
                divisionFactor *= 2;
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
                divideRegion(world, topLeft, mapRegionsByX, 
                    mapRegionsByY, mapRegionsByXPlusWidth, mapRegionsByYPlusHeight, divisionFactor);
                divideRegion(world, topRight, mapRegionsByX,
                    mapRegionsByY, mapRegionsByXPlusWidth, mapRegionsByYPlusHeight, divisionFactor);
                divideRegion(world, bottomLeft, mapRegionsByX,
                    mapRegionsByY, mapRegionsByXPlusWidth, mapRegionsByYPlusHeight, divisionFactor);
                divideRegion(world, bottomRight, mapRegionsByX,
                    mapRegionsByY, mapRegionsByXPlusWidth, mapRegionsByYPlusHeight, divisionFactor);
            }
            // If reg does not contain an obstacle, it is added to the map of known
            // regions with an Id
            else {
                reg.id = Quadtree.regionId;
                Quadtree.regionId += 1;
                int regwidth = world.width / reg.divisionFactor;
                int regheight = world.height / reg.divisionFactor;
                ArrayList<QuadtreeRegion> regionsByX = mapRegionsByX.get(reg.x);
                ArrayList<QuadtreeRegion> regionsByY = mapRegionsByY.get(reg.y);
                ArrayList<QuadtreeRegion> regionsByXPlusWidth = mapRegionsByXPlusWidth.get(reg.x + regwidth);
                ArrayList<QuadtreeRegion> regionsByYPlusHeight = mapRegionsByYPlusHeight.get(reg.y + regheight);
                if (regionsByX == null) {
                    ArrayList<QuadtreeRegion> newRegionsByX = new ArrayList<>();
                    newRegionsByX.add(reg);
                    mapRegionsByX.put(reg.x, newRegionsByX);
                } else {
                    regionsByX.add(reg);
                }
                if (regionsByY == null) {
                    ArrayList<QuadtreeRegion> newRegionsByY = new ArrayList<>();
                    newRegionsByY.add(reg);
                    mapRegionsByY.put(reg.y, newRegionsByY);
                } else {
                    regionsByY.add(reg);
                }
                if (regionsByXPlusWidth == null) {
                    ArrayList<QuadtreeRegion> newRegionsByXPlusWidth = new ArrayList<>();
                    newRegionsByXPlusWidth.add(reg);
                    mapRegionsByXPlusWidth.put(reg.x + regwidth, newRegionsByXPlusWidth);
                } else {
                    regionsByXPlusWidth.add(reg);
                }
                if (regionsByYPlusHeight == null) {
                    ArrayList<QuadtreeRegion> newRegionsByYPlusHeight = new ArrayList<>();
                    newRegionsByYPlusHeight.add(reg);
                    mapRegionsByYPlusHeight.put(reg.y + regheight, newRegionsByYPlusHeight);
                } else {
                    regionsByYPlusHeight.add(reg);
                }
                // If reg contains start or end, save them as startRegion or EndRegion
                if (startRegion == null && world.start.x >= reg.x 
                    && world.start.x <= reg.x + regwidth) {
                    if (world.start.y >= reg.y &&
                         world.start.y <= reg.y + regheight) {
                            startRegion = reg;
                    }
                }
                if (endRegion == null && world.destination.x >= reg.x 
                    && world.destination.x <= reg.x + regwidth) {
                    if (world.destination.y >= reg.y &&
                         world.destination.y <= reg.y + regheight) {
                            endRegion = reg;
                    }
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

        public int id;
        public int x;
        public int y;
        public int divisionFactor;
        public boolean containsObstacle;
    }

    // Fast-Exponentiation
    public static int pow(int a, int b) {
        if (b == 0) {
            return 1;
        }
        if (b % 2 == 1) {
            return a * pow (a, b / 2);
        } else {
            int p = pow (a, b / 2);
            return p * p;
        }
    }

    public static Hashtable<Integer, ArrayList<QuadtreeRegion>> finalMapRegionsByX;
    public static Hashtable<Integer, ArrayList<QuadtreeRegion>> finalMapRegionsByXPlusWidth;
    public static Hashtable<Integer, ArrayList<QuadtreeRegion>> finalMapRegionsByY;
    public static Hashtable<Integer, ArrayList<QuadtreeRegion>> finalMapRegionsByYPlusHeight;
    public static QuadtreeRegion startRegion;
    public static QuadtreeRegion endRegion;
    public static int regionId;
}
