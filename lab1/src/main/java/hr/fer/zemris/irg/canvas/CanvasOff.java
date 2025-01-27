package hr.fer.zemris.irg.canvas;

import hr.fer.zemris.Util;
import hr.fer.zemris.util.IVector;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CanvasOff extends Canvas {
    private final List<IVector> vertices;
    private final List<IVector> polygons;
    private final List<IVector> axes;
    // private IVector eyepoint;
    public CanvasOff(List<IVector> vertices, List<IVector> polygons, List<IVector> axes, IVector eyepoint){
        super(vertices, eyepoint, axes);
        this.vertices=vertices;
        this.polygons=polygons;
        this.axes=axes;
        // this.eyepoint=eyepoint;
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        int height=getHeight();
        int width=getWidth();

        paintAxes(g2d);

        for (var polygon: polygons){
            Util.drawLine(g2d, height, width, vertices.get((int) polygon.get(0)), vertices.get((int) polygon.get(1)));
            Util.drawLine(g2d, height, width, vertices.get((int) polygon.get(1)), vertices.get((int) polygon.get(2)));
            Util.drawLine(g2d, height, width, vertices.get((int) polygon.get(2)), vertices.get((int) polygon.get(0)));
        }
    }

}
