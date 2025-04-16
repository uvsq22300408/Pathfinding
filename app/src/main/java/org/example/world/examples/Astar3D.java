package org.example.world.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.example.world3D.World3D;
import org.example.world3D.World3D.Region3D;

public class Astar3D {
    final static double Infinity = -1;
    public static double astar(World3D world) {
        int nbRegions = world.getNbRegion();
        Region3D start = world.startReg;
        Region3D destination = world.destinationReg;

        List<World3D.Region3D> openSet = new ArrayList<>();
        openSet.add(start);
        double[] gScore = new double[nbRegions];
        double[] fScore = new double[nbRegions];
        
        for (int rx = 0; rx < nbRegions; rx++) {
            gScore[rx] = Infinity;
            fScore[rx] = Infinity;
        }
        gScore[world.getRegionId(start)] = 0;
        fScore[world.getRegionId(start)] = h(start, destination);
        int iteri = 0;
        while(!openSet.isEmpty()) {
            Region3D current = getMinFScoreRegion(fScore, openSet, world);
            if (current == null) {
                return -1; // Pas de chemin.
            }
            //System.out.println("astar3d: current = " + current.x + "," + current.y 
            //    + "," + current.z + " id= " + world.getRegionId(current));
            if (current.egaleA(destination)) {
                System.out.println("astar3d: destination found, id = " 
                    + world.getRegionId(destination));
                return gScore[world.getRegionId(destination)];
            }
            openSet.remove(current);
            Set<Region3D> adjacents = world.adjacents(current);
            //System.out.println("astar3d : there are " + adjacents.size() + " adj");
            for (Region3D adj : adjacents) {
                double tentativeGScore = gScore[world.getRegionId(current)] + adj.distance;
                if (gScore[world.getRegionId(adj)] == Infinity 
                        || tentativeGScore < gScore[world.getRegionId(adj)]) {
                    world.setFather(adj.x / world.tailleReg, adj.y / world.tailleReg,
                        adj.z / world.tailleReg, world.getRegionId(current));
                    gScore[world.getRegionId(adj)] = tentativeGScore;
                    fScore[world.getRegionId(adj)] = tentativeGScore + h(adj, destination);
                    if (!openSet.contains(adj)) {
                        openSet.add(adj);
                    }
                }
            }
        }
        System.out.println("astar3d: no path found");
        return -1; // OpenSet empty but no path found.
    }

    public static double h(Region3D start, Region3D dest) {
        // Return heuristic distance between start and dest.
        // Here, euclidean distance
        float dx = dest.x - start.x;
        float dy = dest.y - start.y;
        float dz = dest.z - start.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static Region3D getMinFScoreRegion(double[] fScore, List<Region3D> openSet, World3D world) {
        double min = Infinity;
        Region3D minreg = null;
        for (Region3D reg : openSet) {
            int index = world.getRegionId(reg);
            double currentFScore = fScore[index];
            if (currentFScore == Infinity) {
                continue;
            }
            if (min == Infinity || min > currentFScore) {
                min = currentFScore;
                minreg = reg;
            }
        }
        return minreg;
    }
}
