package hr.fer.zemris.util;

import java.text.DecimalFormat;
import java.util.Arrays;

public class Vector extends AbstractVector {

    private final double[] vector;
    private final int dimension;
    private final boolean readOnly;

    public Vector(double[] arr){
        this(false, false, arr);
    }

    /**
     *
     * @param readOnly <code>true</code> if this vector must not be allowed to be changed, <code>false</code> otherwise
     * @param newArray <code>true</code> if the given array can be used directly, <code>false</code> if a copy must be made first
     * @param arr one-dimensional array
     */
    public Vector(boolean readOnly, boolean newArray, double[] arr){
        this.readOnly=readOnly;
        dimension=arr.length;
        if (newArray) vector=arr;
        else {
            vector = new double[arr.length];
            System.arraycopy(arr, 0, vector, 0, arr.length);
        }
    }

    @Override
    public double get(int n) {
        return vector[n];
    }

    @Override
    public IVector set(int n, double d) {
        if (readOnly) throw new ReadOnlyException();
        vector[n]=d;
        return this;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public IVector copy() {
        return new Vector(readOnly, false, vector);
    }

    @Override
    public IVector newInstance(int n) {
        return new Vector(readOnly, true, new double[n]);
    }

    @Override
    public String toString(){
        return toString(3);
    }

    public String toString(int precision){
        StringBuilder str= new StringBuilder("(");

        DecimalFormat dec = new DecimalFormat("#0."+" ".repeat(precision));

        for (int k=0; k<dimension-1; ++k){
            str.append(dec.format(vector[k])).append(", ");
        }

        return Arrays.toString(vector);

        //return str.append(dec.format(vector[dimension-1])).append(")").toString();
    }

    public static IVector parseSimple(String str){
        String[] nums=str.split(" +");

        double[] arr=new double[nums.length];

        for (int k=nums.length-1; k>=0; --k){
            arr[k]=Double.parseDouble(nums[k]);
        }

        return new Vector(arr);
    }
}
