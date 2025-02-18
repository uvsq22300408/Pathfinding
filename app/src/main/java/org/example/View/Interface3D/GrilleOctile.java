package org.example.View.Interface3D;

import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
public class GrilleOctile {
    
    public static void main(String args[]) {
        InitWindow(800, 600, "Pathfinding");
        SetTargetFPS(60);
        while(!WindowShouldClose()) {
            BeginDrawing();
            ClearBackground(RAYWHITE);
            EndDrawing();
        }
        CloseWindow();
    }
}
