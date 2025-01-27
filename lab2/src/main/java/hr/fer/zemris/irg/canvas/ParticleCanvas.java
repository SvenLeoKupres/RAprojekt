package hr.fer.zemris.irg.canvas;

import hr.fer.zemris.irg.particles.IColoriser;
import hr.fer.zemris.irg.particles.IPainter;
import hr.fer.zemris.irg.particles.Particle;
import hr.fer.zemris.util.IVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ParticleCanvas extends Canvas {
    
    private final IColoriser coloriser;
    private final IPainter painter;

    private List<Particle> particles = new ArrayList<>();

    public ParticleCanvas(IVector eyepoint, List<IVector> axes, IColoriser coloriser, IPainter painter) {
        super(null, eyepoint, axes);

        this.coloriser = coloriser;
        this.painter = painter;
    }
    
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        this.paintAxes(g2d);

        int width = getWidth();
        int height = getHeight();

        // = new int[]{width/100, height/100};

        for (Particle particle : particles) {
            g2d.setColor(coloriser.calculateColor(particle.getLife()));
            double scale = particle.getPosition().distance(this.getEyepoint());
            int[] size = new int[]{(int) ((double) width /150 * scale), (int) ((double) height /150 * scale)};
            // size[0] = (int) (size[0]*));
            int[] position = new int[]{(int) (particle.getPosition().get(0)*width - (double) size[0] /2), (int) (particle.getPosition().get(1)*height - (double) size[1] /2)};

            painter.paint(g2d, position, size);
        }
    }

    public void setParticles(List<Particle> particles) {
        this.particles = particles;
    }
}
