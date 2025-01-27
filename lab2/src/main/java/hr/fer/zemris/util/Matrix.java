package hr.fer.zemris.util;

public class Matrix extends AbstractMatrix {
    private double[][] matrix;
    private int rows;
    private int columns;

    public Matrix(int n, int k){
        this(n, k, new double[n][k], true);
    }

    /**
     *
     * @param n number of rows
     * @param k number of columns
     * @param elements a two-dimensional array of elements
     * @param flag if <code>true</code> uses the given reference to the given array, otherwise creates a copy of the array
     */
    public Matrix(int n, int k, double[][] elements, boolean flag){
        rows=n;
        columns=k;
        if (flag) matrix=elements;
        else {
            matrix=new double[rows][columns];
            for (int i=rows-1; i>=0; --i){
                System.arraycopy(elements[i], 0, matrix[i], 0, columns);
            }
        }
    }

    @Override
    public int getRowsCount() {
        return rows;
    }

    @Override
    public int getColsCount() {
        return columns;
    }

    @Override
    public double get(int row, int column) {
        if (row<0 || row>=rows || column<0 || column>=columns) throw new IllegalArgumentException();

        return matrix[row][column];
    }

    @Override
    public IMatrix set(int row, int column, double d) {
        if (row<0 || row>=rows || column<0 || column>=columns) throw new IllegalArgumentException();

        matrix[row][column]=d;
        return this;
    }

    @Override
    public IMatrix copy() {
        return new Matrix(rows, columns, matrix, false);
    }

    @Override
    public IMatrix newInstance(int n, int k) {
        return new Matrix(n, k);
    }

    public static Matrix parseSimple(String str){
        String[] vectors=str.split(" *\\Q|\\E *");

        int rows=vectors.length, columns=vectors[0].split(" +").length;
        double[][] matrix=new double[rows][columns];

        for (int k=rows-1; k>=0; --k){
            String[] nums=vectors[k].split(" +");

            for (int i=nums.length-1; i>=0; --i){
                matrix[k][i]=Double.parseDouble(nums[i]);
            }
        }

        return new Matrix(rows, columns, matrix, true);
    }
}
