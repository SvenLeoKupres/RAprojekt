package hr.fer.zemris.util;

public interface IVector {
    double get(int n);
    IVector set(int n, double d);
    int getDimension();
    IVector copy();
    IVector copyPart(int n);
    IVector newInstance(int n);
    IVector add(IVector vector);
    IVector nAdd(IVector vector);
    IVector sub(IVector vector);
    IVector nSub(IVector vector);
    IVector scalarMultiply(double d);
    IVector nScalarMultiply(double d);
    double norm();
    IVector normalize();
    IVector nNormalize();
    double cosine(IVector vector);
    double scalarProduct(IVector vector);
    double distance(IVector vector);
    IVector nVectorProduct(IVector vector);
    IVector nFromHomogenous();
    /**
     *
     * @param flag if <code>true</code>, creates MatrixVectorView, manually creates a Matrix otherwise
     * @return a matrix representation of the vector with dimensions 1 x dim(this)
     */
    IMatrix toRowMatrix(boolean flag);
    /**
     *
     * @param flag if <code>true</code>, creates MatrixVectorView, manually creates a Matrix otherwise
     * @return a matrix representation of the vector with dimensions dim(this) x 1
     */
    IMatrix toColumnMatrix(boolean flag);
    double[] toArray();
}
