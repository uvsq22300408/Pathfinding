package org.example.world.examples;

import java.nio.channels.Pipe.SourceChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.example.world.*;
import org.example.world.World.Region;

public class Astar {
    final static double Infinity = -1;
    public static double astar(World world) {
        int nbRegions = world.getNbRegion();
        Region start = world.startReg;
        Region destination = world.destinationReg;

        List<World.Region> openSet = new ArrayList<>();
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
            //System.out.println("iter no : " + iteri);
            //System.out.flush();
            Region current = getMinFScoreRegion(fScore, openSet, world);
            //System.out.println("current id = " + world.getRegionId(current));
            if (current == null) {
                //System.out.println("Pas de min trouve !");
                //System.out.flush();
                return -1; // Pas de chemin.
            }
            if (current.egaleA(destination)) {
                //System.out.println("destination found !");
                //System.out.flush();
                return gScore[world.getRegionId(destination)];
            }
            openSet.remove(current);
            Set<Region> adjacents = world.adjacents(current);
            //System.out.println("nb adjacents = " + adjacents.size());
            for (Region adj : adjacents) {
                double tentativeGScore = gScore[world.getRegionId(current)] + adj.distance;
                //System.out.println("tentativeGScore = " + tentativeGScore);
                if (gScore[world.getRegionId(adj)] == Infinity 
                        || tentativeGScore < gScore[world.getRegionId(adj)]) {
                    world.setFather(adj.x / world.tailleReg, adj.y / world.tailleReg,
                         world.getRegionId(current));
                    gScore[world.getRegionId(adj)] = tentativeGScore;
                    fScore[world.getRegionId(adj)] = tentativeGScore + h(adj, destination);
                    if (!openSet.contains(adj)) {
                        openSet.add(adj);
                    }
                }
            }
        }
        return -1; // OpenSet empty but no path found.
    }

    public static double h(Region start, Region dest) {
        // Return heuristic distance between start and dest.
        // Here, euclidean distance
        return Math.sqrt(Math.abs(dest.x - start.x) + Math.abs(dest.y - start.y));
    }

    public static Region getMinFScoreRegion(double[] fScore, List<Region> openSet, World world) {
        double min = Infinity;
        Region minreg = null;
        for (Region reg : openSet) {
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
