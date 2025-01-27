package hr.fer.zemris.irg.window;

import hr.fer.zemris.Util;
import hr.fer.zemris.irg.Object3d;
import hr.fer.zemris.irg.canvas.CanvasConstant;
import hr.fer.zemris.irg.canvas.CanvasGouraud;
import hr.fer.zemris.irg.canvas.CanvasOff;
import hr.fer.zemris.irg.parser.ObjParser;
import hr.fer.zemris.irg.parser.ObjectParser;
import hr.fer.zemris.irg.parser.SceneParser;
import hr.fer.zemris.irg.parser.TextSceneParser;
import hr.fer.zemris.util.IMatrix;
import hr.fer.zemris.util.IVector;
import hr.fer.zemris.util.Matrix;
import hr.fer.zemris.util.Vector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;

public class PreviewWindow extends Window {
    private final IVector viewpoint;
    private IVector eyepoint;

    private final JComponent canvas;

    private final Object3d object;

    // private final List<IVector> vertices;
    private final List<IVector> verticesToPrint = new ArrayList<>();
    // private final List<IVector> polygons;
    private final List<IVector> axes=new ArrayList<>();
    private final List<IVector> axesToPrint=new ArrayList<>();
    // private final Map<IVector, Double> diffusion = new HashMap<>();
    private final IVector lightSource;
    private final String lighting;
    public PreviewWindow(IVector viewpoint, IVector eyepoint, IVector lightSource, String lighting, Object3d object){
        super("Object preview");
        this.getContentPane().setLayout(new BorderLayout());

        this.viewpoint = viewpoint;
        this.eyepoint = eyepoint;
        this.lightSource = lightSource;
        this.object = object;
        // this.vertices = vertices;
        // this.polygons = polygons;
        this.lighting = lighting;

        verticesToPrint.addAll(object.getVertices());

        axes.add(new Vector(new double[]{-100, 0, 0, 1}));
        axes.add(new Vector(new double[]{100, 0, 0, 1}));

        axes.add(new Vector(new double[]{0, -100, 0, 1}));
        axes.add(new Vector(new double[]{0, 100, 0, 1}));

        axes.add(new Vector(new double[]{0, 0, -100, 1}));
        axes.add(new Vector(new double[]{0, 0, 100, 1}));

        axesToPrint.addAll(axes);

        normalizeCoords();

        transformView();

        if (lighting.equals("constant")) {
            Map<IVector, Double> diffusion = new HashMap<>();
            for (var k:object.getPolygons()) {
                calculateLight(k, diffusion);
            }
            canvas = new CanvasConstant(verticesToPrint, object.getPolygons(), axesToPrint, eyepoint, diffusion);
        }
        else if (lighting.equals("gouraud")) {
            Map<Integer, Double> diffusion = new HashMap<>();
            for (var k:object.getPolygons()) {
                calculateLightGouraud(k, object.getNormals(), diffusion);
            }
            canvas = new CanvasGouraud(verticesToPrint, object.getPolygons(), axesToPrint, eyepoint, diffusion);
        }
        else if (lighting.equals("off")) {
            canvas = new CanvasOff(verticesToPrint, object.getPolygons(), axesToPrint, eyepoint);
        }
        else throw new RuntimeException("Illegal lighting method");

        canvas.setBackground(Color.WHITE);

        setKeyInputs();

        getContentPane().add(canvas, BorderLayout.CENTER);
    }

    private void transformView(){
        IMatrix matrix = Util.generateTransform(eyepoint, viewpoint);

        for (int k=0; k<object.getVertices().size(); ++k){
            IVector vertex = object.getVertices().get(k);
            vertex = vertex.toRowMatrix(false).nMultiply(matrix).toVector(false);
            verticesToPrint.set(k, vertex);

            //System.out.printf("%f, %f\n", vertex.get(0), vertex.get(1));
        }
        //System.out.println();

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

        adjustCoords(verticesToPrint);
        adjustCoords(axesToPrint);
    }

    private void normalizeCoords(){
        object.normalize();
    }

