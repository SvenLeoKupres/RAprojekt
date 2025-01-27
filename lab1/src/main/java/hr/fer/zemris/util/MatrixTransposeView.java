package hr.fer.zemris.util;

public class MatrixTransposeView extends AbstractMatrix {

    private IMatrix matrix;

    public MatrixTransposeView(IMatrix matrix){
        this.matrix=matrix;
    }

    @Override
    public int getRowsCount() {
        return matrix.getColsCount();
    }

    @Override
    public int getColsCount() {
        return matrix.getRowsCount();
    }

    @Override
    public double get(int n, int k) {
        return matrix.get(k, n);
    }

    @Override
    public IMatrix set(int n, int k, double d) {
        matrix.set(k, n, d);
        return this;
    }

    @Override
    public IMatrix copy() {
        return new MatrixTransposeView(matrix);
    }

    @Override
    public IMatrix newInstance(int n, int k) {
        return new MatrixTransposeView(matrix.newInstance(n, k));
    }

    @Override
    public IMatrix subMatrix(int n, int k, boolean flag) {
        matrix=matrix.subMatrix(k, n, false);
        return this;
    }
}
