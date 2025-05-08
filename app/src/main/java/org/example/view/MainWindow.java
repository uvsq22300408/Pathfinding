package org.example.view;

import org.bytedeco.javacpp.Pointer;
import org.example.benchmark.Benchmark;
import org.example.benchmark.Benchmark3D;
import org.example.benchmark.benchmarkTriangulation;

import static com.raylib.Colors.BLACK;
import static com.raylib.Colors.DARKBLUE;
import static com.raylib.Colors.DARKPURPLE;
import static com.raylib.Colors.GREEN;
import static com.raylib.Colors.LIME;
import static com.raylib.Colors.MAGENTA;
import static com.raylib.Colors.ORANGE;
import static com.raylib.Colors.RAYWHITE;
import static com.raylib.Colors.RED;
import static com.raylib.Colors.YELLOW;
import static com.raylib.Raylib.BeginDrawing;
import static com.raylib.Raylib.BeginMode2D;
import static com.raylib.Raylib.ClearBackground;
import static com.raylib.Raylib.CloseWindow;
import static com.raylib.Raylib.DrawCircle;
import static com.raylib.Raylib.DrawCube;
import static com.raylib.Raylib.DrawCubeWires;
import static com.raylib.Raylib.DrawRectangle;
import static com.raylib.Raylib.DrawSphere;
import static com.raylib.Raylib.DrawSphereWires;
import static com.raylib.Raylib.EndDrawing;
import static com.raylib.Raylib.EndMode2D;
import static com.raylib.Raylib.GetMousePosition;
import static com.raylib.Raylib.InitWindow;
import static com.raylib.Raylib.IsKeyDown;
import static com.raylib.Raylib.KEY_DOWN;
import static com.raylib.Raylib.KEY_LEFT;
import static com.raylib.Raylib.KEY_PAGE_DOWN;
import static com.raylib.Raylib.KEY_PAGE_UP;
import static com.raylib.Raylib.KEY_RIGHT;
import static com.raylib.Raylib.KEY_UP;
import static com.raylib.Raylib.SetTargetFPS;
import static com.raylib.Raylib.Vector3;
import static com.raylib.Raylib.WindowShouldClose;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import javax.swing.Timer;

import org.example.world.examples.Astar;
import org.example.world.examples.Astar3D;
import org.example.world.examples.AstarGrid;
import org.example.world.examples.Dijkstra;
import org.example.world.examples.JPSGrid;
import org.example.world.examples.Quadtree;
import org.example.world.examples.Quadtree3D;
import org.example.world3D.LoadWorld3D;
import org.example.world3D.World3D;
import org.example.world3D.World3D.Region3D;
import org.example.world.LoadWorld;
import org.example.world.RandomWorld;
import org.example.world.SaveWorld;
import org.example.world.World;
import org.example.world.World.ERegionType;
import org.example.world.World.Region;

