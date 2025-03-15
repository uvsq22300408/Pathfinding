package org.example.view;

import org.example.world.examples.Quadtree;
import org.example.world.examples.Quadtree.QuadtreeRegion;

import static com.raylib.Colors.RAYWHITE;
import static com.raylib.Colors.RED;
import static com.raylib.Colors.YELLOW;
import static com.raylib.Colors.BLUE;
import static com.raylib.Raylib.BeginDrawing;
import static com.raylib.Raylib.BeginMode2D;
import static com.raylib.Raylib.ClearBackground;
import static com.raylib.Raylib.CloseWindow;
import static com.raylib.Raylib.DrawCircle;
import static com.raylib.Raylib.DrawLine;
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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import com.raylib.Raylib.Camera2D;
import com.raylib.Raylib.Vector2;

import org.example.world.World;

public class DrawQuadTree {
    public static void drawQuadTree(World world) {
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

        Enumeration<ArrayList<QuadtreeRegion>> regions = 
            Quadtree.finalMapRegionsByX.elements();
        System.out.println("not empty ? = " + regions.hasMoreElements() );
        regions.asIterator().forEachRemaining(a -> {
            System.out.println("a.length = " + a.size());
            a.forEach(r -> {
                System.out.println("r.x = " + r.x + " r.y = " + r.y + " r.div = " + 
                    r.divisionFactor);
            });
        });
        System.out.println("not empty ? = " + regions.hasMoreElements());
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
            DrawRectangle(0,0, world.width, world.height, BLUE);
            regions = Quadtree.finalMapRegionsByX.elements();
            MainWindow.drawObstacles(world);
            regions.asIterator().forEachRemaining(array -> {
                array.forEach(r -> {
                    int w = world.width / r.divisionFactor;
                    int h = world.height / r.divisionFactor;
                    DrawLine(r.x, r.y, r.x + w, r.y, YELLOW);
                    DrawLine(r.x, r.y, r.x, r.y + h, YELLOW);
                    DrawLine(r.x, r.y + h, r.x  + w, r.y + h, YELLOW);
                    DrawLine(r.x + w, r.y, r.x + w, r.y + h, YELLOW);
                });
            });
            EndMode2D();
            EndDrawing();
        }
        CloseWindow();
        
    }
}
