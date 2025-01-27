package hr.fer.zemris.irg.window;

import hr.fer.zemris.Util;
import hr.fer.zemris.irg.Object3d;
import hr.fer.zemris.irg.canvas.AnimationCanvasConstant;
import hr.fer.zemris.irg.parser.*;
import hr.fer.zemris.util.IMatrix;
import hr.fer.zemris.util.IVector;
import hr.fer.zemris.util.Matrix;
import hr.fer.zemris.util.Vector;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AnimationWindow extends Window {
    private static final int ANIMATION_TIME = 30; //measured in seconds
    private static final int FRAMERATE = 30;      //fps

    private final IVector viewpoint;
    private final IVector eyepoint;

    private final JComponent canvas;

    private final List<IVector> axes=new ArrayList<>();
    private final List<IVector> axesToPrint=new ArrayList<>();

    private final IVector lightSource;
    private final String lighting;

    private final Object3d object;
    private final List<IVector> verticesToPrint=new ArrayList<>();

    private final List<IVector> curveVertices;  //vertices defining the curve
    private  List<IVector> curve;               //all the points used in the curve
    private List<Double> t_values;              //all the values of t in the curve
    private final List<IVector> curveToDraw=new ArrayList<>();  //transformed curve points, used to draw it
    private final IVector s = new Vector(new double[]{0, 0, 1});                      //starting orientation of the object

    public AnimationWindow(IVector viewpoint, IVector eyepoint, IVector lightSource, String lighting, Object3d object, List<IVector> curveVertices) {
        super("Animation");
        this.getContentPane().setLayout(new BorderLayout());

        this.viewpoint = viewpoint;
        this.eyepoint = eyepoint;
        this.lightSource = lightSource;
        this.lighting = lighting;
        this.object = object;
        verticesToPrint.addAll(object.getVertices());
        this.curveVertices = curveVertices;

        axes.add(new Vector(new double[]{-100, 0, 0, 1}));
        axes.add(new Vector(new double[]{100, 0, 0, 1}));

        axes.add(new Vector(new double[]{0, -100, 0, 1}));
        axes.add(new Vector(new double[]{0, 100, 0, 1}));

        axes.add(new Vector(new double[]{0, 0, -100, 1}));
        axes.add(new Vector(new double[]{0, 0, 100, 1}));

        axesToPrint.addAll(axes);

        normalizeCoords();

        createCurve();

        transformStatic();

        if (lighting.equals("constant")) {
            Map<IVector, Double> diffusion = new HashMap<>();
            for (var k:object.getPolygons()) {
                calculateLight(k, diffusion);
            }
            canvas = new AnimationCanvasConstant(curveToDraw, verticesToPrint, object.getPolygons(), axesToPrint, eyepoint, diffusion);
        }
//        else if (lighting.equals("gouraud")) {
//            // Map<Integer, IVector> map = calculateNormals(verticesNormal);
//            Map<Integer, Double> diffusion = new HashMap<>();
//            for (var k:object.getPolygons()) {
//                calculateLightGouraud(k, object.getNormals(), diffusion);
//            }
//            canvas = new CanvasGouraud(verticesToPrint, object.getPolygons(), axesToPrint, eyepoint, diffusion);
//        }
//        else if (lighting.equals("off")) {
//            canvas = new CanvasOff(verticesToPrint, object.getPolygons(), axesToPrint, eyepoint);
//        }
        else throw new RuntimeException("Illegal lighting method");

        canvas.setBackground(Color.WHITE);

        getContentPane().add(canvas, BorderLayout.CENTER);
    }

    private void normalizeCoords() {
        object.normalize();
        object.scale(0.1);
        Util.normalize(curveVertices);
    }

    private void createCurve(){
        t_values = new ArrayList<>();
        curve = new ArrayList<>();

        int num_of_frames = ANIMATION_TIME*FRAMERATE;                           // total number of frames
        int num_of_segments = curveVertices.size()-3;                                   // number of curve segments
        double num_per_segment = (double) num_of_frames / num_of_segments;       // amount of frames per segment
        IMatrix B = new Matrix(4, 4, new double[][]{
                {-1/6., 0.5, -0.5, 1/6.},
                {0.5, -1., 0.5, 0},
                {-0.5, 0, 0.5, 0},
                {1/6., 2/3., 1/6., 0}
        }, true);
        for (int k = 0; k< curveVertices.size()-3; ++k) {
            IMatrix R = new Matrix(4, 4, new double[][]{
                    {curveVertices.get(k).get(0), curveVertices.get(k).get(1), curveVertices.get(k).get(2), 1},
                    {curveVertices.get(k+1).get(0), curveVertices.get(k+1).get(1), curveVertices.get(k+1).get(2), 1},
                    {curveVertices.get(k+2).get(0), curveVertices.get(k+2).get(1), curveVertices.get(k+2).get(2), 1},
                    {curveVertices.get(k+3).get(0), curveVertices.get(k+3).get(1), curveVertices.get(k+3).get(2), 1}
            }, true);
            for (double t = 0; t < 1; t += (1 / num_per_segment)) {
                IVector T = new Vector(new double[]{t * t * t, t * t, t, 1});
                curve.add(T.toRowMatrix(true).nMultiply(B).nMultiply(R).toVector(true));
                curve.get(curve.size()-1).set(3, 1);
                if (t_values.size()<num_per_segment) t_values.add(t);
            }
        }
        curveToDraw.addAll(curve);
    }

    private void transformStatic(){
        IMatrix matrix = Util.generateTransform(eyepoint, viewpoint);

        for (int k=0; k<curveToDraw.size(); ++k){
            IVector vertex = curveToDraw.get(k);
            vertex = vertex.toRowMatrix(false).nMultiply(matrix).toVector(false);
            curveToDraw.set(k, vertex);
        }

        IMatrix additional = new Matrix(4, 4, new double[][]{
                {1, 0, 0, 0},
                {0, -1, 0, 0},
                {0, 0, -1, 0},
                {0, 0, 0, 1}
        }, true);

        for (int k=0; k<axes.size(); ++k){
            IVector vertex = axes.get(k);
            vertex = vertex.toRowMatrix(false).nMultiply(matrix).toVector(false);
            if (Objects.equals(lighting, "constant")) vertex = vertex.toRowMatrix(false).nMultiply(additional).toVector(false);
            axesToPrint.set(k, vertex);
        }

        adjustCoords(axesToPrint);
        adjustCoords(curveToDraw);
    }


    private void transformView() {
        IMatrix matrix = Util.generateTransform(eyepoint, viewpoint);

        for (int k=0; k<verticesToPrint.size(); ++k){
            IVector vertex = verticesToPrint.get(k);
            vertex = vertex.toRowMatrix(false).nMultiply(matrix).toVector(false);
            // vertex.set(3, 1);
            verticesToPrint.set(k, vertex);
        }

        adjustCoords(verticesToPrint);
    }

    private void calculateLight(IVector polygon, Map<IVector, Double> diffusion){
        IVector v1 = object.getVertices().get((int) polygon.get(0));
        IVector v2 = object.getVertices().get((int) polygon.get(1));
        IVector v3 = object.getVertices().get((int) polygon.get(2));

        IVector np = v1.nSub(v2).copyPart(3).nVectorProduct(v1.nSub(v3).copyPart(3));

        IVector n = new Vector(new double[3]);
        n.set(0, (v1.get(0) + v2.get(0) + v3.get(0)) / 3 - lightSource.get(0));
        n.set(1, (v1.get(1) + v2.get(1) + v3.get(1)) / 3 - lightSource.get(1));
        n.set(2, (v1.get(2) + v2.get(2) + v3.get(2)) / 3 - lightSource.get(2));
        //n.scalarMultiply(-1);

        double cos = np.cosine(n);

        double ambience = lightSource.get(3) * lightSource.get(4);
        double diff = cos * lightSource.get(5) * lightSource.get(6);
        diffusion.put(polygon, Math.max(0, ambience + diff));
        //System.out.println(diffusion.get(polygon));
    }

    public void startAnimation(){
        boolean start=true;
        long delay = ANIMATION_TIME* 1000L / ((long) t_values.size() *(curveVertices.size()-3));
        Iterator<Double> it = t_values.iterator();
        int segment = 0;
        for (int k=0; k<curve.size(); ++k){
            if (!it.hasNext()) {
                it = t_values.iterator();
                segment++;
            }
            while (((AnimationCanvasConstant) this.canvas).isDrawing()) {
                try {
                    Thread.sleep(1);
                }
                catch (InterruptedException ignored) {}
            }
            step(k, it.next(), segment);
            ((AnimationCanvasConstant) this.canvas).setDrawing(true);
            canvas.repaint();
            if (start) {
                setVisible(true);
                start = false;
            }
            try {
                Thread.sleep(delay);
            }
            catch (InterruptedException ignored) {}

             // System.out.println("Done");
        }
    }

    private void step(int k, double t, int segment){
        verticesToPrint.clear();
        IVector vertex = curve.get(k);
        IVector T = new Vector(new double[]{t * t, t, 1});
        IMatrix B = new Matrix(3, 4, new double[][]{
                {-0.5, 1.5, -1.5, 0.5},
                {1, -2, 1, 0},
                {-0.5, 0, 0.5, 0}
        }, true);
        IMatrix R = new Matrix(4, 4, new double[][]{
                {curveVertices.get(segment).get(0), curveVertices.get(segment).get(1), curveVertices.get(segment).get(2), 1},
                {curveVertices.get(segment + 1).get(0), curveVertices.get(segment + 1).get(1), curveVertices.get(segment + 1).get(2), 1},
                {curveVertices.get(segment + 2).get(0), curveVertices.get(segment + 2).get(1), curveVertices.get(segment + 2).get(2), 1},
                {curveVertices.get(segment + 3).get(0), curveVertices.get(segment + 3).get(1), curveVertices.get(segment + 3).get(2), 1}
        }, true);

        IVector tangent = T.toRowMatrix(false).nMultiply(B).nMultiply(R).toVector(false).copyPart(3); //vektor tangente iz tocke "vertex"
        IVector os = s.nVectorProduct(tangent).normalize();
        double cos = tangent.cosine(s);

        for (var i:object.getVertices()){
            verticesToPrint.add(i.copy());
        }
        transformObject(cos, os, vertex);

        transformView();
    }

    private void transformObject(double cos, IVector os, IVector point){
        double sin = Math.sqrt(1-cos*cos);

        for (int k=0; k<verticesToPrint.size(); ++k){
            IVector vertex = verticesToPrint.get(k);
            vertex = Util.rodriguesFormula(vertex.copyPart(3), os.copyPart(3), cos, sin);
            verticesToPrint.set(k, vertex.add(point).set(3, 1));
        }
    }

    public static void main(String[] args){
        SceneParser sceneParser = new TextSceneParser();
        sceneParser.parse();

        ObjectParser objectParser = new ObjParser(args[0]);
        objectParser.parse();
        Object3d object = objectParser.getObject();

        CurveParser curveParser = new TextCurveParser();
        curveParser.parse();
        List<IVector> curve = curveParser.getCurve();

        if (sceneParser.getViewpoint()==null || sceneParser.getEyepoint()==null || object.getVertices().size()<3) {
            System.out.println("improperly set parameters.");
            return;
        }

        AnimationWindow window = new AnimationWindow(sceneParser.getViewpoint(), sceneParser.getEyepoint(), sceneParser.getLightSource(), sceneParser.getLighting(), object, curve);
        window.startAnimation();
    }
}
