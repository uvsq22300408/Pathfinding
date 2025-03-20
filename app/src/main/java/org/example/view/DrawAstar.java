package org.example.view;

import com.raylib.Raylib.Camera2D;
import com.raylib.Raylib.Vector2;

import static com.raylib.Colors.RAYWHITE;
import static com.raylib.Colors.RED;
import static com.raylib.Colors.YELLOW;
import static com.raylib.Colors.BLUE;
import static com.raylib.Colors.LIME;
import static com.raylib.Colors.PURPLE;
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

import org.example.world.World;
import org.example.world.examples.AstarGrid;

public class DrawAstar {
    public static void drawAstar(World world) {
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
            MainWindow.drawWorld(world);
            MainWindow.drawObstacles(world);
            if (AstarGrid.cheminFinal != null && !AstarGrid.cheminFinal.isEmpty()) { // S'il y a un chemin
                AstarGrid.cheminFinal.iterator().forEachRemaining((p) -> {
                    DrawRectangle(p.x * world.tailleReg, p.y * world.tailleReg,
                        world.tailleReg, world.tailleReg, RED);
                });
            }
            EndMode2D();
            EndDrawing();
        }
        CloseWindow();
    }
}
