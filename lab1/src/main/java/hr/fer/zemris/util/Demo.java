package hr.fer.zemris.util;

public class Demo {
    public static void main(String[] args){
        IMatrix phi = new Matrix(5, 5, new double[][]{{1., 0.25, 0.0625, 0.0156, 0.0039}, {1., 0.5, 0.25, 0.125, 0.0625}, {1., 1., 1., 1., 1.}, {1., 1.5, 2.25, 3.375, 5.0625}, {1, 2., 4., 8., 16.}}, true);
        IMatrix y = new Matrix(5, 1, new double[][]{{0.707}, {1.}, {0.}, {-1.}, {0.}}, true);
        IMatrix lambda = new Matrix(5, 5, new double[][]{{1,0,0,0,0}, {0,1,0,0,0}, {0,0,1,0,0}, {0,0,0,1,0}, {0,0,0,0,1}}, true);
        IMatrix w = phi.nTranspose(false).nMultiply(phi).nAdd(lambda).nInvert().nMultiply(phi.nTranspose(false)).nMultiply(y);
        System.out.println(w.toArray());
    }
}
