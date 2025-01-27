package hr.fer.zemris.util;

public interface IMatrix {
    int getRowsCount();
    int getColsCount();
    double get(int n, int k);
    IMatrix set(int n, int k, double d);
    IMatrix copy();
    IMatrix newInstance(int n, int k);
    IMatrix nTranspose(boolean flag);
    IMatrix add(IMatrix matrix);
    IMatrix nAdd(IMatrix matrix);
    IMatrix sub(IMatrix matrix);
    IMatrix nSub(IMatrix matrix);
    IMatrix nMultiply(IMatrix matrix);
    double determinant();
    IMatrix subMatrix(int n, int k, boolean flag);
    IMatrix nInvert();
    IVector singleRow(int rowNo);
    IVector singleColumn(int columnNo);
    double[][] toArray();
    IVector toVector(boolean flag);
}
