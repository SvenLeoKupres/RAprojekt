package hr.fer.zemris.irg.particles;

import hr.fer.zemris.util.IVector;
import hr.fer.zemris.util.Vector;

import java.util.*;

/**
 * Generates particles in a single point</br>
 * speedFunction array has 3 parts, one for each axis. Each part is applied on the matching part of the startingSpeed vector to allow variations
 */
public class DotSource implements ParticleSource {
    // private static final Random r = new Random();

    private int maxParticles;

    private final IVector position;
    private final ParticleGenerator generator;
    private final List<Particle> particles = new ArrayList<>();
    private final IVector startingSpeed;
    private final UnaryFunction[] speedFunction;
    private final ITimer timer;

    public DotSource(IVector position, ParticleGenerator generator, IVector startingSpeed, UnaryFunction[] speedFunction, ITimer timer) {
        this(position, generator, startingSpeed, speedFunction, timer, -1);
    }

    public DotSource(IVector position, ParticleGenerator generator, IVector startingSpeed, UnaryFunction[] speedFunction, ITimer timer, int maxParticles) {
        this.position = position;
        this.generator = generator;
        this.startingSpeed = startingSpeed;
        this.speedFunction = speedFunction;
        this.timer = timer;

        this.maxParticles = maxParticles;
    }

    @Override
    public void tick(IVector acc) {
        for (int k=particles.size()-1; k>=0; k--) {
            particles.get(k).tick(acc);
            if (particles.get(k).getLife()<=0) particles.remove(k);
        }

        List<Particle> newParticles = generator.generate();
        for (Particle particle : newParticles) {
            if (maxParticles>0 && particles.size() < maxParticles) return;

            IVector velocity = new Vector(new double[4]);
            for (int i=0; i<3; ++i) {
                velocity.set(i,  speedFunction[i].accept(startingSpeed.get(i)));
            }
            velocity.set(3, 0);
            particle.setVelocity(velocity);

            particle.setPosition(position.copy());

            particle.setLife(timer.lifespan());
            particles.add(particle);
        }

    }

    @Override
    public List<Particle> getParticles() {
        List<Particle> copy = new ArrayList<>();
        for (Particle particle : particles) {
            copy.add(particle.copy());
        }
        return copy;
    }

    @Override
    public IVector getPosition() {
        return position;
    }

    @Override
    public double getParticleDuration(Particle particle){
        return particle.getLife();
    }

    public void setMaxParticles(int maxParticles) {
        this.maxParticles = maxParticles;
    }
}
