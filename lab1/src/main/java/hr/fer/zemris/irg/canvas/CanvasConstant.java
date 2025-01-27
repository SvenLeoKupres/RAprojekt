package hr.fer.zemris.irg.canvas;

import hr.fer.zemris.util.IVector;
import hr.fer.zemris.util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CanvasConstant extends Canvas {

    private final IVector eyepoint;
    private final List<IVector> vertices;
    private final List<IVector> polygons;
    private final Map<IVector, Double> diffusion;

    public CanvasConstant(List<IVector> vertices, List<IVector> polygons, List<IVector> axes, IVector eyepoint, Map<IVector, Double> diffusion){
        super(vertices, eyepoint, axes);

        this.eyepoint = eyepoint;
        this.vertices=vertices;
        this.polygons=polygons;
        this.diffusion=diffusion;
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        int height=getHeight();
        int width=getWidth();

        paintAxes(g2d);

        if (vertices.isEmpty()) {return;}

        // List<IVector> polygons = orderPolygons();

        for (var polygon: polygons){
            if (checkVisible(polygon)) {
                double intensity = diffusion.get(polygon);
                g2d.setColor(new Color((float) intensity / 255, (float) intensity / 255, (float) intensity / 255));
                fillTriangle(g2d, polygon, height, width);
            }
        }
    }

    private void fillTriangle(Graphics2D g2d, IVector polygon, int height, int width) {
        int[] xCoords = new int[3];
        int[] yCoords = new int[3];
        IVector t1 = vertices.get((int) polygon.get(0));
        IVector t2 = vertices.get((int) polygon.get(1));
        IVector t3 = vertices.get((int) polygon.get(2));
        xCoords[0] = (int) (t1.get(0)*width);
        xCoords[1] = (int) (t2.get(0)*width);
        xCoords[2] = (int) (t3.get(0)*width);

        yCoords[0] = (int) (t1.get(1)*height);
        yCoords[1] = (int) (t2.get(1)*height);
        yCoords[2] = (int) (t3.get(1)*height);
        g2d.fillPolygon(new Polygon(xCoords, yCoords, 3));

//        g2d.drawLine(xCoords[0], yCoords[0], xCoords[1], yCoords[1]);
//        g2d.drawLine(xCoords[1], yCoords[1], xCoords[2], yCoords[2]);
//        g2d.drawLine(xCoords[2], yCoords[2], xCoords[0], yCoords[0]);
    }

    private List<IVector> orderPolygons(){
        List<IVector> polygons = new ArrayList<>(this.polygons);
        return polygons.stream().sorted((o1, o2) -> {
            IVector point1 = new Vector(new double[]{
                    (vertices.get((int) o1.get(0)).get(0) + vertices.get((int) o1.get(1)).get(0) + vertices.get((int) o1.get(2)).get(0)) / 3,
                    (vertices.get((int) o1.get(0)).get(1) + vertices.get((int) o1.get(1)).get(1) + vertices.get((int) o1.get(2)).get(1)) / 3,
                    (vertices.get((int) o1.get(0)).get(2) + vertices.get((int) o1.get(1)).get(2) + vertices.get((int) o1.get(2)).get(2)) / 3});
            IVector point2 = new Vector(new double[]{
                    (vertices.get((int) o2.get(0)).get(0) + vertices.get((int) o2.get(1)).get(0) + vertices.get((int) o2.get(2)).get(0)) / 3,
                    (vertices.get((int) o2.get(0)).get(1) + vertices.get((int) o2.get(1)).get(1) + vertices.get((int) o2.get(2)).get(1)) / 3,
                    (vertices.get((int) o2.get(0)).get(2) + vertices.get((int) o2.get(1)).get(2) + vertices.get((int) o2.get(2)).get(2)) / 3});

            double d1 = point1.distance(eyepoint.copyPart(3)), d2 = point2.distance(eyepoint.copyPart(3));
            if (d1 == d2) return 0;
            return point1.distance(eyepoint.copyPart(3)) < point2.distance(eyepoint.copyPart(3)) ? -1 : 1;
        }).toList();
    }

}
