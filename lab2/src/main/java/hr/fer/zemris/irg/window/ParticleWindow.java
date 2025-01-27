package hr.fer.zemris.irg.window;

import com.sun.jdi.Value;
import hr.fer.zemris.Util;
import hr.fer.zemris.irg.canvas.ParticleCanvas;
import hr.fer.zemris.irg.parser.SceneParser;
import hr.fer.zemris.irg.parser.TextSceneParser;
import hr.fer.zemris.irg.particles.*;
import hr.fer.zemris.util.IMatrix;
import hr.fer.zemris.util.IVector;
import hr.fer.zemris.util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleWindow extends Window {
    private int frame = 0;

    private final int totalFrames;

    private final IVector acceleration;

    private final IVector viewpoint;
    private final IVector eyepoint;
    private final IMatrix transformationMatrix;

    private final java.util.List<IVector> axes=new ArrayList<>();
    private final List<IVector> axesToPrint=new ArrayList<>();

    private final ParticleCanvas canvas;

    private final ParticleSource[] particleSources;

    public ParticleWindow(IVector acceleration, int totalFrames, IVector viewpoint, IVector eyepoint, ParticleSource[] sources, IColoriser coloriser, IPainter painter) {
        super("Particle window");
        this.getContentPane().setLayout(new BorderLayout());

        axes.add(new Vector(new double[]{-100, 0, 0, 1}));
        axes.add(new Vector(new double[]{100, 0, 0, 1}));

        axes.add(new Vector(new double[]{0, -100, 0, 1}));
        axes.add(new Vector(new double[]{0, 100, 0, 1}));

        axes.add(new Vector(new double[]{0, 0, -100, 1}));
        axes.add(new Vector(new double[]{0, 0, 100, 1}));

        axesToPrint.addAll(axes);

        this.totalFrames = totalFrames;

        this.viewpoint = viewpoint;
        this.eyepoint = eyepoint;
        transformationMatrix = Util.generateTransform(eyepoint, viewpoint);

        this.acceleration = acceleration;
        this.particleSources = sources;

        canvas = new ParticleCanvas(eyepoint, axesToPrint, coloriser, painter);
        this.getContentPane().add(canvas, BorderLayout.CENTER);

        transformAxes();
        this.adjustCoords(axesToPrint);
    }

    private void transformAxes(){
        for (int k=0; k<axes.size(); ++k){
            IVector vertex = axes.get(k);
            vertex = vertex.toRowMatrix(false).nMultiply(transformationMatrix).toVector(false);
            axesToPrint.set(k, vertex);
        }
    }

    private void transformParticles(List<Particle> particles){
        for (Particle particle : particles) {
            IVector vertex = particle.getPosition();
            vertex.set(2, -vertex.get(2));
            vertex = vertex.toRowMatrix(false).nMultiply(transformationMatrix).toVector(false);
            particle.setPosition(vertex);
            // particles.set(k, vertex);
        }
    }

    public void step() throws AnimationFinishedException {
        if (frame>=totalFrames) throw new AnimationFinishedException();

        List<Particle> particles = new ArrayList<>();
        for (var k:particleSources){
            particles.addAll(k.getParticles());
            k.tick(this.acceleration);
        }
        transformParticles(particles);
        adjustParticles(particles);

        canvas.setParticles(particles);

        repaint();

        frame++;
    }

    private void adjustParticles(List<Particle> particles){
        IVector adjustment = new Vector(new double[]{1, 1, 1, 1});
        for (var k:particles){
            k.getPosition().add(adjustment).scalarMultiply(0.5);
//            position.set(0, (position.get(0)+1)/2);
//            position.set(1, (position.get(1)+1)/2);
//            position.set(2, (position.get(2)+1)/2);
        }
    }

    public static void main(String[] args) {
        SceneParser sceneParser = new TextSceneParser();
        sceneParser.parse();

        if (sceneParser.getViewpoint()==null || sceneParser.getEyepoint()==null) {
            System.out.println("improperly set parameters.");
            return;
        }

        Random r = new Random();

        int framerate = 30;
        int duration = 15;

        /* vodena fontana
        double maxLife = 150;
        IVector acceleration = new Vector(new double[]{0., 0., -0.08, 0});
        IVector startSpeed = new Vector(new double[]{0, 0, 0.4, 0});

        IVector position = new Vector(new double[]{0, 0, 0, 1});
        UnaryFunction[] speedFunctions = new UnaryFunction[3];
        speedFunctions[0] = value -> value+r.nextDouble()/1.5-0.5;
        speedFunctions[1] = speedFunctions[0];
        speedFunctions[2] = value -> value;
        ITimer timer = new UnaryTimer(maxLife, value -> value);
        ParticleGenerator generator = new UniformParticleGenerator(30);
        ParticleSource source = new DotSource(position.copy().set(0, 1), generator, startSpeed, speedFunctions, timer);

        ParticleSource source2 = new DotSource(position.copy().set(1, 1), generator, startSpeed, speedFunctions, timer);

        UnaryFunction[] colorFunctions = new UnaryFunction[3];
        colorFunctions[0] = value -> 0;
        colorFunctions[1] = value -> (maxLife-value)/(maxLife);
        colorFunctions[2] = value -> 1;
        IColoriser coloriser = new InterpolatedColoriser(0xff, 0xffffff, colorFunctions);

        IPainter painter = new DotPainter();

        ParticleWindow window = new ParticleWindow(acceleration, framerate*duration, sceneParser.getViewpoint(), sceneParser.getEyepoint(), new ParticleSource[]{source, source2}, coloriser, painter);
        */

        // kirby dots
        double maxLife = 150;
        IVector acceleration = new Vector(new double[]{0, 0, 0.02, 0});
        IVector startSpeed = new Vector(new double[]{0, 0, 0.02, 0});

        IVector position = new Vector(new double[]{0, 0, 0, 1});
        UnaryFunction[] speedFunctions = new UnaryFunction[3];
        speedFunctions[0] = value -> value+r.nextDouble()/1.5-0.5;
        speedFunctions[1] = speedFunctions[0];
        speedFunctions[2] = speedFunctions[0];
        ITimer timer = new UnaryTimer(maxLife, value -> value);
        ParticleGenerator generator = new UniformParticleGenerator(30);
        ParticleSource source = new DotSource(position, generator, startSpeed, speedFunctions, timer);

        UnaryFunction[] colorFunctions = new UnaryFunction[3];
        colorFunctions[0] = value -> 0;
        colorFunctions[1] = value -> (maxLife-value)/(maxLife);
        colorFunctions[2] = value -> 1;
        IColoriser coloriser = new InterpolatedColoriser(0x0, 0xff0000, colorFunctions);

        IPainter painter = new DotPainter();

        ParticleWindow window = new ParticleWindow(acceleration, framerate*duration, sceneParser.getViewpoint(), sceneParser.getEyepoint(), new ParticleSource[]{source}, coloriser, painter);


        /* plamen
        double maxLife = 20;
        IVector acceleration = new Vector(new double[]{0.0, 0.0, -0.001, 0});
        IVector startSpeed = new Vector(new double[]{0, 0, 0.01, 0});

        IVector position = new Vector(new double[]{-0.2, -0.2, 0, 1});
        UnaryFunction[] speedFunctions = new UnaryFunction[3];
        speedFunctions[0] = value -> value+r.nextDouble()/2. - 0.5/2.;
        speedFunctions[1] = speedFunctions[0];
        speedFunctions[2] = value -> value;
        ITimer timer = new UnaryTimer(maxLife, value -> value);
        ParticleGenerator generator = new UniformParticleGenerator(30);
        ParticleSource source = new DotSource(position, generator, startSpeed, speedFunctions, timer);

        UnaryFunction[] colorFunctions = new UnaryFunction[3];
        colorFunctions[0] = value -> Math.pow((maxLife-value)/(maxLife), 0.5);
        colorFunctions[1] = colorFunctions[0];
        colorFunctions[2] = colorFunctions[0];
        IColoriser coloriser = new InterpolatedColoriser(0x0000ff, 0xffbb00, colorFunctions);

        IPainter painter = new DotPainter();

        ParticleWindow window = new ParticleWindow(acceleration, framerate*duration, sceneParser.getViewpoint(), sceneParser.getEyepoint(), new ParticleSource[]{source}, coloriser, painter);

         */
        window.setVisible(true);


        while (true){
            try {
                window.step();
                Thread.sleep((long) 1000 / framerate);
            }
            catch (AnimationFinishedException e) {
                System.out.println("Done");
                return;
            }
            catch (InterruptedException ignored) {}
        }
    }
}
