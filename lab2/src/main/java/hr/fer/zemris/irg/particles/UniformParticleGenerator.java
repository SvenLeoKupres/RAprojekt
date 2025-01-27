package hr.fer.zemris.irg.particles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UniformParticleGenerator implements ParticleGenerator {
    private static final Random rand = new Random();
    private int average;

    public UniformParticleGenerator(int average) {
        this.average = average;
    }

    @Override
    public List<Particle> generate() {

        double r = rand.nextDouble();

        int num = (int) (2*average*r + 0.5);
//        if (r < 0.5) num = (int) (average*r + 0.5);
//        else num = (int) (average*(1+r) + 0.5);
        List<Particle> result = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            result.add(new Particle());
        }
        return result;
    }

    public void setAverage(int average) {
        this.average = average;
    }
}
