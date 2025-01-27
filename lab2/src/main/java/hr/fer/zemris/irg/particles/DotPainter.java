package hr.fer.zemris.irg.particles;

import hr.fer.zemris.util.IVector;

import java.awt.*;

public class DotPainter implements IPainter {
    @Override
    public void paint(Graphics2D g, int[] point, int[] size) {
        g.fillOval(point[0], point[1], size[0], size[1]);
    }
}
