package org.example.view;

import static com.raylib.Colors.RAYWHITE;
import static com.raylib.Colors.RED;
import static com.raylib.Colors.WHITE;
import static com.raylib.Colors.YELLOW;
import static com.raylib.Colors.BLUE;
import static com.raylib.Colors.LIME;
import static com.raylib.Colors.PURPLE;
import static com.raylib.Colors.BLACK;
import static com.raylib.Raylib.BeginDrawing;
import static com.raylib.Raylib.BeginMode2D;
import static com.raylib.Raylib.ClearBackground;
import static com.raylib.Raylib.CloseWindow;
import static com.raylib.Raylib.DrawCircle;
import static com.raylib.Raylib.DrawLine;
import static com.raylib.Raylib.DrawRectangle;
import static com.raylib.Raylib.DrawTriangle;
import static com.raylib.Raylib.DrawTriangleLines;
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

import java.awt.Point;
import java.util.List;

import org.example.Triangulation.Triangle;
import org.example.benchmark.benchmarkTriangulation;
import org.example.world.World;
import org.example.world.examples.AstarGrid;

import com.raylib.Raylib.Camera2D;
import com.raylib.Raylib.Vector2;


public class DrawTriangulation {
    public static void drawTriangulation(World world) {
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

        List<Triangle> triangles = benchmarkTriangulation.triangles;
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
            MainWindow.drawWorld(world);
            MainWindow.drawObstacles(world);
            for(Triangle t : triangles) {
                List<Point> points = t.getPoints();
                Vector2 pointA = new Vector2();
                Vector2 pointB = new Vector2();
                Vector2 pointC = new Vector2();
                pointA.x(points.get(0).x);
                pointA.y(points.get(0).y);
                pointB.x(points.get(1).x);
                pointB.y(points.get(1).y);
                pointC.x(points.get(2).x);
                pointC.y(points.get(2).y);
                DrawTriangleLines(pointA, pointB, pointC, BLACK);
            }
            // Draw path
            List<Point> path = benchmarkTriangulation.path;
            for (int pointIndex=0; pointIndex < path.size() - 1; pointIndex++) {
                Point start = path.get(pointIndex);
                Point end = path.get(pointIndex + 1);
                DrawLine(start.x, start.y, end.x, end.y, RED);
            }
            EndMode2D();
            EndDrawing();
        }
        CloseWindow();
    }
}
