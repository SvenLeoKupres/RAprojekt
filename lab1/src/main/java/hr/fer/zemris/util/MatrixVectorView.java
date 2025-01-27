package hr.fer.zemris.util;

/**
 * asRowMatrix je istinit ako ima samo jedan red
 */
public class MatrixVectorView extends AbstractMatrix {

    private IVector vector;
    private boolean asRowMatrix;

    public MatrixVectorView(IVector vector, boolean asRowMatrix){
        this.vector=vector;
        this.asRowMatrix=asRowMatrix;
    }

    @Override
    public int getRowsCount() {
        return asRowMatrix ? 1 : vector.getDimension();
    }

    @Override
    public int getColsCount() {
        return asRowMatrix ? vector.getDimension() : 1;
    }

    @Override
    public double get(int n, int k) {
        return asRowMatrix ? vector.get(k) : vector.get(n);
    }

    @Override
    public IMatrix set(int n, int k, double d) {
        if (asRowMatrix) vector.set(k, d);
        else vector.set(n, d);
        return this;
    }

    @Override
    public IMatrix copy() {
        return new MatrixVectorView(vector.copy(), asRowMatrix);
    }

    @Override
    public IMatrix newInstance(int n, int k) {
        return new MatrixVectorView(vector.newInstance(vector.getDimension()), asRowMatrix);
    }
}
