package hr.fer.zemris.irg.canvas;

import hr.fer.zemris.Util;
import hr.fer.zemris.util.IVector;
import hr.fer.zemris.util.Vector;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public abstract class Canvas extends JComponent {
    private final List<IVector> vertices;
    private final IVector eyepoint;
    private final List<IVector> axes;
    public Canvas(List<IVector> vertices, IVector eyepoint, List<IVector> axes) {
        this.vertices = vertices;
        this.eyepoint = eyepoint;
        this.axes = axes;
    }

    @Override
    public void paint(Graphics g){
        paintAxes((Graphics2D) g);
    }

    public boolean checkVisible(IVector polygon){
        int index = (int) polygon.get(0);
//        if (index < 0 || index >= vertices.size()) {
//            throw new IllegalArgumentException("Invalid index: " + index + " list size: " + vertices.size());
//        }
        IVector v1 = vertices.get(index);
        index = (int) polygon.get(1);
//        if (index < 0 || index >= vertices.size()) {
//            throw new IllegalArgumentException("Invalid index: " + index + " list size: " + vertices.size());
//        }
        IVector v2 = vertices.get(index);
        index = (int) polygon.get(2);
//        if (index < 0 || index >= vertices.size()) {
//            throw new IllegalArgumentException("Invalid index: " + index + " list size: " + vertices.size());
//        }
        IVector v3 = vertices.get(index);

        IVector np = v1.nSub(v2).copyPart(3).nVectorProduct(v1.nSub(v3).copyPart(3));
        //np.scalarMultiply(-1);
        IVector n = new Vector(new double[3]);
        n.set(0, (v1.get(0)+v2.get(0)+v3.get(0))/3 - eyepoint.get(0));
        n.set(1, (v1.get(1)+v2.get(1)+v3.get(1))/3 - eyepoint.get(1));
        n.set(2, (v1.get(2)+v2.get(2)+v3.get(2))/3 - eyepoint.get(2));
        //n.scalarMultiply(-1);

        double check = np.scalarProduct(n);

        return check>0;
        /*
        if (check>0){
            tmpPolygons.add(polygon);
            //calculateLight(v1, v2, v3, np, polygon);
        }

         */
    }

    public IVector getEyepoint(){
        return eyepoint;
    }

    void paintAxes(Graphics2D g){
        int height=getHeight();
        int width=getWidth();

        g.setColor(Color.BLUE);
        Util.drawLine(g, height, width, axes.get(0), axes.get(1));
        g.setColor(Color.GREEN);
        Util.drawLine(g, height, width, axes.get(2), axes.get(3));
        g.setColor(Color.RED);
        Util.drawLine(g, height, width, axes.get(4), axes.get(5));
        g.setColor(Color.BLACK);
    }
}