import com.raylib.Raylib.Camera2D;
import com.raylib.Raylib.Vector2;
import com.raylib.Raylib.Vector3;
public class MainWindow {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            return;
        } else {
            switch (args[0]) {
                // Generate benchmark graphs
                case "benchmark-generate": {
                    Benchmark.benchmarkGenerate();
                }
                break;
                case "draw-quadtree": {
                    if (args.length > 1) {
                        World world = LoadWorld.loadWorld(args[1]);
                        double distance = Quadtree.quadtree(world);
                        System.out.println("Quadtree distance = " + distance);
                        DrawQuadTree.drawQuadTree(world);
                    } else {
                        System.out.println("Un nom de graphe dans app/benchmark est requis.");
                    }
                }
                break;
                case "draw-dijkstra": {
                    if (args.length > 1) {
                        World world = LoadWorld.loadWorld(args[1]);
                        double longueur = Dijkstra.dijkstra(world);
                        main_draw(world, longueur);
                    } else {
                        System.out.println("Un nom de graphe dans app/benchmark est requis.");
                    }
                }
                break;
                case "draw-astar": {
                    if (args.length > 1) {
                        World world = LoadWorld.loadWorld(args[1]);
                        double longueur = AstarGrid.astar(world);
                        DrawAstar.drawAstar(world);
                    } else {
                        System.out.println("Un nom de graphe dans app/benchmark est requis.");
                    }
                }
                break;
                case "draw-jps": {
                    if (args.length > 1) {
                        World world = LoadWorld.loadWorld(args[1]);
                        double longueur = JPSGrid.JPS(world);
                        DrawJPS.drawJPS(world);
                    } else {
                        System.out.println("Un nom de graphe dans app/benchmark est requis.");
                    }
                }
                break;
                case "draw-triangulation": {
                    if (args.length > 1) {
                        World world = LoadWorld.loadWorld(args[1]);
                        int numPoints = 5;
                        // numPoints ?
                        if (args.length > 2) {
                            numPoints = intFromString(args[2]);
                        }
                        benchmarkTriangulation.benchmark(world, numPoints);
                        DrawTriangulation.drawTriangulation(world);
                    }
                }
                break;
                case "benchmark-run": {
                    Benchmark.benchmarkall();
                }
                break;
                case "benchmark-generate3d": {
                    Benchmark3D.benchmarkGenerate3D();
                }
                break;
                case "benchmark-run3d": {
                    Benchmark3D.benchmarkAll();
                }
                break;
                case "draw-astar3d": {
                    World3D world = LoadWorld3D.loadWorld(args[1]);
                    Astar3D.astar(world);
                    DrawAstar3D.drawAstar3D(world);
                }
                break;
                case "draw-quadtree3d": {
                    World3D world = LoadWorld3D.loadWorld(args[1]);
                    Quadtree3D.quadtree(world);
                    DrawQuadtree3D.drawQuadtree3D(world);
                }
                break;
                default:
                    System.out.println("arg " + args[0] + " not recognized.");
                    break;
            }
        }
    }

    public static void main_draw(World world, double longueur) {
        // Run un algo
        //double longueur = Dijkstra.dijkstra(world);
        Astar.astar(world);
        System.out.println("Longueur trouvée: " + longueur);

        InitWindow(800, 600, "Pathfinding");
        SetTargetFPS(60);
        Camera2D camera = new Camera2D();
        camera.fill(0);
        Vector2 targetVector = new Vector2();
        targetVector.x(world.start.x - 20);
        targetVector.y(world.start.y - 20);
        camera.target(targetVector);
        Vector2 cameraOffset = GetMousePosition();
        camera.offset(cameraOffset);
        camera.zoom(1.0f);

        while(!WindowShouldClose()) {
            if (IsKeyDown(KEY_UP)) targetVector.y(targetVector.y() - 10);
            if (IsKeyDown(KEY_DOWN)) targetVector.y(targetVector.y() + 10);
            if (IsKeyDown(KEY_LEFT)) targetVector.x(targetVector.x() - 10);
            if (IsKeyDown(KEY_RIGHT)) targetVector.x(targetVector.x() + 10);
            if (IsKeyDown(KEY_PAGE_DOWN)) camera.zoom(camera.zoom() - 0.05f);
            if (IsKeyDown(KEY_PAGE_UP)) camera.zoom(camera.zoom() + 0.05f);
            camera.target(targetVector);
            BeginDrawing();
            BeginMode2D(camera);
            ClearBackground(RAYWHITE);
            drawWorld(world);
            drawObstacles(world);
            if (longueur > 0) { // S'il y a un chemin
                drawPath(world);
            }
            EndMode2D();
            EndDrawing();
        }
        CloseWindow();
    }

    public static void drawWorld(World world) {
        switch(world.regiontype) {
            case ERegionType.OCTILE: {
                int tr = world.tailleReg;
                for(int x = 0; x < world.width; x += tr) {
                    for (int y = 0; y < world.height; y += tr) {
                        int type = world.passThrough[(x / tr) * (world.height / tr) + (y / tr)]; 
                        if (type == World.InnerWorld.OBSTACLE) {
                            DrawRectangle(x, y, Math.max(tr, 1), Math.max(tr, 1), YELLOW);
                        } else if (type == World.InnerWorld.START) {
                            DrawRectangle(x, y, Math.max(tr, 1), Math.max(tr, 1), DARKPURPLE);
                        } else if (x == world.destinationReg.x && y == world.destinationReg.y) {
                            DrawRectangle(x, y, Math.max(tr, 1), Math.max(tr, 1), LIME);
                        } else {
                            DrawRectangle(x, y, Math.max(tr, 1), Math.max(tr, 1), DARKBLUE);
                        }
                    }
                }
            }
        }
    }

    public static void drawWorld3D(World3D world) {
        Vector3 origin = new Vector3();
        origin.x(world.width / 2);
        origin.y(world.height / 2);
        origin.z(world.depth / 2);
        DrawCubeWires(origin, world.width, world.height, world.depth, YELLOW);
        //DrawCube(origin, world.width - 1, world.height - 1, world.depth - 1, MAGENTA);
        Vector3 startvec = new Vector3();
        startvec.x(world.startReg.x);
        startvec.y(world.startReg.y);
        startvec.z(world.startReg.z);
        DrawCube(startvec, world.tailleReg, world.tailleReg, world.tailleReg, RED);
        Vector3 destvec = new Vector3();
        destvec.x(world.destinationReg.x);
        destvec.y(world.destinationReg.y);
        destvec.z(world.destinationReg.z);
        DrawCube(destvec, world.tailleReg, world.tailleReg, world.tailleReg, GREEN);
    }

    public static void drawObstacles(World world) {
       for (World.Obstacle o : world.obstacles) {
            DrawCircle(Math.round(o.x), Math.round(o.y), o.radius, BLACK);
       }
    }

    public static void drawObstacles3D(World3D world) {
        for (World3D.Obstacle3D o : world.obstacles) {
            Vector3 center = new Vector3();
            center.x(Math.round(o.x));
            center.y(Math.round(o.y));
            center.z(Math.round(o.z));
            DrawSphereWires(center, o.radius, 3, 4, BLACK); 
            //DrawSphere(center, o.radius, ORANGE);
       }
    }

    public static void drawPath(World world) {
        switch (world.regiontype) {
            case ERegionType.OCTILE:
                Region dest = world.destinationReg;
                Region current = dest;
                int tailleRegion = world.tailleReg;
                while (!current.egaleA(world.startReg)) {
                    if (!current.egaleA(dest)) {
                        DrawRectangle(current.x, current.y, tailleRegion,
                        tailleRegion, RED);
                    }
                    // Current devient pere de current
                    int fatherId = world.passThrough[world.getRegionId(current)];
                    int fatherY = fatherId % world.heightInRegion();
                    int fatherX = (fatherId - fatherY) / world.heightInRegion();
                    current = new Region(fatherX * world.tailleReg, fatherY * tailleRegion, tailleRegion); 
                }
                break;
        
            default:
                break;
        }
    }

    public static void drawPath3D(World3D world) {
        Region3D dest = world.destinationReg;
        Region3D current = dest;
        int tailleRegion = world.tailleReg;
        System.out.println("drawPath3d: startx = " + world.startReg.x + " y=" + world.startReg.y
            + " z=" + world.startReg.z);
        while (!current.egaleA(world.startReg)) {
            if (!current.egaleA(dest)) {
                Vector3 currentPosition = new Vector3();
                currentPosition.x(current.x);
                currentPosition.y(current.y);
                currentPosition.z(current.z);
                DrawCube(currentPosition, tailleRegion, tailleRegion, tailleRegion, RED);
            }
            // Current devient pere de current
            int currentId = world.getRegionId(current);
            System.out.println("drawPath3d: currentId = " + currentId);
            if (currentId < 0) {
                break;
            }
            int fatherId = world.passThrough[currentId];

            int x = fatherId / (world.heightInRegion() * world.depthInRegion());
            int y = (fatherId - x * world.heightInRegion() * world.depthInRegion()) / world.depthInRegion();
            int z = fatherId % world.depthInRegion();
            current = new Region3D(x * tailleRegion, y * tailleRegion, z * tailleRegion,
                tailleRegion); 
            System.out.println("drawPath3d: currentx = " + current.x + " y=" + current.y + " z=" + current.z);
            System.out.println("current = startreg = " + current.egaleA(world.startReg));
        }
    }

    private static int intFromString(String s) {
        System.out.println("intFromString: " + s);
        int value = 0;
        for (char c : s.toCharArray()) {
            if (c >= '0' && c <= '9') {
                value *= 10;
                value += c - '0';
            }
        }
        return value;
    }
}
