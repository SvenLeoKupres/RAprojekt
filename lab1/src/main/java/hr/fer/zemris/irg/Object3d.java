package hr.fer.zemris.irg;

import hr.fer.zemris.Util;
import hr.fer.zemris.util.IVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Object3d {
    private List<IVector> polygons;
    private List<IVector> vertices;
    private Map<Integer, IVector> normals;

    public Object3d(List<IVector> polygons, List<IVector> vertices) {
        this(polygons, vertices, Util.calculateNormals(polygons, vertices));
    }

    public Object3d(List<IVector> polygons, List<IVector> vertices, Map<Integer, IVector> normals) {
        this.polygons = polygons;
        this.vertices = vertices;
        this.normals = normals;
    }

    public List<IVector> getPolygons() {
        return polygons;
    }

    public List<IVector> getVertices() {
        return vertices;
    }

    public Map<Integer, IVector> getNormals() {
        return normals;
    }

    public void normalize(){
        Util.normalize(vertices);
    }

    public void scale(double scale){
        for (IVector v : vertices) {
            v.scalarMultiply(scale).set(3, 1);
        }
    }

//    public void translate(double x, double y, double z) {
//        IVector v = new Vector(new double[]{x, y, z});
//        for (var k: polygons) {
//            k.add(v);
//        }
//    }
//
//    public void rotate(double pitch, double yaw, double roll) {
//    }
//
//    public void transform(IMatrix matrix){
//        vertices.replaceAll(iVector -> iVector.toRowMatrix(true).nMultiply(matrix).toVector(false));
//    }

    public Object3d copy(){
        return new Object3d(new ArrayList<>(this.polygons), new ArrayList<>(this.vertices), new HashMap<>(this.normals));
    }
}
