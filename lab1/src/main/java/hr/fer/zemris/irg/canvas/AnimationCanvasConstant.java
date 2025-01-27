package hr.fer.zemris.irg.canvas;

import hr.fer.zemris.util.IVector;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class AnimationCanvasConstant extends CanvasConstant {
    List<IVector> curve;
    private boolean drawing=false;
    public AnimationCanvasConstant(List<IVector> curve, List<IVector> vertices, List<IVector> polygons, List<IVector> axes, IVector eyepoint, Map<IVector, Double> light) {
        super(vertices, polygons, axes, eyepoint, light);
        this.curve = curve;
    }

    @Override
    public void paintComponent(Graphics g) {
        for (int k = 0; k < curve.size()-1; k++) {
            IVector p1 = curve.get(k).copy();
            IVector p2 = curve.get(k+1).copy();
            g.drawLine((int) (p1.get(0)*getWidth()), (int) (p1.get(1)*getHeight()), (int) (p2.get(0)*getWidth()), (int) (p2.get(1)*getHeight()));
        }
        super.paintComponent(g);

        // paintAxes((Graphics2D) g);
        drawing = false;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public void setDrawing(boolean drawing) {
        this.drawing = drawing;
    }
}
