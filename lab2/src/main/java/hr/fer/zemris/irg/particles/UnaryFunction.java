package hr.fer.zemris.irg.particles;

public interface UnaryFunction {
    /**
     *
     * @param value number between 0 and 1
     * @return some function applied to the given value
     */
    double accept(double value);
}
