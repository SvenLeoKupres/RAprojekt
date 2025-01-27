package hr.fer.zemris.irg.canvas;

import hr.fer.zemris.Util;
import hr.fer.zemris.util.IVector;
import hr.fer.zemris.util.Vector;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class CanvasGouraud extends Canvas {
    private final static double sample = 0.001;
    // private final IVector eyepoint;
    private final java.util.List<IVector> vertices;
    private final java.util.List<IVector> polygons;
    private final java.util.List<IVector> axes;
    //private final IVector lightSource;
    //private final Map<Integer, IVector> normals;
    private final Map<Integer, Double> diffusion;

    public CanvasGouraud(java.util.List<IVector> vertices, java.util.List<IVector> polygons, List<IVector> axes, IVector eyepoint, Map<Integer, Double> diffusion){
        super(vertices, eyepoint, axes);

        // this.eyepoint = eyepoint;
        this.vertices = vertices;
        this.polygons = polygons;
        this.axes = axes;
        //this.lightSource = lightSource;
        this.diffusion = diffusion;
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        //for (var k:polygons) calculateLight(k);

        int height=getHeight();
        int width=getWidth();

        paintAxes(g2d);

        //int index=0;
        for (var polygon: polygons){
            if (checkVisible(polygon)) {
                //calculateLight(polygon);
                //IVector v1, v2;
                //double intensity = diffusion.get(polygon);
                //if (intensity==0) continue;
                //g2d.setColor(new Color((float) intensity/255, (float) intensity/255, (float) intensity/255));
                //System.out.println(g2d.getColor());
                fillTriangle(g2d, polygon, height, width);
            }
        }
    }

    private void fillTriangle(Graphics2D g2d, IVector polygon, int height, int width) {

        int index1 = (int) polygon.get(0);
        int index2 = (int) polygon.get(1);
        int index3 = (int) polygon.get(2);

        IVector t1 = vertices.get(index1);
        IVector t2 = vertices.get(index2);
        IVector t3 = vertices.get(index3);

        double i1 = diffusion.get(index1);
        double i2 = diffusion.get(index2);
        double i3 = diffusion.get(index3);

        int[] xCoords = new int[3];
        int[] yCoords = new int[3];

        xCoords[0] = (int) (t1.get(0)*width);
        xCoords[1] = (int) (t2.get(0)*width);
        xCoords[2] = (int) (t3.get(0)*width);

        yCoords[0] = (int) (height - t1.get(1)*height);
        yCoords[1] = (int) (height - t2.get(1)*height);
        yCoords[2] = (int) (height - t3.get(1)*height);

        /*
        IMatrix matrix = new Matrix(3, 3, new double[][]{
                {xCoords[0], xCoords[1], xCoords[2]},
                {yCoords[0], yCoords[1], yCoords[2]},
                {1, 1, 1}
        }, true);
         */

        for (double k = 0; k<=1; k += sample){
            for (double i = 0; i<=1; i += sample){
                for (double j = 0; j<=1; j += sample){
                    if (k+i+j < 0.9999999 || k+i+j>1.0000001) continue;
                    //IVector bars = new Vector(new double[]{k, i, j});
                    //IVector t = matrix.nMultiply(bars.toColumnMatrix(true)).toVector(true);
                    int x = (int) (xCoords[0] * k + xCoords[1] * i + xCoords[2] * j);
                    int y = (int) (yCoords[0] * k + yCoords[1] * i + yCoords[2] * j);

                    double intensity = i1 * k + i2 * i + i3 * j;
                    g2d.setColor(new Color((float) intensity / 255, (float) intensity / 255, (float) intensity / 255));
                    //g2d.drawLine((int) x*width,(int) (height - y*height), (int) (x*width), (int) (height - y*height));
                    g2d.drawLine(x, y, x, y);
                    //System.out.println("Any second now");
                }
            }
        }
        // System.out.println("Done");

        //g2d.drawLine(xCoords[0], yCoords[0], xCoords[1], yCoords[1]);
        //g2d.drawLine(xCoords[1], yCoords[1], xCoords[2], yCoords[2]);
        //g2d.drawLine(xCoords[2], yCoords[2], xCoords[0], yCoords[0]);
        //g2d.fillPolygon(new Polygon(xCoords, yCoords, 3));
    }


    private IVector directionVector(IVector v1, IVector v2, IVector v3, IVector source){
        IVector n = new Vector(new double[3]);
        n.set(0, (v1.get(0) + v2.get(0) + v3.get(0)) / 3 - source.get(0));
        n.set(1, (v1.get(1) + v2.get(1) + v3.get(1)) / 3 - source.get(1));
        n.set(2, (v1.get(2) + v2.get(2) + v3.get(2)) / 3 - source.get(2));

        return n;
    }

}
