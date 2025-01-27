package hr.fer.zemris;

import hr.fer.zemris.util.IMatrix;
import hr.fer.zemris.util.IVector;
import hr.fer.zemris.util.Matrix;
import hr.fer.zemris.util.Vector;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
    public static Map<Integer, IVector> calculateNormals(List<IVector> polygons, List<IVector> vertices) {
        Map<Integer, IVector> normals = new HashMap<>();

        for (var polygon : polygons) {
            IVector v1 = vertices.get((int) polygon.get(0));
            IVector v2 = vertices.get((int) polygon.get(1));
            IVector v3 = vertices.get((int) polygon.get(2));

            IVector np = v1.nSub(v2).copyPart(3).nVectorProduct(v1.nSub(v3).copyPart(3));

            for (int i = 0; i < 3; ++i) {
                int index = (int) polygon.get(i);
                IVector normal = normals.get(index);
                if (normal == null) normal = new Vector(new double[]{0, 0, 0});
                normal.add(np.nScalarMultiply(0.25));
                normals.put(index, normal);
            }
        }

        return normals;
    }

    public static void drawLine(Graphics2D g2d, int height, int width, IVector v1, IVector v2) {
        g2d.drawLine((int) (v1.get(0)*width), (int) (height-v1.get(1)*height), (int) (v2.get(0)*width), (int) (height-v2.get(1)*height));
    }

    /**
     * Normalizes all the vectors in the list (corresponding to vertices), so that the vertices fall in range [-1,1]x[-1,1]x[-1,1]
     * @param vertices the list of vectors/vertices being normalized
     */
    public static void normalize(List<IVector> vertices){
        double xmin= vertices.get(0).get(0);
        double ymin= vertices.get(0).get(1);
        double zmin= vertices.get(0).get(2);
        double xmax=xmin;
        double ymax=ymin;
        double zmax=zmin;
        for (var vertex:vertices){
            double x=vertex.get(0), y=vertex.get(1), z=vertex.get(2);
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
            vertex.set(0, (vertex.get(0) - xmid) * 2 / longestDiff);
            vertex.set(1, (vertex.get(1) - ymid) * 2 / longestDiff);
            vertex.set(2, (vertex.get(2) - zmid) * 2 / longestDiff);

            //System.out.printf("%f, %f\n", vertex.get(0), vertex.get(1));
        }
        //System.out.println();
    }

    public static IMatrix generateTransform(IVector eyepoint, IVector viewpoint){
        Matrix t1 = new Matrix(4, 4, new double[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {-eyepoint.get(0), -eyepoint.get(1), -eyepoint.get(2), 1.}
        }, true);

        IVector g1 = viewpoint.toRowMatrix(false).nMultiply(t1).toVector(false);

        double tmp = Math.sqrt(g1.get(0)*g1.get(0)+g1.get(1)*g1.get(1));
        double sina = (tmp!=0) ? (g1.get(1) / tmp) : 1;
        double cosa = (tmp!=0) ? (g1.get(0) / tmp) : 0;

        Matrix t2 = new Matrix(4, 4, new double[][]{
                {cosa, -sina, 0, 0},
                {sina, cosa, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        }, true);

        IVector g2 = g1.toRowMatrix(false).nMultiply(t2).toVector(false);

        tmp = Math.sqrt(g2.get(0)*g2.get(0)+g2.get(2)*g2.get(2));
        double sinb = (tmp!=0) ? (g2.get(0) / tmp) : 0;
        double cosb = (tmp!=0) ? (g2.get(2) / tmp) : 1;

        Matrix t3 = new Matrix(4, 4, new double[][]{
                {cosb, 0, sinb, 0},
                {0, 1, 0, 0},
                {-sinb, 0, cosb, 0},
                {0, 0, 0 ,1}
        }, true);

        Matrix t4 = new Matrix(4, 4, new double[][]{
                {0, -1, 0, 0},
                {1, 0, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        }, true);

        Matrix t5 = new Matrix(4, 4, new double[][]{
                {-1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, -1, 0},
                {0, 0, 0, 1}
        }, true);

        IMatrix t = t1.nMultiply(t2).nMultiply(t3).nMultiply(t4).nMultiply(t5);

        double h = viewpoint.distance(eyepoint);
        // double h = Math.sqrt(Math.pow(viewpoint.get(0)-eyepoint.get(0), 2)+Math.pow(viewpoint.get(1)-eyepoint.get(1), 2)+Math.pow(viewpoint.get(2)-eyepoint.get(2), 2));
        //double d = Math.sqrt(Math.pow(eyepoint.get(0), 2)+Math.pow(eyepoint.get(1), 2)+Math.pow(eyepoint.get(2), 2));

        Matrix p = new Matrix(4, 4, new double[][]{
                {1/h, 0, 0, 0},
                {0, 1/h, 0, 0},
                {0, 0, 0, 1/h},
                {0, 0, 0, 0}
        }, true);

        return t.nMultiply(p);
    }

    public static IVector rodriguesFormula(IVector v, IVector k, double cos, double sin){
        IVector res = v.nScalarMultiply(cos);
        res = res.add(k.nVectorProduct(v).nScalarMultiply(sin));
        res = res.add(k.scalarMultiply(k.scalarProduct(v)).nScalarMultiply(1-cos));
        return new Vector(new double[]{res.get(0), res.get(1), res.get(2), 1});
    }
}
