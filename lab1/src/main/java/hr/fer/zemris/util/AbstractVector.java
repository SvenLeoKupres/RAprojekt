package hr.fer.zemris.util;

public abstract class AbstractVector implements IVector {

    @Override
    public IVector copyPart(int n) {
        IVector vector=newInstance(n);
        int k;
        for (k=0; k<this.getDimension() && k<n; ++k){
            vector.set(k, this.get(k));
        }
        if (k<this.getDimension()) return vector;

        for (; k<n; ++k){
            vector.set(k, 0);
        }

        return vector;
    }

    @Override
    public IVector add(IVector vector) {
        if ( this.getDimension()!=vector.getDimension() ) throw new IncompatibleOperandException();

        for (int i=this.getDimension()-1; i>=0; --i) {
            this.set(i, this.get(i) + vector.get(i));
        }

        return this;
    }

    @Override
    public IVector nAdd(IVector vector) {
        return this.copy().add(vector);
    }

    @Override
    public IVector sub(IVector vector) {
        if ( this.getDimension()!=vector.getDimension() ) throw new IncompatibleOperandException();

        for (int i=this.getDimension()-1; i>=0; --i) {
            this.set(i, this.get(i) - vector.get(i));
        }

        return this;
    }

    @Override
    public IVector nSub(IVector vector) {
        return this.copy().sub(vector);
    }

    @Override
    public IVector scalarMultiply(double d) {
        for (int k=0; k<this.getDimension(); ++k){
            this.set(k, get(k)*d);
        }

        return this;
    }

    @Override
    public IVector nScalarMultiply(double d) {
        return this.copy().scalarMultiply(d);
    }

    @Override
    public double norm() {
        double norm=0;

        for (int k=0; k<getDimension(); ++k){
            norm+=get(k)*get(k);
        }

        return Math.sqrt(norm);
    }

    @Override
    public IVector normalize() {
        double norm=norm();

        for (int k=0; k<getDimension(); ++k){
            set(k, get(k)/norm);
        }

        return this;
    }

    @Override
    public IVector nNormalize() {
        return this.copy().normalize();
    }

    @Override
    public double cosine(IVector vector) {
        if ( this.getDimension()!=vector.getDimension() ) throw new IncompatibleOperandException();

        return this.scalarProduct(vector)/(this.norm()*vector.norm());
    }

    @Override
    public double scalarProduct(IVector vector) {
        if ( this.getDimension()!=vector.getDimension() ) throw new IncompatibleOperandException();

        double product=0;

        for (int k=0; k<getDimension(); ++k){
            product+=get(k)*vector.get(k);
        }

        return product;
    }

    @Override
    public IVector nVectorProduct(IVector vector) {
        if ( this.getDimension()!=3 || vector.getDimension()!=3 ) throw new IncompatibleOperandException();

        IVector product=newInstance(this.getDimension());

        for (int k=0; k<3; ++k){
            product.set(k, get((k+1)%3)*vector.get((k+2)%3)-get((k+2)%3)*vector.get((k+1)%3));
        }

        return product;
    }

    @Override
    public IVector nFromHomogenous() {
        IVector copy=newInstance(this.getDimension()-1);
        int n=this.getDimension()-1;
        for (int k=0; k<copy.getDimension(); ++k){
            copy.set(k, this.get(k)/(double)n);
        }
        return copy;
    }

    @Override
    public IMatrix toRowMatrix(boolean flag) {
        if (!flag) {
            double[][] arr = new double[1][getDimension()];
            for (int k = getDimension() - 1; k >= 0; --k) {
                arr[0][k] = get(k);
            }
            return new Matrix(1, getDimension(), arr, true);
        }

        return new MatrixVectorView(this, true);
    }

    @Override
    public IMatrix toColumnMatrix(boolean flag) {
        if (!flag) {
            double[][] arr = new double[getDimension()][1];
            for (int k = getDimension() - 1; k >= 0; --k) {
                arr[k][0] = get(k);
            }
            return new Matrix(getDimension(),1, arr, true);
        }

        return new MatrixVectorView(this, false);
    }

    @Override
    public double[] toArray() {
        double[] arr=new double[this.getDimension()];
        for (int k=0; k<this.getDimension(); ++k){
            arr[k]=get(k);
        }
        return arr;
    }

    @Override
    public String toString(){
        StringBuilder str= new StringBuilder("(");

        for (int k=0; k<getDimension()-1; ++k){
            str.append(get(k)).append(",");
        }

        return str.append(get(getDimension() - 1)).append(")").toString();
    }

    @Override
    public double distance(IVector vector){
        if (this.getDimension()!=vector.getDimension() ) throw new IncompatibleOperandException();

        double sum = 0;
        IVector sub = this.nSub(vector);
        for (int k=getDimension()-1; k>=0; --k){
            sum += sub.get(k)*sub.get(k);
        }
        return Math.sqrt(sum);
    }

    @Override
    public boolean equals(Object obj) {
        // return super.equals(obj);
        if (!(obj instanceof AbstractVector other)) return false;
        if (other.getDimension() != this.getDimension()) return false;
        for (int i = 0; i < this.getDimension(); ++i){
            if (other.get(i) != this.get(i)) return false;
        }
        return true;
    }
}
