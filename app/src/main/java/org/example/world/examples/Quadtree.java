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
        startRegion = null;
        endRegion = null;
        Quadtree.regionId = 0;
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
        
        if (startRegion == null || endRegion == null) {
            System.out.println("Error: Quadtree -> start or end was not found.");
            return -1;
        }
        // Run A* to find a path along regions centers
        Quadtree.fathers = new QuadtreeRegion[Quadtree.regionId];
        fathers[startRegion.id] = null;
        List<QuadtreeRegion> openSet = new ArrayList<>();
        openSet.add(startRegion);
        double[] gScore = new double[Quadtree.regionId];
        double[] fScore = new double[Quadtree.regionId];
        
        for (int rx = 0; rx < Quadtree.regionId; rx++) {
            gScore[rx] = Infinity;
            fScore[rx] = Infinity;
        }
        gScore[startRegion.id] = 0;
        fScore[startRegion.id] = h(startRegion, world);
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
                if (adj.id == endRegion.id) {
                    adjCenterX = world.destination.x;
                    adjCenterY = world.destination.y;
                }
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
                double tentativeGScore = gScore[current.id] + distance;
                if (gScore[adj.id] == Infinity || tentativeGScore < gScore[adj.id]) { 
                    Quadtree.fathers[adj.id] = current;
                    gScore[adj.id] = tentativeGScore;
                    fScore[adj.id] = tentativeGScore + h(adj, world);
                    if (!openSet.contains(adj)) {
                        openSet.add(adj);
                    }
                }
            }
        }
        return -1;
    }

    /** Astar heuristic function. */
    private static double h(QuadtreeRegion start, World world) {
        // Return heuristic distance between start and world dest.
        int startwidth = world.width / start.divisionFactor;
        int startheight = world.height / start.divisionFactor;
        int startCenterX = start.x + startwidth / 2;
        int startCenterY = start.y + startheight / 2;
        double dx = world.destination.x - startCenterX;
        double dy = world.destination.y - startCenterY;
        return Math.sqrt(dx * dx + dy * dy);
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
        List<QuadtreeRegion> leftNeighbours = finalMapRegionsByXPlusWidth.get(currentRegion.x - 1);
        List<QuadtreeRegion> left = fuse(leftReg, leftNeighbours);
        //System.out.println("====== Region" + currentRegion.id);
        //System.out.println("leftReg: " + currentRegion.x + " finalX+Width : " + (currentRegion.x -1));
        if (left != null) {
            left.forEach((r) -> {
                int rheight = height / r.divisionFactor;
                if (r.y <= currentRegion.y + currentHeight && r.y + rheight >= currentRegion.y) {
                    adj.add(r);
                }
            });
        }
        // Select all adjacent regions right
        List<QuadtreeRegion> rightReg = finalMapRegionsByX.get(currentRegion.x + currentWidth);
        List<QuadtreeRegion> rightNeighbours = finalMapRegionsByX.get(currentRegion.x + currentWidth + 1);
        List<QuadtreeRegion> right = fuse(rightReg, rightNeighbours);
        //System.out.println("rightReg: " + (currentRegion.x + currentWidth) + " finalX : " + (currentRegion.x + currentWidth + 1));
        if (right != null) {
            right.forEach((r) -> {
                int rheight = height / r.divisionFactor;
                if (r.y <= currentRegion.y + currentHeight && r.y + rheight >= currentRegion.y) {
                    adj.add(r);
                }
            });
        }
        // Select all adjacent regions up (above)
        List<QuadtreeRegion> upReg = finalMapRegionsByY.get(currentRegion.y + currentHeight);
        List<QuadtreeRegion> upNeighbours = finalMapRegionsByY.get(currentRegion.y + currentHeight + 1);
        List<QuadtreeRegion> up = fuse(upReg, upNeighbours);
        //System.out.println("upReg: " + (currentRegion.y + currentHeight) + "finalY : " + (currentRegion.y + currentHeight +1));
        if (up != null) {
            up.forEach((r) -> {
                int rwidth = width / r.divisionFactor;
                if (r.x <= currentRegion.x + currentWidth && r.x + rwidth >= currentRegion.x) {
                    adj.add(r);
                }
            });
        }
        // Select all adjacent regions down (below)
        List<QuadtreeRegion> downReg = finalMapRegionsByYPlusHeight.get(currentRegion.y);
        List<QuadtreeRegion> downNeighbours = finalMapRegionsByYPlusHeight.get(currentRegion.y - 1);
        List<QuadtreeRegion> down = fuse(downReg, downNeighbours);
        //System.out.println("downReg: " + currentRegion.y + " finalY+Height : " + (currentRegion.y -1));
        if (down != null) {
            down.forEach((r) -> {
                int rwidth = width / r.divisionFactor;
                if (r.x <= currentRegion.x + currentWidth && r.x + rwidth >= currentRegion.x) {
                    adj.add(r);
                }
            });
        }
        //System.out.println("Adjacents: " + adj.size());
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
                System.out.println("Region id=" + reg.id + " width=" + regwidth 
                   + " height=" + regheight + " x=" + reg.x + " y=" + reg.y);
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

    private static List<QuadtreeRegion> fuse(List<QuadtreeRegion> list1, List<QuadtreeRegion> list2) {
        List<QuadtreeRegion> newlist = new ArrayList<>();
        if (list1 == null) {
            return list2;
        }
        if (list2 == null) {
            return list1;
        }
        newlist.addAll(list1);
        newlist.addAll(list2);
        return newlist;
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
    public static QuadtreeRegion[] fathers;
}
