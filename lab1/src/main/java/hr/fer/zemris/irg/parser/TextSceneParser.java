package hr.fer.zemris.irg.parser;

import hr.fer.zemris.irg.window.PreviewWindow;
import hr.fer.zemris.util.IVector;
import hr.fer.zemris.util.Vector;

import java.io.IOException;
import java.io.InputStream;

public class TextSceneParser implements SceneParser {
    private IVector viewpoint=null, eyepoint=null, lightSource=null;
    private String lighting = "off";
    private boolean parsed = false;

    @Override
    public void parse() {
        String str;
        try(InputStream is = PreviewWindow.class.getClassLoader().getResourceAsStream("scene.txt")) {
            if(is==null) throw new RuntimeException("Datoteka je nedostupna.");
            byte[] data = is.readAllBytes();
            str=new String(data);
        } catch(IOException ex) {
            throw new RuntimeException("Greška pri čitanju datoteke.", ex);
        }

        String[] lines=str.split("\n\r|\n|\r");
        for (var k:lines){
            if (k.startsWith("g")) {
                String[] vector = k.split("g ")[1].split(" ");
                float x = Float.parseFloat(vector[0]);
                float y = Float.parseFloat(vector[1]);
                float z = Float.parseFloat(vector[2]);
                viewpoint = new Vector(new double[]{x, y, z, 1});
            }
            else if (k.startsWith("o")) {
                String[] vector = k.split("o ")[1].split(" ");
                float x = Float.parseFloat(vector[0]);
                float y = Float.parseFloat(vector[1]);
                float z = Float.parseFloat(vector[2]);
                eyepoint = new Vector(new double[]{x, y, z, 1});
            }
            else if (k.startsWith("s")) {
                String[] vector = k.split("s ")[1].split(" ");
                float x = Float.parseFloat(vector[0]);
                float y = Float.parseFloat(vector[1]);
                float z = Float.parseFloat(vector[2]);
                float ka = Math.max(0, Math.min(1, Float.parseFloat(vector[3])));
                float ia = Math.max(0, Math.min(255, Float.parseFloat(vector[4])));
                float kd = Math.max(0, Math.min(1, Float.parseFloat(vector[5])));
                float id = Math.max(0, Math.min(255, Float.parseFloat(vector[6])));
                lightSource = new Vector(new double[]{x, y, z, ka, ia, kd, id});
            }
            else if (k.startsWith("l")) {
                String[] vector = k.split("l ")[1].split(" ");
                lighting = vector[0];
            }
        }

        parsed = true;
    }

    @Override
    public IVector getViewpoint() {
        if (!parsed) throw new RuntimeException("Not parsed");
        return viewpoint;
    }

    @Override
    public IVector getEyepoint() {
        if (!parsed) throw new RuntimeException("Not parsed");
        return eyepoint;
    }

    @Override
    public IVector getLightSource() {
        if (!parsed) throw new RuntimeException("Not parsed");
        return lightSource;
    }

    @Override
    public String getLighting(){
        if (!parsed) throw new RuntimeException("Not parsed");
        return lighting;
    }
}
