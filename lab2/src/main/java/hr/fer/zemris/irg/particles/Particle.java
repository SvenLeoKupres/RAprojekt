package hr.fer.zemris.irg.particles;

import hr.fer.zemris.util.IVector;
import hr.fer.zemris.util.Vector;

public class Particle {
    private IVector position;
    private IVector velocity;
    private double life;

    public Particle() {
        this(new Vector(new double[]{0, 0, 0, 1}), new Vector(new double[]{0, 0, 0, 0}), 0);
    }

    public Particle(double life) {
        this(new Vector(new double[]{0, 0, 0, 1}), new Vector(new double[]{0, 0, 0, 0}), life);
    }

    public Particle(IVector position) {
        this(position, new Vector(new double[]{0, 0, 0, 0}), 0);
    }

    public Particle(IVector position, IVector velocity, double life) {
        this.position = position;
        this.velocity = velocity;
        this.life = life;
    }

    public IVector getPosition() {
        return position;
    }

    public IVector getVelocity() {
        return velocity;
    }

    public void setPosition(IVector position) {
        this.position = position;
    }

    public void setVelocity(IVector velocity) {
        this.velocity = velocity;
    }

    public void tick(IVector acc){
        life--;
        position.add(velocity);
        velocity.add(acc);
    }

    public void setLife(double life) {
        this.life = life;
    }

    public double getLife() {
        return life;
    }

    public Particle copy() {
        return new Particle(position.copy(), velocity.copy(), life);
    }
}