    private void calculateLight(IVector polygon, Map<IVector, Double> diffusion){
        List<IVector> vertices = object.getVertices();
        IVector v1 = vertices.get((int) polygon.get(0));
        IVector v2 = vertices.get((int) polygon.get(1));
        IVector v3 = vertices.get((int) polygon.get(2));

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

    private void calculateLightGouraud(IVector polygon, Map<Integer, IVector> normals, Map<Integer, Double> diffusion){
        for (int k=0; k<3; ++k){
            int index = (int) polygon.get(k);

            if (diffusion.get(index)!=null) continue;

            IVector np = normals.get(index);
            IVector n = verticesToPrint.get(index).copyPart(3).nSub(lightSource.copyPart(3));

            double cos = np.cosine(n);

            double ambience = lightSource.get(3) * lightSource.get(4);
            double diff = cos * lightSource.get(5) * lightSource.get(6);

            double start = diffusion.get(index)==null ? 0 : diffusion.get(index);
            diffusion.put(index, Math.min(255, start+Math.max(0, ambience + diff)));
        }
        //IVector n = directionVector(v1, v2, v3, lightSource);
        //n.scalarMultiply(-1);

        //System.out.println(diffusion);
    }

    private void setKeyInputs(){
        JButton jReset = new JButton();
        jReset.setFocusable(true);
        jReset.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"),
                "doSomething");
        jReset.getActionMap().put("doSomething",
                reset);
        getContentPane().add(jReset);

        JButton jMoveXPositive = new JButton();
        jMoveXPositive.setFocusable(true);
        jMoveXPositive.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"),
                "doSomething");
        jMoveXPositive.getActionMap().put("doSomething",
                moveXPositive);
        getContentPane().add(jMoveXPositive);

