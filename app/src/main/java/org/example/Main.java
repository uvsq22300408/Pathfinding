package org.example;

import org.example.view.MainWindow;
import org.example.view.Grille2D.GrillePathfinding;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        for (var a : args) {
            System.out.println(a);
        }
        usage();
        String programName = "grille";
        if (args.length > 0 && 
            (args[0].compareTo("world") == 0 || args[0].compareTo("sansGrille") == 0)) {
                System.out.println("args detected");
                programName = args[0];
                System.out.println("programName = " + programName);
        }
        if (programName.compareTo("world") == 0) {
            if (args.length > 1) {
                String[] options = Arrays.copyOfRange(args, 1, args.length);
                MainWindow.main(options);
            }
            else {
                System.out.println("'world' prend au moins une option.");
            }
        } else if (programName.compareTo("grille") == 0) {
            GrillePathfinding.main(args);
        }
    }

    private static void usage() {
        System.out.println("usage: [program] [options]");
        System.out.println(" PROGRAM : ");
        System.out.println("  world : raylib rendering");
        System.out.println("  grille : 2D grid Swing");
        System.out.println("  sansGrille : Swing without grid");
        System.out.println(" OPTIONS WORLD:");
        System.out.println("  === Benchmark");
        System.out.println("  benchmark-generate: generate random graphs in benchmark folder");
        System.out.println("  benchmark-run: Run all algorithms against the graphs in app/benchmark/");
        System.out.println("  === Draw Algorithm");
        System.out.println("  draw-astar GRAPHNAME : execute A* on GRAPHNAME from app/benchmarks/");
        System.out.println("  draw-dijkstra GRAPHNAME : execute dijkstra on GRAPHNAME from app/benchmarks/");
        System.out.println("  draw-quadtree GRAPHNAME : execute quadtree on GRAPHNAME from app/benchmarks/");
        System.out.println("  draw-triangulation GRAPHNAME [NUMPOINTS = 5] : execute triangulation on GRAPHNAME with NUMPOINTS numpoints");
        System.out.println("  === Benchmark3D");
        System.out.println("  benchmark-generate3d: generate random graphs in benchmark folder");
        System.out.println("  benchmark-run3d: Run all algorithms against the graphs in app/benchmark/");
        System.out.println("  === Draw Algorithm");
        System.out.println("  draw-astar3d GRAPHLOCATION : execute A* on GRAPHLOCATION");
        System.out.println("  draw-dijkstra3d GRAPHLOCATION : execute dijkstra on GRAPHLOCATION");
        System.out.println("  draw-quadtree3d GRAPHLOCATION : execute quadtree on GRAPHLOCATION");
        System.out.println(" OPTIONS GRILLE:");
        System.out.println(" OPTIONS SANS_GRILLE:");
    }
}
