package org.example.world.examples;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.example.world3D.*;
import org.example.world3D.World3D.Obstacle3D;


public class Quadtree3D {
    public static double Infinity = -1;
    public static int MAX_DIVISION = pow(2, 6);

    public static double quadtree(World3D world) {
        // Divide regions in 8 if they contain an obstacle
        // until they get to a minimum size
        startRegion = null;
        endRegion = null;
        Quadtree3D.regionId = 0;
        QuadtreeRegion3D reg = new QuadtreeRegion3D(0, 0, 0, 1);
        reg.containsObstacle = containsObstacle(world, reg);
        Hashtable<Integer, ArrayList<QuadtreeRegion3D>> mapRegionsByX = new Hashtable<>();
        Hashtable<Integer, ArrayList<QuadtreeRegion3D>> mapRegionsByY = new Hashtable<>();
        Hashtable<Integer, ArrayList<QuadtreeRegion3D>> mapRegionsByZ = new Hashtable<>();
        Hashtable<Integer, ArrayList<QuadtreeRegion3D>> mapRegionsByXPlusWidth = new Hashtable<>();
        Hashtable<Integer, ArrayList<QuadtreeRegion3D>> mapRegionsByYPlusHeight = new Hashtable<>();
        Hashtable<Integer, ArrayList<QuadtreeRegion3D>> mapRegionsByZPlusDepth = new Hashtable<>();
        divideRegion(world, reg, mapRegionsByX, mapRegionsByY, mapRegionsByZ, mapRegionsByXPlusWidth,
             mapRegionsByYPlusHeight, mapRegionsByZPlusDepth, 1);
        finalMapRegionsByX = mapRegionsByX;
        finalMapRegionsByY = mapRegionsByY;
        finalMapRegionsByZ = mapRegionsByZ;
        finalMapRegionsByXPlusWidth = mapRegionsByXPlusWidth;
        finalMapRegionsByYPlusHeight = mapRegionsByYPlusHeight;
        finalMapRegionsByZPlusDepth = mapRegionsByZPlusDepth;
        if (startRegion == null || endRegion == null) {
            System.out.println("Error: Quadtree3D -> start or end was not found.");
            return -1;
        }
        // Run A* to find a path along regions centers
        Quadtree3D.fathers = new QuadtreeRegion3D[Quadtree3D.regionId];
        fathers[startRegion.id] = null;
        List<QuadtreeRegion3D> openSet = new ArrayList<>();
        openSet.add(startRegion);
        double[] gScore = new double[Quadtree3D.regionId];
        double[] fScore = new double[Quadtree3D.regionId];
        
        for (int rx = 0; rx < Quadtree3D.regionId; rx++) {
            gScore[rx] = Infinity;
            fScore[rx] = Infinity;
        }
        gScore[startRegion.id] = 0;
        fScore[startRegion.id] = h(startRegion, world);
        while(!openSet.isEmpty()) {
            QuadtreeRegion3D current = getMinFScoreRegion(fScore, openSet, world);
            if (current == null) {
                return -1; // Pas de chemin.
            }
            if (current.id == endRegion.id) { 
                return gScore[endRegion.id];
            }
            openSet.remove(current);
            Set<QuadtreeRegion3D> adjacents = adjacents(current, world.width, world.height, world.depth);
            for (QuadtreeRegion3D adj : adjacents) {
                // Calculate distance between currentRegion and adj
                double distance = 0;
                int adjwidth = world.width / adj.divisionFactor;
                int adjheight = world.height / adj.divisionFactor;
                int adjdepth = world.depth / adj.divisionFactor;
                double adjCenterX = adj.x + (adjwidth / 2.0);
                double adjCenterY = adj.y + (adjheight / 2.0);
                double adjCenterZ = adj.z + (adjdepth / 2.0);
                if (adj.id == endRegion.id) {
                    adjCenterX = world.destination.x;
                    adjCenterY = world.destination.y;
                    adjCenterZ = world.destination.z;
                }
                if (current.id == startRegion.id) {
                    // Return distance between start and neighbor's center
                    double dx = adjCenterX - world.start.x;
                    double dy = adjCenterY - world.start.y;
                    double dz = adjCenterZ - world.start.z;
                    distance = Math.sqrt(dx * dx + dy *  dy + dz * dz);
                } else {
                    double currentRegWidth = world.width / current.divisionFactor;
                    double currentRegHeight = world.height / current.divisionFactor;
                    double currentRegDepth = world.depth / current.divisionFactor;
                    double currentCenterX = current.x + (currentRegWidth / 2.0);
                    double currentCenterY = current.y + (currentRegHeight / 2.0);
                    double currentCenterZ = current.z + (currentRegDepth / 2.0);
                    double dx = adjCenterX - currentCenterX;
                    double dy = adjCenterY - currentCenterY;
                    double dz = adjCenterZ - currentCenterZ;
                    distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                }
                double tentativeGScore = gScore[current.id] + distance;
                if (gScore[adj.id] == Infinity || tentativeGScore < gScore[adj.id]) { 
                    Quadtree3D.fathers[adj.id] = current;
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
    private static double h(QuadtreeRegion3D start, World3D world) {
        // Return heuristic distance between start and world dest.
        int startwidth = world.width / start.divisionFactor;
        int startheight = world.height / start.divisionFactor;
        int startdepth = world.depth / start.divisionFactor;
        int startCenterX = start.x + startwidth / 2;
        int startCenterY = start.y + startheight / 2;
        int startCenterZ = start.z + startdepth / 2;
        double dx = world.destination.x - startCenterX;
        double dy = world.destination.y - startCenterY;
        double dz = world.destination.z - startCenterZ;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /** Return minreg, the region having the smallest fscore,
     * or null if no suitable candidate. */ 
    private static QuadtreeRegion3D getMinFScoreRegion(double[] fScore, List<QuadtreeRegion3D> openSet, World3D world) {
        double min = Infinity;
        QuadtreeRegion3D minreg = null;
        for (QuadtreeRegion3D openReg: openSet) {
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
    public static Set<QuadtreeRegion3D> adjacents(QuadtreeRegion3D currentRegion, int width, int height, int depth) {
        int currentWidth = width / currentRegion.divisionFactor;
        int currentHeight = height / currentRegion.divisionFactor;
        int currentDepth = depth / currentRegion.divisionFactor;
        Set<QuadtreeRegion3D> adj = new LinkedHashSet<>();
        // Select all adjacent regions left
        List<QuadtreeRegion3D> leftReg = finalMapRegionsByXPlusWidth.get(currentRegion.x);
        List<QuadtreeRegion3D> leftNeighbours = finalMapRegionsByXPlusWidth.get(currentRegion.x - 1);
        List<QuadtreeRegion3D> left = fuse(leftReg, leftNeighbours);
        //System.out.println("====== Region" + currentRegion.id);
        //System.out.println("leftReg: " + currentRegion.x + " finalX+Width : " + (currentRegion.x -1));
        if (left != null) {
            left.forEach((r) -> {
                int rheight = height / r.divisionFactor;
                int rdepth = depth / r.divisionFactor;
                if (r.y <= currentRegion.y + currentHeight && r.y + rheight >= currentRegion.y &&
                     r.z <= currentRegion.z + currentDepth && r.z + rdepth >= currentRegion.z) {
                    adj.add(r);
                }
            });
        }
        // Select all adjacent regions right
        List<QuadtreeRegion3D> rightReg = finalMapRegionsByX.get(currentRegion.x + currentWidth);
        List<QuadtreeRegion3D> rightNeighbours = finalMapRegionsByX.get(currentRegion.x + currentWidth + 1);
        List<QuadtreeRegion3D> right = fuse(rightReg, rightNeighbours);
        //System.out.println("rightReg: " + (currentRegion.x + currentWidth) + " finalX : " + (currentRegion.x + currentWidth + 1));
        if (right != null) {
            right.forEach((r) -> {
                int rheight = height / r.divisionFactor;
                int rdepth = depth / r.divisionFactor;
                if (r.y <= currentRegion.y + currentHeight && r.y + rheight >= currentRegion.y &&
                     r.z <= currentRegion.z + currentDepth && r.z + rdepth >= currentRegion.z) {
                    adj.add(r);
                }
            });
        }
        // Select all adjacent regions up (above)
        List<QuadtreeRegion3D> upReg = finalMapRegionsByY.get(currentRegion.y + currentHeight);
        List<QuadtreeRegion3D> upNeighbours = finalMapRegionsByY.get(currentRegion.y + currentHeight + 1);
        List<QuadtreeRegion3D> up = fuse(upReg, upNeighbours);
        //System.out.println("upReg: " + (currentRegion.y + currentHeight) + "finalY : " + (currentRegion.y + currentHeight +1));
        if (up != null) {
            up.forEach((r) -> {
                int rwidth = width / r.divisionFactor;
                int rdepth = depth / r.divisionFactor;
                if (r.x <= currentRegion.x + currentWidth && r.x + rwidth >= currentRegion.x &&
                     r.z <= currentRegion.z + currentDepth && r.z + rdepth >= currentRegion.z) {
                    adj.add(r);
                }
            });
        }
        // Select all adjacent regions down (below)
        List<QuadtreeRegion3D> downReg = finalMapRegionsByYPlusHeight.get(currentRegion.y);
        List<QuadtreeRegion3D> downNeighbours = finalMapRegionsByYPlusHeight.get(currentRegion.y - 1);
        List<QuadtreeRegion3D> down = fuse(downReg, downNeighbours);
        //System.out.println("downReg: " + currentRegion.y + " finalY+Height : " + (currentRegion.y -1));
        if (down != null) {
            down.forEach((r) -> {
                int rwidth = width / r.divisionFactor;
                int rdepth = depth / r.divisionFactor;
                if (r.x <= currentRegion.x + currentWidth && r.x + rwidth >= currentRegion.x &&
                        r.z <= currentRegion.z + currentDepth && r.z + rdepth >= currentRegion.z) {
                    adj.add(r);
                }
            });
        }
        // Select all "foreground" adjacent regions
        List<QuadtreeRegion3D> foreReg = finalMapRegionsByZPlusDepth.get(currentRegion.z);
        List<QuadtreeRegion3D> foreNeighbours = finalMapRegionsByZPlusDepth.get(currentRegion.z - 1);
        List<QuadtreeRegion3D> foreground = fuse(foreReg, foreNeighbours);
        if (foreground != null) {
            foreground.forEach((r) -> {
                int rwidth = width / r.divisionFactor;
                int rheight = height / r.divisionFactor;
                if (r.x <= currentRegion.x + currentWidth && r.x + rwidth >= currentRegion.x &&
                    r.y <= currentRegion.y + currentHeight && r.y + rheight >= currentRegion.y) {
                        adj.add(r);
                    }
            });
        }
        // Select all "background" adjacent regions
        List<QuadtreeRegion3D> backReg = finalMapRegionsByZ.get(currentRegion.z + currentDepth);
        List<QuadtreeRegion3D> backNeighbours = finalMapRegionsByZ.get(currentRegion.z + currentDepth + 1);
        List<QuadtreeRegion3D> background = fuse(backReg, backNeighbours);
        if (background != null) {
            background.forEach((r) -> {
                int rwidth = width / r.divisionFactor;
                int rheight = height / r.divisionFactor;
                if (r.x <= currentRegion.x + currentWidth && r.x + rwidth >= currentRegion.x &&
                    r.y <= currentRegion.y + currentHeight && r.y + rheight >= currentRegion.y) {
                        adj.add(r);
                    }
            });
        }
        //System.out.println("Adjacents: " + adj.size());
        return adj;
    }

    /** Returns true if the region contains an obstacle. */
    public static boolean containsObstacle(World3D world, QuadtreeRegion3D reg) {
        // Check if an obstacle is in or overrun the region.
        List<Obstacle3D> obstacles = world.obstacles;
        int regwidth = world.width / reg.divisionFactor;
        int regheight = world.height / reg.divisionFactor;
        int regdepth = world.depth / reg.divisionFactor;
        for (Obstacle3D o : obstacles) {
            if (o.x - o.radius < reg.x + regwidth && o.x + o.radius > reg.x) {
                if (o.y - o.radius < reg.y + regheight && o.y + o.radius > reg.y) {
                    if (o.z - o.radius < reg.z + regdepth && o.z + o.radius > reg.z) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Divide regions and set Start and Endpoints
    public static void divideRegion(World3D world, QuadtreeRegion3D reg, 
        Hashtable<Integer, ArrayList<QuadtreeRegion3D>> mapRegionsByX, 
        Hashtable<Integer, ArrayList<QuadtreeRegion3D>> mapRegionsByY,
        Hashtable<Integer, ArrayList<QuadtreeRegion3D>> mapRegionsByZ,
        Hashtable<Integer, ArrayList<QuadtreeRegion3D>> mapRegionsByXPlusWidth,
        Hashtable<Integer, ArrayList<QuadtreeRegion3D>> mapRegionsByYPlusHeight,
        Hashtable<Integer, ArrayList<QuadtreeRegion3D>> mapRegionsByZPlusDepth,
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
                int depth = world.depth / divisionFactor;
                QuadtreeRegion3D x0y0z0 = new QuadtreeRegion3D(reg.x, reg.y, reg.z, divisionFactor);
                QuadtreeRegion3D x0y0z1 = new QuadtreeRegion3D(reg.x, reg.y, reg.z + depth, divisionFactor);
                QuadtreeRegion3D x0y1z0 = new QuadtreeRegion3D(reg.x, reg.y + height, reg.z,
                    divisionFactor);
                QuadtreeRegion3D x0y1z1 = new QuadtreeRegion3D(reg.x, reg.y + height, reg.z + depth,
                    divisionFactor);
                QuadtreeRegion3D x1y0z0 = new QuadtreeRegion3D(reg.x + width, reg.y, reg.z,
                    divisionFactor);
                QuadtreeRegion3D x1y0z1 = new QuadtreeRegion3D(reg.x + width, reg.y, reg.z + depth,
                    divisionFactor);
                QuadtreeRegion3D x1y1z0 = new QuadtreeRegion3D(reg.x + width, reg.y + height, reg.z,
                    divisionFactor);
                QuadtreeRegion3D x1y1z1 = new QuadtreeRegion3D(reg.x + width, reg.y + height, reg.z + depth,
                    divisionFactor);
                x0y0z0.containsObstacle = containsObstacle(world, x0y0z0);
                x0y0z1.containsObstacle = containsObstacle(world, x0y0z1);
                x0y1z0.containsObstacle = containsObstacle(world, x0y1z0);
                x0y1z1.containsObstacle = containsObstacle(world, x0y1z1);
                x1y0z0.containsObstacle = containsObstacle(world, x1y0z0);
                x1y0z1.containsObstacle = containsObstacle(world, x1y0z1);
                x1y1z0.containsObstacle = containsObstacle(world, x1y1z0);
                x1y1z1.containsObstacle = containsObstacle(world, x1y1z1);
                divideRegion(world, x0y0z0, mapRegionsByX, 
                    mapRegionsByY, mapRegionsByZ, mapRegionsByXPlusWidth, mapRegionsByYPlusHeight, mapRegionsByZPlusDepth,
                    divisionFactor);
                divideRegion(world, x0y0z1, mapRegionsByX, 
                    mapRegionsByY, mapRegionsByZ, mapRegionsByXPlusWidth, mapRegionsByYPlusHeight, mapRegionsByZPlusDepth,
                    divisionFactor);
                divideRegion(world, x0y1z0, mapRegionsByX, 
                    mapRegionsByY, mapRegionsByZ, mapRegionsByXPlusWidth, mapRegionsByYPlusHeight, mapRegionsByZPlusDepth,
                    divisionFactor);
                divideRegion(world, x0y1z1, mapRegionsByX, 
                    mapRegionsByY, mapRegionsByZ, mapRegionsByXPlusWidth, mapRegionsByYPlusHeight, mapRegionsByZPlusDepth,
                    divisionFactor);
                divideRegion(world, x1y0z0, mapRegionsByX, 
                    mapRegionsByY, mapRegionsByZ, mapRegionsByXPlusWidth, mapRegionsByYPlusHeight, mapRegionsByZPlusDepth,
                    divisionFactor);
                divideRegion(world, x1y0z1, mapRegionsByX, 
                    mapRegionsByY, mapRegionsByZ, mapRegionsByXPlusWidth, mapRegionsByYPlusHeight, mapRegionsByZPlusDepth,
                    divisionFactor);
                divideRegion(world, x1y1z0, mapRegionsByX, 
                    mapRegionsByY, mapRegionsByZ, mapRegionsByXPlusWidth, mapRegionsByYPlusHeight, mapRegionsByZPlusDepth,
                    divisionFactor);
                divideRegion(world, x1y1z1, mapRegionsByX, 
                    mapRegionsByY, mapRegionsByZ, mapRegionsByXPlusWidth, mapRegionsByYPlusHeight, mapRegionsByZPlusDepth,
                    divisionFactor);
            }
            // If reg does not contain an obstacle, it is added to the map of known
            // regions with an Id
            else {
                reg.id = Quadtree3D.regionId;
                Quadtree3D.regionId += 1;
                int regwidth = world.width / reg.divisionFactor;
                int regheight = world.height / reg.divisionFactor;
                int regdepth = world.depth / reg.divisionFactor;
                //System.out.println("Region id=" + reg.id + " width=" + regwidth 
                //   + " height=" + regheight + " x=" + reg.x + " y=" + reg.y);
                ArrayList<QuadtreeRegion3D> regionsByX = mapRegionsByX.get(reg.x);
                ArrayList<QuadtreeRegion3D> regionsByY = mapRegionsByY.get(reg.y);
                ArrayList<QuadtreeRegion3D> regionsByZ = mapRegionsByZ.get(reg.z);
                ArrayList<QuadtreeRegion3D> regionsByXPlusWidth = mapRegionsByXPlusWidth.get(reg.x + regwidth);
                ArrayList<QuadtreeRegion3D> regionsByYPlusHeight = mapRegionsByYPlusHeight.get(reg.y + regheight);
                ArrayList<QuadtreeRegion3D> regionsByZPlusDepth = mapRegionsByZPlusDepth.get(reg.z + regdepth);
                if (regionsByX == null) {
                    ArrayList<QuadtreeRegion3D> newRegionsByX = new ArrayList<>();
                    newRegionsByX.add(reg);
                    mapRegionsByX.put(reg.x, newRegionsByX);
                } else {
                    regionsByX.add(reg);
                } if (regionsByY == null) {
                    ArrayList<QuadtreeRegion3D> newRegionsByY = new ArrayList<>();
                    newRegionsByY.add(reg);
                    mapRegionsByY.put(reg.y, newRegionsByY);
                } else {
                    regionsByY.add(reg);
                } if (regionsByZ == null) {
                    ArrayList<QuadtreeRegion3D> newRegionsByZ = new ArrayList<>();
                    newRegionsByZ.add(reg);
                    mapRegionsByZ.put(reg.z, newRegionsByZ);
                } else {
                    regionsByZ.add(reg);
                } if (regionsByXPlusWidth == null) {
                    ArrayList<QuadtreeRegion3D> newRegionsByXPlusWidth = new ArrayList<>();
                    newRegionsByXPlusWidth.add(reg);
                    mapRegionsByXPlusWidth.put(reg.x + regwidth, newRegionsByXPlusWidth);
                } else {
                    regionsByXPlusWidth.add(reg);
                } if (regionsByYPlusHeight == null) {
                    ArrayList<QuadtreeRegion3D> newRegionsByYPlusHeight = new ArrayList<>();
                    newRegionsByYPlusHeight.add(reg);
                    mapRegionsByYPlusHeight.put(reg.y + regheight, newRegionsByYPlusHeight);
                } else {
                    regionsByYPlusHeight.add(reg);
                } if (regionsByZPlusDepth == null) {
                    ArrayList<QuadtreeRegion3D> newRegionsByZPlusDepth = new ArrayList<>();
                    newRegionsByZPlusDepth.add(reg);
                    mapRegionsByZPlusDepth.put(reg.z + regdepth, newRegionsByZPlusDepth);
                } else {
                    regionsByZPlusDepth.add(reg);
                }
                // If reg contains start or end, save them as startRegion or EndRegion
                if (startRegion == null && world.start.x >= reg.x 
                    && world.start.x <= reg.x + regwidth) {
                    if (world.start.y >= reg.y &&
                         world.start.y <= reg.y + regheight) {
                            if (world.start.z >= reg.z &&
                             world.start.z <= reg.z + regdepth) {
                                startRegion = reg;
                            }
                    }
                }
                if (endRegion == null && world.destination.x >= reg.x 
                    && world.destination.x <= reg.x + regwidth) {
                    if (world.destination.y >= reg.y &&
                         world.destination.y <= reg.y + regheight) {
                            if (world.destination.z >= reg.z &&
                             world.destination.z <= reg.z + regdepth) {
                                endRegion = reg;
                            }
                    }
                }
            }
    }

    private static List<QuadtreeRegion3D> fuse(List<QuadtreeRegion3D> list1, List<QuadtreeRegion3D> list2) {
        List<QuadtreeRegion3D> newlist = new ArrayList<>();
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

    public static class QuadtreeRegion3D {
        public QuadtreeRegion3D(int x, int y, int z, int divisionFactor) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.divisionFactor = divisionFactor;
            this.containsObstacle = false;
        }

        public int id;
        public int x;
        public int y;
        public int z;
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

    public static Hashtable<Integer, ArrayList<QuadtreeRegion3D>> finalMapRegionsByX;
    public static Hashtable<Integer, ArrayList<QuadtreeRegion3D>> finalMapRegionsByXPlusWidth;
    public static Hashtable<Integer, ArrayList<QuadtreeRegion3D>> finalMapRegionsByY;
    public static Hashtable<Integer, ArrayList<QuadtreeRegion3D>> finalMapRegionsByYPlusHeight;
    public static Hashtable<Integer, ArrayList<QuadtreeRegion3D>> finalMapRegionsByZ;
    public static Hashtable<Integer, ArrayList<QuadtreeRegion3D>> finalMapRegionsByZPlusDepth;
    public static QuadtreeRegion3D startRegion;
    public static QuadtreeRegion3D endRegion;
    public static int regionId;
    public static QuadtreeRegion3D[] fathers;
}
