package hr.fer.zemris.util;

public abstract class AbstractMatrix implements IMatrix {

    @Override
    public IMatrix nTranspose(boolean flag) {
        if (!flag) {
            IMatrix newMatrix = newInstance(getColsCount(), getRowsCount());

            for (int k = getRowsCount() - 1; k >= 0; --k) {
                for (int i = getColsCount() - 1; i >= 0; --i) {
                    newMatrix.set(i, k, get(k, i));
                }
            }

            return newMatrix;
        }

        return new MatrixTransposeView(this);
    }

    @Override
    public IMatrix add(IMatrix matrix) {
        if (this.getRowsCount()!= matrix.getRowsCount() || this.getColsCount()!=this.getColsCount()) throw new IncompatibleOperandException();

        for (int k=this.getRowsCount()-1; k>=0; --k){
            for (int i=this.getColsCount()-1; i>=0; --i){
                set(k, i, this.get(k, i) + matrix.get(k, i));
            }
        }

        return this;
    }

    @Override
    public IMatrix nAdd(IMatrix matrix) {
        return this.copy().add(matrix);
    }

    @Override
    public IMatrix sub(IMatrix matrix) {
        if (this.getRowsCount()!= matrix.getRowsCount() || this.getColsCount()!=this.getColsCount()) throw new IncompatibleOperandException();

        for (int k=this.getRowsCount()-1; k>=0; --k){
            for (int i=this.getColsCount()-1; i>=0; --i){
                set(k, i, this.get(k, i) - matrix.get(k, i));
            }
        }

        return this;
    }

    @Override
    public IMatrix nSub(IMatrix matrix) {
        return this.copy().sub(matrix);
    }

    @Override
    public IMatrix nMultiply(IMatrix matrix) {
        if (this.getColsCount()!= matrix.getRowsCount()) throw new IncompatibleOperandException();

        IMatrix newMatrix=this.newInstance(this.getRowsCount(), matrix.getColsCount());

        for (int rowNo=getRowsCount()-1; rowNo>=0; --rowNo){
            for (int columnNo=matrix.getColsCount()-1; columnNo>=0; --columnNo){
                double tmp=0;
                for (int help=getColsCount()-1; help>=0; --help){
                    tmp+=get(rowNo, help)*matrix.get(help, columnNo);
                }
                newMatrix.set(rowNo, columnNo, tmp);
            }
        }

        return newMatrix;
    }

    @Override
    public double determinant() {
        if (this.getColsCount()!=getRowsCount()) throw new IncompatibleOperandException();

        if (this.getRowsCount()==1) return get(0,0);

        if (getRowsCount()==2) {
            //double a=get(0,0), b=get(1,1), c=get(0,1), d=get(1,0);
            //double d=get(0, 0) * get(1, 1) - get(0, 1) * get(1, 0);
            return get(0, 0) * get(1, 1) - get(0, 1) * get(1, 0);
        }

        double d=0;

        for (int k=0; k<getRowsCount(); ++k){
            if (k%2==0) d+=get(0, k)*this.subMatrix(0, k, true).determinant();
            else d-=get(0, k)*this.subMatrix(0, k, true).determinant();
        }

        return d;
    }

    @Override
    public IMatrix subMatrix(int row, int column, boolean idk) {
        if (row<0 || row>=getRowsCount() || column<0 || column>=getColsCount()) throw new IllegalArgumentException();

        if (!idk) {
            IMatrix newMatrix = this.newInstance(getRowsCount() - 1, getColsCount() - 1);

            int passed = 0;

            for (int k = 0; k < getRowsCount(); ++k) {
                for (int i = 0; i < getColsCount(); ++i) {
                    if (k == row && i == column) {
                        passed = 1;
                    }
                    newMatrix = set(k, i, get(k + passed, i + passed));
                }
            }

            return newMatrix;
        }

        return new MatrixSubMatrixView(this, row, column);
    }

    @Override
    public IMatrix nInvert() {
        if (this.getColsCount()!=getRowsCount() || this.determinant()==0) throw new IncompatibleOperandException();

        IMatrix newMatrix=this.newInstance(this.getRowsCount(), this.getColsCount());
        for (int k=getRowsCount()-1; k>=0; --k){
            for (int i=getColsCount()-1; i>=0; --i){
                if (k==i) newMatrix.set(k, i, 1);
                else newMatrix.set(k, i, 0);
            }
        }

        IMatrix copyMatrix=this.copy();
        for (int k=0; k<this.getColsCount(); ++k){
            if (copyMatrix.get(k, k)==0){
                int tmp=this.getColsCount()-1;
                while (copyMatrix.get(tmp, k)==0) tmp--;
                double tmp2=copyMatrix.get(k, tmp);
                for (int i=copyMatrix.getColsCount()-1; i>=0; --i) {
                    copyMatrix.set(k, i, copyMatrix.get(k, i) + copyMatrix.get(tmp, i) / tmp2);
                    newMatrix.set(k, i, newMatrix.get(k, i) + newMatrix.get(tmp, i) / tmp2);
                }
            }

            if (copyMatrix.get(k, k)!=1){
                double tmp=copyMatrix.get(k,k);
                for (int i=0; i<this.getColsCount(); ++i){
                    copyMatrix.set(k, i, copyMatrix.get(k, i)/tmp);
                    newMatrix.set(k, i, newMatrix.get(k, i)/tmp);
                }
            }

            for (int i=0; i<this.getRowsCount(); ++i){
                if (i!=k && copyMatrix.get(i, k)!=0){
                    double tmp=copyMatrix.get(i, k);
                    for (int j=0; j<this.getColsCount(); ++j){
                        copyMatrix.set(i, j, copyMatrix.get(i, j)-copyMatrix.get(k, j)*tmp);
                        newMatrix.set(i, j, newMatrix.get(i, j)-newMatrix.get(k, j)*tmp);
                    }
                }
            }
        }

        return newMatrix;
    }

    @Override
    public IVector singleRow(int rowNo){
        IVector vector = new Vector(new double[getColsCount()]);
        for (int k=0; k<getColsCount(); ++k){
            vector.set(k, get(rowNo, k));
        }

        return vector;
    }

    @Override
    public IVector singleColumn(int colNo){
        IVector vector = new Vector(new double[getRowsCount()]);
        for (int k=0; k<getRowsCount(); ++k){
            vector.set(k, get(k, colNo));
        }

        return vector;
    }

    @Override
    public double[][] toArray() {
        double[][] matrix=new double[getRowsCount()][getColsCount()];
        for (int k=getRowsCount()-1; k>=0; --k){
            for (int i=getColsCount()-1; i>=0; --i){
                matrix[k][i]=get(k, i);
            }
        }
        return matrix;
    }

    /**
     *
     * @param idk if <code>true</code> uses <code>VectorMatrixView</code>, manually calculates a vector otherwise
     * @return a vector representation of a matrix
     */
    @Override
    public IVector toVector(boolean idk) {
        if (this.getColsCount()>1 && getRowsCount()>1) throw new IncompatibleOperandException();

        if (!idk) {
            double[] arr;

            if (this.getRowsCount() == 1) {
                arr = new double[getColsCount()];
                for (int k = this.getColsCount() - 1; k >= 0; --k) {
                    arr[k] = get(0, k);
                }
            } else {
                arr = new double[getRowsCount()];
                for (int k = this.getRowsCount() - 1; k >= 0; --k) {
                    arr[k] = get(k, 0);
                }
            }

            return new Vector(arr);
        }

        return new VectorMatrixView(this);
    }
}
