package hr.fer.zemris.irg.particles;

import java.util.Random;

public class UnaryTimer implements ITimer {
    private static final Random r = new Random();
    private final UnaryFunction function;
    private final double maxLength;

    public UnaryTimer(double maxLength, UnaryFunction function) {
        this.function = function;
        this.maxLength = maxLength;
    }

    @Override
    public double lifespan() {
        return function.accept(r.nextDouble()*maxLength);
    }
}
