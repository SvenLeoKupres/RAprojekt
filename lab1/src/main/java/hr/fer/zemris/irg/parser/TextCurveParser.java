package hr.fer.zemris.irg.parser;

import hr.fer.zemris.irg.window.PreviewWindow;
import hr.fer.zemris.util.IVector;
import hr.fer.zemris.util.Vector;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TextCurveParser implements CurveParser {
    private final List<IVector> curve=new ArrayList<>();
    private boolean parsed = false;
    @Override
    public void parse() {
        String str;
        try(InputStream is = PreviewWindow.class.getClassLoader().getResourceAsStream("curve.txt")) {
            if(is==null) throw new RuntimeException("Datoteka je nedostupna.");
            byte[] data = is.readAllBytes();
            str=new String(data);
        } catch(IOException ex) {
            throw new RuntimeException("Greška pri čitanju datoteke.", ex);
        }

        for (var k:str.split("\n\r|\n|\r")){
            String[] vector = k.split(" ");

            curve.add(new Vector(new double[]{Double.parseDouble(vector[0].trim()), Double.parseDouble(vector[1].trim()), Double.parseDouble(vector[2].trim())}));
        }

        parsed = true;
    }
    @Override
    public List<IVector> getCurve() {
        if (!parsed) throw new IllegalStateException("Not parsed.");
        return curve;
    }


}
