package hr.fer.zemris.marching.cubes;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Terrain {
    private final int width, height, depth;
    private List<double[]> vertices = null;
    private List<int[]> polygons = null;

    public Terrain(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public void buildTerrain(Function function) {
        vertices = new ArrayList<>();
        polygons = new ArrayList<>();

        int[][][] codes = new int[width][height][depth];

        for (int x=0; x<width; ++x){
            for (int y=0; y<height; ++y){
                for (int z=0; z<depth; ++z){
                    codes[x][y][z] = function.respectsFunction(x, y, z) ? 255 : -1;
                }
            }
        }

        for (int x=0; x<width; ++x){
            for (int y=0; y<height; ++y){
                for (int z=0; z<depth; ++z){
                    if (codes[x][y][z] == -1) {
                        generatePolygon(codes, x, y, z);
                    }
                    // if (polygons.size()>=polygonLimit) return;
                }
            }
        }

        normalize();
    }

    private void generatePolygon(int[][][] codes, int x, int y, int z) {
        codes[x][y][z] = getNewCode(codes, x, y, z);
        if (codes[x][y][z]==0 || codes[x][y][z]==255) return;

        int[] polygonArray = PolygonArray.array[codes[x][y][z]];
        int[] newPolygon = new int[3];

        double[][] verticesArray = VerticesArray.array;

        for (int k=0; polygonArray[k]!=-1; ++k){
            double[] newVertex = shift(verticesArray[polygonArray[k]], x, y, z);

            boolean flag = false;
            for (int i=vertices.size()-1; i>=0 && !flag; i--){
                double[] vertex = vertices.get(i);
                if (vertex[0] == newVertex[0] && vertex[1] == newVertex[1] && vertex[2] == newVertex[2]){
                    flag = true;
                    newPolygon[k%3] = i+1;
                }
            }
            if (!flag) {
                vertices.add(newVertex);
                newPolygon[k%3] = vertices.size();
            }

            if (k%3==2) {
                polygons.add(newPolygon);
                newPolygon = new int[3];
            }
        }
    }

    private int getNewCode(int[][][] codes, int x, int y, int z) {
        int newCode = 0;
        for (int x0 = x - 1; x0 < x + 2; ++x0) {
            int maskx = (x0 == x - 1) ? 0b10011001 : (x0 == x) ? 0b11111111 : 0b01100110;
            if (x0 != -1 && x0 != width) for (int y0 = y - 1; y0 < y + 2; ++y0) {
                int masky = (y0 == y - 1) ? 0b00001111 : (y0 == y) ? 0b11111111 : 0b11110000;
                if (y0 != -1 && y0 != height) for (int z0 = z - 1; z0 < z + 2; ++z0) {
                    int maskz = (z0 == z - 1) ? 0b00110011 : (z0 == z) ? 0b11111111 : 0b11001100;
                    if (z0 != -1 && z0 != depth && codes[x0][y0][z0] == 255)
                        newCode |= codes[x0][y0][z0] & maskx & masky & maskz;
                }
            }
        }
        return newCode;
    }

    private double[] shift(double[] vertex, int x, int y, int z) {
        double[] newVertex = new double[3];
        newVertex[0] = vertex[0] + x;
        newVertex[1] = vertex[1] + y;
        newVertex[2] = vertex[2] + z;
        return newVertex;
    }

    private void normalize(){
        double xmin= vertices.get(0)[0];
        double ymin= vertices.get(0)[1];
        double zmin= vertices.get(0)[2];
        double xmax=xmin;
        double ymax=ymin;
        double zmax=zmin;
        for (var vertex:vertices){
            double x=vertex[0], y=vertex[1], z=vertex[2];
            if (x<xmin) xmin=x;
            if (x>xmax) xmax=x;
            if (y<ymin) ymin=y;
            if (y>ymax) ymax=y;
            if (z<zmin) zmin=z;
            if (z>zmax) zmax=z;
        }

        double xdiff = xmax-xmin;
        double ydiff = ymax-ymin;
        double zdiff = zmax-zmin;

        double xmid=xmin+xdiff/2;
        double ymid=ymin+ydiff/2;
        double zmid=zmin+zdiff/2;

        double longestDiff;
        if (xdiff>ydiff && xdiff>zdiff) longestDiff = xdiff;
        else if (ydiff>xdiff && ydiff>zdiff) longestDiff = ydiff;
        else longestDiff = zdiff;

        //midPoint = new Vector(new double[]{xmid, ymid, zmid, 1});

        for (var vertex:vertices){
            vertex[0] = (vertex[0] - xmid) * 2 / longestDiff;
            vertex[1] = (vertex[1] - ymid) * 2 / longestDiff;
            vertex[2] = (vertex[2] - zmid) * 2 / longestDiff;

            //System.out.printf("%f, %f\n", vertex.get(0), vertex.get(1));
        }
        //System.out.println();

    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getDepth() {
        return depth;
    }

    public List<double[]> getVertices() {
        return vertices;
    }

    public List<int[]> getPolygons() {
        return polygons;
    }

    public String buildObject(IFileBuilder builder){
        return builder.buildObj(this);
    }

    public static void main(String[] args) throws IOException {
        int width = 101, height = 101, depth = 101;
        Terrain terrain = new Terrain(width,height,depth);

        //circle
//        terrain.buildTerrain((x,y,z)->{
//            int sum=(x-width/2)*(x-width/2) + (y-height/2)*(y-height/2) + (z-depth/2)*(z-depth/2);
//            int res = ((width-1)*(height-1)/4);
//            return sum < res;
//        });

        //cube
//        terrain.buildTerrain((x,y,z, width, height, depth)-> x == 0 || x == width-1 || y == 0 || y == height-1 || z == 0 || z == depth-1);

        //cone
//        terrain.buildTerrain((x,y,z) -> y-height/2.>-Math.sqrt(Math.pow(x-width/2., 2)+Math.pow(z-depth/2., 2)));

        //perlin noise 3D
        terrain.buildTerrain(new PerlinNoise3D(width, height, depth));

        //perlin noise 2D
//        terrain.buildTerrain(new PerlinNoise2D(width,height, 50, 50));

        Path path = Paths.get("./marchingCubes.obj");
        OutputStream os = new BufferedOutputStream(Files.newOutputStream(path));
        String str = terrain.buildObject(new ObjFileBuilder());
        os.write(str.getBytes());
        os.close();
    }
}
