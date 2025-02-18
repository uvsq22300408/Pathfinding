package org.example.view;
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

import org.example.world.examples.Dijkstra;
import org.example.world.World;
import org.example.world.World.ERegionType;
import org.example.world.World.Region;

import com.raylib.Raylib.Camera2D;
import com.raylib.Raylib.Vector2;
public class MainWindow {
    
    public static void main(String args[]) {
        World world = new World(1000, 500, World.ERegionType.OCTILE, new World.Point(20, 20), new World.Point(400, 499),
        50);
        World world2 = new World(300, 300, World.ERegionType.OCTILE, new World.Point(20, 20), 
            new World.Point(270, 190), 50);
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
            drawPath(world);
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