        JButton jMoveXNegative = new JButton();
        jMoveXNegative.setFocusable(true);
        jMoveXNegative.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"),
                "doSomething");
        jMoveXNegative.getActionMap().put("doSomething",
                moveXNegative);
        getContentPane().add(jMoveXNegative);

        JButton jMoveYPositive = new JButton();
        jMoveYPositive.setFocusable(true);
        jMoveYPositive.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"),
                "doSomething");
        jMoveYPositive.getActionMap().put("doSomething",
                moveYPositive);
        getContentPane().add(jMoveYPositive);

        JButton jMoveYNegative = new JButton();
        jMoveYNegative.setFocusable(true);
        jMoveYNegative.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"),
                "doSomething");
        jMoveYNegative.getActionMap().put("doSomething",
                moveYNegative);
        getContentPane().add(jMoveYNegative);

        JButton jMoveZPositive = new JButton();
        jMoveZPositive.setFocusable(true);
        jMoveZPositive.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Q"),
                "doSomething");
        jMoveZPositive.getActionMap().put("doSomething",
                moveZPositive);
        getContentPane().add(jMoveZPositive);

        JButton jMoveZNegative = new JButton();
        jMoveZNegative.setFocusable(true);
        jMoveZNegative.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("E"),
                "doSomething");
        jMoveZNegative.getActionMap().put("doSomething",
                moveZNegative);
        getContentPane().add(jMoveZNegative);

        JButton jRotateXPositive = new JButton();
        jRotateXPositive.setFocusable(true);
        jRotateXPositive.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("I"),
                "doSomething");
        jRotateXPositive.getActionMap().put("doSomething",
                rotateXPositive);
        getContentPane().add(jRotateXPositive);

        JButton jRotateXNegative = new JButton();
        jRotateXNegative.setFocusable(true);
        jRotateXNegative.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("K"),
                "doSomething");
        jRotateXNegative.getActionMap().put("doSomething",
                rotateXNegative);
        getContentPane().add(jRotateXNegative);

        JButton jRotateYPositive = new JButton();
        jRotateYPositive.setFocusable(true);
        jRotateYPositive.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("J"),
                "doSomething");
        jRotateYPositive.getActionMap().put("doSomething",
                rotateYPositive);
        getContentPane().add(jRotateYPositive);

        JButton jRotateYNegative = new JButton();
        jRotateYNegative.setFocusable(true);
        jRotateYNegative.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("L"),
                "doSomething");
        jRotateYNegative.getActionMap().put("doSomething",
                rotateYNegative);
        getContentPane().add(jRotateYNegative);

        JButton jRotateZPositive = new JButton();
        jRotateZPositive.setFocusable(true);
        jRotateZPositive.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("U"),
                "doSomething");
        jRotateZPositive.getActionMap().put("doSomething",
                rotateZPositive);
        getContentPane().add(jRotateZPositive);

        JButton jRotateZNegative = new JButton();
        jRotateZNegative.setFocusable(true);
        jRotateZNegative.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("O"),
                "doSomething");
        jRotateZNegative.getActionMap().put("doSomething",
                rotateZNegative);
        getContentPane().add(jRotateZNegative);
    }

    private final Action reset = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            viewpoint.set(0, 0);
            viewpoint.set(1, 0);
            viewpoint.set(2, 0);
            eyepoint.set(0, 1);
            eyepoint.set(1, 1);
            eyepoint.set(2, 1);
            transformView();
            canvas.repaint();
        }
    };

    private final Action moveXPositive = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            eyepoint.set(0, eyepoint.get(0)+D);
            viewpoint.set(0, viewpoint.get(0)+D);
            transformView();
            canvas.repaint();
        }
    };

    private final Action moveXNegative = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            eyepoint.set(0, eyepoint.get(0)-D);
            viewpoint.set(0, viewpoint.get(0)-D);
            transformView();
            canvas.repaint();
        }
    };

    private final Action moveYPositive = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            eyepoint.set(1, eyepoint.get(1)+D);
            viewpoint.set(1, viewpoint.get(1)+D);
            transformView();
            canvas.repaint();
        }
    };

    private final Action moveYNegative = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            eyepoint.set(1, eyepoint.get(1)-D);
            viewpoint.set(1, viewpoint.get(1)-D);
            transformView();
            canvas.repaint();
        }
    };

    private final Action moveZPositive = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            eyepoint.set(2, eyepoint.get(2)+D);
            viewpoint.set(2, viewpoint.get(2)+D);
            transformView();
            canvas.repaint();
        }
    };

    private final Action moveZNegative = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            eyepoint.set(2, eyepoint.get(2)-D);
            viewpoint.set(2, viewpoint.get(2)-D);
            transformView();
            canvas.repaint();
        }
    };

    private final Action rotateXPositive = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //eyepoint.set(0, viewpoint.get(0)+D);
            rotate(0, true);
            transformView();
            canvas.repaint();
        }
    };

    private final Action rotateXNegative = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //eyepoint.set(0, viewpoint.get(0)-D);
            rotate(0, false);
            transformView();
            canvas.repaint();
        }
    };

    private final Action rotateYPositive = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //eyepoint.set(1, viewpoint.get(1)+D);
            rotate(1, true);
            transformView();
            canvas.repaint();
        }
    };

    private final Action rotateYNegative = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //eyepoint.set(1, viewpoint.get(1)-D);
            rotate(1, false);
            transformView();
            canvas.repaint();
        }
    };

    private final Action rotateZPositive = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //eyepoint.set(2, viewpoint.get(2)+D);
            rotate(2, true);
            transformView();
            canvas.repaint();
        }
    };

    private final Action rotateZNegative = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //eyepoint.set(2, viewpoint.get(2)-D);
            rotate(2, false);
            transformView();
            canvas.repaint();
        }
    };

    private void rotate(int k, boolean flag){
        IVector eyeCopy=eyepoint.nSub(viewpoint).set(3, 1);
        double r = (flag) ? R : -R;
        double cos = Math.cos(r);
        double sin = Math.sin(r);

        IMatrix s;
        if (k==0){
            s = new Matrix(4, 4, new double[][]{
                    {1, 0, 0, 0},
                    {0, cos, sin, 0},
                    {0, -sin, cos, 0},
                    {0, 0, 0, 1}
            }, true);
        }
        else if (k==1){
            s = new Matrix(4, 4, new double[][]{
                    {cos, 0, sin, 0},
                    {0, 1, 0, 0},
                    {-sin, 0, cos, 0},
                    {0, 0, 0, 1}
            }, true);
        }
        else if (k==2){
            s = new Matrix(4, 4, new double[][]{
                    {cos, sin, 0, 0},
                    {-sin, cos, 0, 0},
                    {0, 0, 1, 0},
                    {0, 0, 0, 1}
            }, true);
        }
        else return;

        eyepoint = eyeCopy.toRowMatrix(false).nMultiply(s).toVector(false).add(viewpoint).set(3, 1);

    }

    public static void main(String[] args){
        SceneParser sceneParser = new TextSceneParser();
        sceneParser.parse();

        ObjectParser objectParser = new ObjParser(args[0]);
        objectParser.parse();
        Object3d object = objectParser.getObject();

        if (sceneParser.getViewpoint()==null || sceneParser.getEyepoint()==null || object.getVertices().size()<3) {
            System.out.println("improperly set parameters.");
            return;
        }

        new PreviewWindow(sceneParser.getViewpoint(), sceneParser.getEyepoint(), sceneParser.getLightSource(), sceneParser.getLighting(), object).setVisible(true);
    }
}
