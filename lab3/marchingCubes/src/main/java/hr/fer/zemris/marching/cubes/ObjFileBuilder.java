package hr.fer.zemris.marching.cubes;

import java.util.List;

public class ObjFileBuilder implements IFileBuilder {
    @Override
    public String buildObj(Terrain terrain) {
        List<double[]> vertices = terrain.getVertices();
        List<int[]> polygons = terrain.getPolygons();

        StringBuilder sb = new StringBuilder();

        for (double[] vertex : vertices) {
            sb.append("v ").append(vertex[0]).append(" ").append(vertex[1]).append(" ").append(vertex[2]).append("\n");
        }
        for (int[] polygon : polygons) {
            sb.append("f ").append(polygon[0]).append(" ").append(polygon[1]).append(" ").append(polygon[2]).append("\n");
        }
        return sb.toString();
    }
}
