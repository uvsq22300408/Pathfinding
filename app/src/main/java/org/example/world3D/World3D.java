package org.example.world3D;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.example.world.World.InnerWorld;

public  class World3D {

    public World3D(int w, int h, Point3D _start, Point3D _destination,
        int _tailleRegion, int z) {
        this.depth = z;
        obstacles = new ArrayList<>();
        width = w;
        height = h;
        tailleReg = _tailleRegion;
        start = _start;
        destination = _destination;
        info = new InfoWorld3D();
        Region3D startReg, destinationRegion;
        startReg = new Region3D.Octile(start.x, start.y, start.z, tailleReg);
        destinationRegion = new Region3D.Octile(destination.x, destination.y, destination.z, 
            tailleReg);
        this.startReg = startReg;
        this.destinationReg = destinationRegion;
        passThrough = new int[info.nbRegion];
        for (int ix = 0; ix < info.nbRegion; ix++) {
            passThrough[ix] = -1;
        }
        passThrough[getRegionId(startReg)] = InnerWorld.START;
        passThrough[getRegionId(destinationRegion)] = InnerWorld.DESTINATION;
        
    }

    public static class Region3D {
        public Region3D(float x, float y, float z, int tailleReg) {
            int regionStartX = Math.round(x) / tailleReg;
            int regionStartY = Math.round(y) / tailleReg;
            int regionStartZ = Math.round(z) / tailleReg;
            this.x = regionStartX * tailleReg;
            this.y = regionStartY * tailleReg;
            this.z = regionStartZ * tailleReg;
            this.distance = 0;
        }

        public boolean egaleA(Region3D r2) {
            if (x == r2.x && y == r2.y && z == r2.z) {
                return true;
            }
            return false;
        }

        public Region3D(int x, int y, int z, int tailleReg, double dist) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.distance = dist;
        }

        public static class Octile extends Region3D {
            public Octile(float x, float y, float z, int tailleReg) {
                super(x, y, z, tailleReg);
            }
        }

        public final int x;
        public final int y;
        public final int z;
        public final double distance;
    }

    public static class Obstacle3D {
        public Obstacle3D(float _x,  float _y, float _z, float r) {
            x = _x;
            y = _y;
            radius = r;
            z = _z;
        }
        
        public float x;
        public float y;
        public float radius;
        public float z;
    }

    public static class Point3D {
        public Point3D(float x, float y, float z) {
            this.z = z;
            this.x = x;
            this.y = y;
            }

        public float x;
        public float y;
        public float z;
    }

    public void addObstacle(float x, float y, float z, float r) {
        Obstacle3D ob = new Obstacle3D(x, y, z, r);
        obstacles.add(ob);
        // Get corner regions.
        int tr = tailleReg;
        //Region3D obstacleCenter = new Region3D(x, y, tr);
        //int obstacleCenterId = getRegionId(obstacleCenter);
        Region3D topLeftRegion = new Region3D(x - r, y - r, z - r, tr);
        Region3D downRightRegion = new Region3D(x + r, y + r, z + r, tr);
        // Mark the rectangle regions around obstacle as obstacle.
        for (float w = topLeftRegion.x; w <= downRightRegion.x; w += tr) {
            for (float h = topLeftRegion.y; h <= downRightRegion.y; h += tr) {
                for (float d = topLeftRegion.z; d <= downRightRegion.z; d += tr) {
                    Region3D obstacle = new Region3D(w, h, d, tr);
                    int id = getRegionId(obstacle);
                    if (id < info.nbRegion) {
                        passThrough[id] = InnerWorld.OBSTACLE;
                    }
                }
            }
        }
    }

    public class InfoWorld3D {
        public InfoWorld3D() {
            widthInRegion = width / tailleReg;
            heightInRegion = height / tailleReg;
            depthInRegion = depth / tailleReg;
            nbRegion = widthInRegion * heightInRegion * depthInRegion;
        }

        public Set<Region3D> getAdjacent(Region3D r) {
            Set<Region3D> neighborhood = new LinkedHashSet<>();
            int x_normalized = r.x / tailleReg;
            int y_normalized = r.y / tailleReg;
            int z_normalized = r.z / tailleReg;
            // We go through each adjacent region
            // and store those that are not obstacles
            for (int x = x_normalized - 1; x < x_normalized + 2; x++) {
                if (x < 0 || x >= widthInRegion) continue;                        
                for (int y = y_normalized - 1; y < y_normalized + 2; y++) {
                    if (y < 0 || y >= heightInRegion) continue;
                    for (int z = z_normalized - 1; z < z_normalized + 2; z++) {
                        if (z < 0 || z >= depthInRegion) continue;
                        if (y == y_normalized && x == x_normalized && z == z_normalized) continue;
                        if ((passThrough[x * heightInRegion * depthInRegion 
                                + y * depthInRegion + z] == InnerWorld.OBSTACLE)) 
                            continue;
                        double distanceX = (x - x_normalized) * tailleReg;
                        double distanceY = (y - y_normalized) * tailleReg;
                        double distanceZ = (z - z_normalized) * tailleReg;
                        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY 
                            + distanceZ * distanceZ);
                        Region3D neigh = new Region3D(x * tailleReg, y * tailleReg, z * tailleReg,
                            tailleReg, distance);
                        neighborhood.add(neigh);
                    }
                }
            }
            return neighborhood;
        }

        public void infoSetFather(int regx, int regy, int regz, int fatherId) {
            //System.out.println("father(" + regx + "," + regy + "," + regz + ") = " + fatherId);
            passThrough[regx * heightInRegion * depthInRegion + regy * depthInRegion + regz] = fatherId;
        }

        public int infoGetRegionId(Region3D reg) {
            int x_normalized = reg.x / tailleReg;
            int y_normalized = reg.y / tailleReg;
            int z_normalized = reg.z / tailleReg;
            int id =  x_normalized * heightInRegion * depthInRegion 
                + y_normalized * depthInRegion + z_normalized;
            //System.out.println("id = " + id);
            return id;
        }

        public int widthInRegion;
        public int heightInRegion;
        public int depthInRegion;
        public int nbRegion;
    }

    public void setFather(int regx, int regy, int regz, int pereId) {
        info.infoSetFather(regx, regy, regz, pereId);
    }

    public int getNbRegion() {
        return info.nbRegion;
    }

    public int getRegionId(Region3D reg) {
        return info.infoGetRegionId(reg);
    }

    public Set<Region3D> adjacents(Region3D r) {
        return info.getAdjacent(r);
    }

    public int heightInRegion() {
        return info.heightInRegion;
    }

    public int widthInRegion() {
        return info.widthInRegion;
    }

    public int depthInRegion() {
        return info.depthInRegion;
    }

    public int depth;
    public int tailleReg;
    public int height;
    public int width;
    public List<Obstacle3D> obstacles;
    public Point3D start;
    public Point3D destination;
    public Region3D startReg;
    public Region3D destinationReg;
    public int[] passThrough;
    public InfoWorld3D info; 
}
