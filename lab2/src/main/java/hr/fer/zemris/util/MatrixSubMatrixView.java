package hr.fer.zemris.util;

public class MatrixSubMatrixView extends AbstractMatrix {

    IMatrix matrix;
    int[] rows;
    int[] columns;

    public MatrixSubMatrixView(IMatrix matrix, int n, int k){
        this.matrix=matrix;

        rows=new int[matrix.getRowsCount()-1];
        columns=new int[matrix.getColsCount()-1];

        int passed=0;
        for (int i=0; i< matrix.getRowsCount()-1; ++i){
            if (i==n){
                passed++;
            }
            rows[i]=i+passed;
        }
        passed=0;
        for (int i=0; i< matrix.getColsCount()-1; ++i){
            if (i==k){
                passed++;
            }
            columns[i]=i+passed;
        }
    }

    private MatrixSubMatrixView(IMatrix matrix, int[] n, int[] k){
        this.matrix=matrix;

        rows=new int[n.length];
        columns=new int[k.length];

        System.arraycopy(n, 0, rows, 0, n.length);
        System.arraycopy(k, 0, rows, 0, k.length);
    }

    @Override
    public int getRowsCount() {
        return rows.length;
    }

    @Override
    public int getColsCount() {
        return columns.length;
    }

    @Override
    public double get(int n, int k) {
        if (n<0 || n>=rows.length || k<0 || k>=columns.length) throw new IncompatibleOperandException();
        return matrix.get(rows[n], columns[k]);
    }

    @Override
    public IMatrix set(int n, int k, double d) {
        if (n<0 || n>=rows.length || k<0 || k>=columns.length) throw new IncompatibleOperandException();

        matrix.set(rows[n], columns[k], d);

        return this;
    }

    @Override
    public IMatrix copy() {
        return new MatrixSubMatrixView(matrix.copy(), rows, columns);
    }

    @Override
    public IMatrix newInstance(int n, int k) {
        return new MatrixSubMatrixView(matrix.newInstance(n, k), rows, columns);
    }

    @Override
    public double[][] toArray(){
        double[][] arr=new double[rows.length][columns.length];

        for (int k=rows.length-1; k>=0; --k){
            for (int i=columns.length-1; i>=0; --i){
                arr[k][i]=matrix.get(rows[k], columns[i]);
            }
        }

        return arr;
    }
}
