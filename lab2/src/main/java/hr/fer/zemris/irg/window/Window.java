package hr.fer.zemris.irg.window;

import hr.fer.zemris.util.IVector;

import javax.swing.*;
import java.util.List;

public abstract class Window extends JFrame {
    static final double D=0.01;
    static final double R=Math.PI/180;
    static final int width=700;
    static final int height=700;

    public Window(String title) {
        super(title);
        this.setSize(width, height);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    void adjustCoords(List<IVector> list){
        for (var vertex:list){
            vertex.set(0, (vertex.get(0)+1)/2);
            vertex.set(1, (vertex.get(1)+1)/2);
            vertex.set(2, (vertex.get(2)+1)/2);

            //System.out.printf("%f, %f\n", vertex.get(0), vertex.get(1));
        }
        //System.out.println();
    }
}
