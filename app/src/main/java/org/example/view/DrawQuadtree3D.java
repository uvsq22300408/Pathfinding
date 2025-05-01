package org.example.view;

import static com.raylib.Colors.RAYWHITE;
import static com.raylib.Colors.RED;
import static com.raylib.Colors.WHITE;
import static com.raylib.Colors.YELLOW;
import static com.raylib.Colors.BLUE;
import static com.raylib.Colors.LIME;
import static com.raylib.Colors.PURPLE;
import static com.raylib.Colors.GRAY;
import static com.raylib.Raylib.BeginDrawing;
import static com.raylib.Raylib.BeginMode2D;
import static com.raylib.Raylib.BeginMode3D;
import static com.raylib.Raylib.CAMERA_FIRST_PERSON;
import static com.raylib.Raylib.CAMERA_FREE;
import static com.raylib.Raylib.CAMERA_PERSPECTIVE;
import static com.raylib.Raylib.ClearBackground;
import static com.raylib.Raylib.CloseWindow;
import static com.raylib.Raylib.DisableCursor;
import static com.raylib.Raylib.DrawCircle;
import static com.raylib.Raylib.DrawCube;
import static com.raylib.Raylib.DrawGrid;
import static com.raylib.Raylib.DrawLine;
import static com.raylib.Raylib.DrawRectangle;
import static com.raylib.Raylib.EndDrawing;
import static com.raylib.Raylib.EndMode2D;
import static com.raylib.Raylib.EndMode3D;
import static com.raylib.Raylib.GetCurrentMonitor;
import static com.raylib.Raylib.GetMonitorHeight;
import static com.raylib.Raylib.GetMonitorWidth;
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
import static com.raylib.Raylib.UpdateCamera;
import static com.raylib.Raylib.WindowShouldClose;

import java.util.ArrayList;
import java.util.List;

import org.example.world.World;
import org.example.world.examples.AstarGrid;
import org.example.world.examples.Quadtree3D;
import org.example.world.examples.Quadtree3D.QuadtreeRegion3D;
import org.example.world3D.World3D;
import org.example.world3D.World3D.Region3D;

import com.raylib.Raylib.Camera3D;
import com.raylib.Raylib.Vector2;
import com.raylib.Raylib.Vector3;

public class DrawQuadtree3D {
    public static void drawQuadtree3D(World3D world) {
        int display = GetCurrentMonitor();
        InitWindow(GetMonitorWidth(display), GetMonitorHeight(display), "Pathfinding");
        SetTargetFPS(60);
        Camera3D camera = new Camera3D();
        camera.fill(0);
        Vector3 position = new Vector3();
        position.x(-5.0f);
        position.y(20.0f);
        position.z(30.0f);
        Vector3 targetVector = new Vector3();
        targetVector.x(0);
        targetVector.y(0);
        targetVector.z(0);
        camera._position(position);
        camera.target(targetVector);
        camera.fovy(60.0f);
        camera.projection(CAMERA_PERSPECTIVE);
        Vector3 camUp = new Vector3();
        camUp.x(0);
        camUp.y(0.0f);
        camUp.z(1.0f);
        camera.up(camUp);
        DisableCursor();
        // Vector2 cameraOffset = GetMousePosition();
        // camera.offset(cameraOffset);
        // camera.zoom(1.0f);
        List<World3D.Region3D> obstacleCubes = obstaclesCube(world);
        while(!WindowShouldClose()) {
            UpdateCamera(camera, CAMERA_FREE);
            BeginDrawing();
            BeginMode3D(camera);
            ClearBackground(RAYWHITE);
            MainWindow.drawWorld3D(world);
            MainWindow.drawObstacles3D(world);
            MainWindow.drawPath3D(world);
            /*for(World3D.Region3D r : obstacleCubes) {
                Vector3 posi = new Vector3();
                posi.x(r.x);
                posi.y(r.y);
                posi.z(r.z);
                DrawCube(posi, world.tailleReg, world.tailleReg, world.tailleReg, PURPLE);
            }*/
            // Draw path 
            if (Quadtree3D.fathers != null && Quadtree3D.endRegion != null) {
                QuadtreeRegion3D current = Quadtree3D.endRegion;
                while (Quadtree3D.fathers[current.id] != null) {
                    QuadtreeRegion3D f = Quadtree3D.fathers[current.id];
                    int fwidth = world.width / f.divisionFactor;
                    int fheight = world.height / f.divisionFactor;
                    int fdepth = world.depth / f.divisionFactor;
                    Vector3 fposition = new Vector3();
                    position.x(f.x + (fwidth / 2));
                    position.y(f.y + (fheight / 2));
                    position.z(f.z + (fdepth / 2));
                    DrawCube(position, fwidth, fheight, fdepth, GRAY);
                    current = f;
                }
            }
            //DrawGrid(10, 10);
            EndMode3D();
            EndDrawing();
        }
        CloseWindow();
    }

    static List<World3D.Region3D> obstaclesCube(World3D world) {
        ArrayList<World3D.Region3D> cubes = new ArrayList<>();
        for(int _z = 0; _z < world.getNbRegion(); _z++) {
            if (world.passThrough[_z] == World.InnerWorld.OBSTACLE) {
                int x = _z / (world.heightInRegion() * world.depthInRegion());
                int y = (_z - x * world.heightInRegion() * world.depthInRegion()) / world.depthInRegion();
                int z = _z % world.depthInRegion();
                World3D.Region3D reg = new Region3D(x * world.tailleReg,
                    y * world.tailleReg, z * world.tailleReg, world.tailleReg);
                cubes.add(reg);
            }
        }
        System.out.println("obstaclesCubes: " + cubes.size() + " found.");
        return cubes;
    }
}
