package org.example.view;
import static com.raylib.Colors.BLACK;
import static com.raylib.Colors.DARKBLUE;
import static com.raylib.Colors.DARKPURPLE;
import static com.raylib.Colors.LIME;
import static com.raylib.Colors.RAYWHITE;
import static com.raylib.Colors.RED;
import static com.raylib.Colors.YELLOW;
import static com.raylib.Raylib.BeginDrawing;
import static com.raylib.Raylib.BeginMode2D;
import static com.raylib.Raylib.ClearBackground;
import static com.raylib.Raylib.CloseWindow;
import static com.raylib.Raylib.DrawCircle;
import static com.raylib.Raylib.DrawRectangle;
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

import org.example.world.examples.Dijkstra;
import org.example.View.SansGrille2D.Obstacle;
import org.example.world.LoadWorld;
import org.example.world.RandomWorld;
import org.example.world.SaveWorld;
import org.example.world.World;
import org.example.world.World.ERegionType;
import org.example.world.World.Region;

import com.raylib.Raylib.Camera2D;
import com.raylib.Raylib.Vector2;
public class MainWindow {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            World world = LoadWorld.loadWorld("example1");
            main_draw(world);
        } else {
            switch (args[0]) {
                case "benchmark-generate":
                    int[] Awidths = {100, 200, 500, 1000, 8000};
                    int[] Aheights = {100, 200, 500, 1000, 8000};
                    int[] Ataille_regions = {10, 20, 50, 100};
                    int[] Anb_obstacle = {3, 5, 10, 40};
                    for (int width_ix = 0; width_ix < Awidths.length; width_ix += 1) {
                        for (int height_ix = 0; height_ix < Aheights.length; height_ix += 1) {
                            for (int taille_ix = 0; taille_ix < Ataille_regions.length; taille_ix += 1) {
                                for (int obs_ix = 0; obs_ix < Anb_obstacle.length; obs_ix += 1) {
                                    int width = Awidths[width_ix];
                                    int height = Aheights[height_ix];
                                    int nb_obstacles = Anb_obstacle[obs_ix];
                                    int taille_region = Ataille_regions[taille_ix];
                                    if (width <= 2 * taille_region || height <= 2 * taille_region) {
                                        continue;
                                    }
                                    World world = RandomWorld
                                        .randomWorld(width, height, nb_obstacles, taille_region);
                                    SaveWorld.saveWorld(world, 
                                        "benchw" + width + "h" + height + "o" + nb_obstacles 
                                        + "t" + taille_region);
                                }
                            }
                        }
                    }
                    break;
                case "draw": {
                    World world = LoadWorld.loadWorld(args[1]);
                    main_draw(world);
                    }
                    break;
                case "benchmark-run": {
                    try {
                        Path file = Path.of("benchmark-results.csv");
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile(),
                            false));
                        File dossier = new File(SaveWorld.SAVE_LOCATION);
                        File[] graphes = dossier.listFiles();
                        int nbGraphes = graphes.length;
                        // Ajouter les autres algorithmes
                        writer.write("nomGraphe,DijkstraLength,DijkstraTimeMs\n");
                        for (int gx = 0; gx < nbGraphes; gx++) {
                            System.out.println("opening: " + graphes[gx].getName());
                            World world = LoadWorld.loadWorld(graphes[gx].getName());
                            Instant before = Instant.now();
                            double longueur = Dijkstra.dijkstra(world);
                            Instant after = Instant.now();
                            long timeElapsed = Duration.between(before, after).toMillis();
                            writer.write(graphes[gx].getName() + ",");
                            if (longueur <= 0) {
                                writer.write("-1,"); // => Pas de chemin
                            }
                            else {
                                writer.write(longueur + ",");
                            }
                            writer.write(timeElapsed + ",");
                            // Autres algos
                            //...
                            writer.write("\n");
                            writer.flush();
                        }
                        writer.close();
                    } catch (Exception e) {
                        System.err.println("main: ne peut pas ouvrir benchmark-results.csv");
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    public static void main_draw(World world) {
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

        // Run un algo
        double longueur = Dijkstra.dijkstra(world);
        System.out.println("Longueur trouvée: " + longueur);

        
        
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

    public static void drawObstacles(World world) {
       for (World.Obstacle o : world.obstacles) {
            DrawCircle(Math.round(o.x), Math.round(o.y), o.radius, BLACK);
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
}
