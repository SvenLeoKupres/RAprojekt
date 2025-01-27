package hr.fer.zemris.irg.particles;

import java.util.Random;

public class UnaryFunctionTimer implements ITimer {
    private UnaryFunction function;
    private Random r = new Random();

    public UnaryFunctionTimer(UnaryFunction function) {
        this.function = function;
    }

    @Override
    public double lifespan() {
        return function.accept(r.nextDouble());
    }
}
