package hr.fer.zemris.util;

public class VectorMatrixView extends AbstractVector {

    private IMatrix matrix;
    private int dimension;
    private boolean rowMatrix;

    public VectorMatrixView(IMatrix matrix){
        this.matrix=matrix;
        dimension = Math.max(matrix.getRowsCount(), matrix.getColsCount());
        rowMatrix = dimension==matrix.getColsCount();
    }
    @Override
    public double get(int n) {
        return rowMatrix ? matrix.get(0, n) : matrix.get(n, 0);
    }

    @Override
    public IVector set(int n, double d) {
        if (rowMatrix) matrix.set(0, n, d);
        else matrix.set(n, 0, d);
        return this;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public IVector copy() {
        return new VectorMatrixView(matrix.copy());
    }

    @Override
    public IVector newInstance(int n) {
        return rowMatrix ? new VectorMatrixView(matrix.newInstance(n, 0)) : new VectorMatrixView(matrix.newInstance(0, n));
    }
}
