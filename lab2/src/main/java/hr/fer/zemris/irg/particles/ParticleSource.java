package hr.fer.zemris.irg.particles;

import hr.fer.zemris.util.IVector;

import java.util.List;

public interface ParticleSource {
    void tick(IVector acceleration);
    List<Particle> getParticles();

    IVector getPosition();
    double getParticleDuration(Particle particle);
}
