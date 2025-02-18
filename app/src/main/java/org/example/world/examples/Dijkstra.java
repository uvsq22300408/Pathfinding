package org.example.world.examples;

import java.util.HashSet;
import java.util.Set;

import org.example.world.World;
import org.example.world.World.Region;

public class Dijkstra {
    // Retourne la longueur du chemin
    public static double dijkstra(World world) {
        final double Infinity = -1;
        Region start = world.startReg;
        Region destination = world.destinationReg;
        if (start.egaleA(destination)) {
            return 0;
        }
        boolean destinationFound = false;
        Set<Region> toVisit = new HashSet<>();
        int nbRegions = world.getNbRegion();
        int heightInRegion = world.heightInRegion();
        int widthInRegion = world.widthInRegion();
        double[] distance = new double[nbRegions];
        // Initialisation des distances
        for (int regx = 0; regx < widthInRegion; regx++) {
            for (int regy = 0; regy < heightInRegion; regy++) {
                if (regx * world.tailleReg == start.x && regy * world.tailleReg == start.y) {
                    distance[regx * heightInRegion + regy] = 0;
                }
                else {
                    distance[regx * heightInRegion + regy] = Infinity;
                }
            }
        }
        
        Region current = start;
        while (!destinationFound) {
            Set<Region> adjacents = world.adjacents(current);
            Region minRegion = null;
            double min = Infinity;
            int current_x = current.x / world.tailleReg;
            int current_y = current.y / world.tailleReg;
            double currentdist = distance[current_x * heightInRegion + current_y];
            // On update les distances prend la zone la plus proche
            for (Region reg : adjacents) {                
                int regx = reg.x / world.tailleReg;
                int regy = reg.y / world.tailleReg;
                // Distance pour aller jusqu'au voisin depuis start
                double dist = distance[regx * heightInRegion + regy];
                double newdist = currentdist + reg.distance;
                if (dist == Infinity || dist > newdist) {
                    distance[regx * heightInRegion + regy] = newdist;
                    // On ajoute le sommet dans les sommets a traiter, on le retire si deja present
                    toVisit.remove(reg);
                    toVisit.add(reg);
                    // Update pere
                    int fatherid = world.getRegionId(current);
                    world.setFather(regx, regy, fatherid);
                } 
            }
            for (Region reg : toVisit) {
                int regx = reg.x / world.tailleReg;
                int regy = reg.y / world.tailleReg;
                if (min == Infinity || distance[regx * heightInRegion + regy] < min) {
                    min = distance[regx * heightInRegion + regy];
                    minRegion = reg;
                }
            }
            if (minRegion == null) {
                // Aucun chemin possible, l'algorithme ne peut pas continuer
                return -1;
            }
            // Si minRegion = destination, on a trouve le plus court chemin
            if (minRegion.egaleA(destination)) {
                return minRegion.distance + currentdist;
            }
            current = minRegion;
            toVisit.remove(current);
        }
        return -1;
    }
}
