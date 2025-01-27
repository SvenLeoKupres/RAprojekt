package hr.fer.zemris.irg.parser;

import hr.fer.zemris.irg.particles.IColoriser;
import hr.fer.zemris.irg.particles.ITimer;
import hr.fer.zemris.irg.particles.ParticleSource;

public interface ParticleParser extends IParser {
    ParticleSource getParticleSource();
    IColoriser getColoriser();
    ITimer getTimer();
}
