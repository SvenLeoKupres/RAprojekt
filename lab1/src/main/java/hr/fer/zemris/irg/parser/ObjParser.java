package hr.fer.zemris.irg.parser;

import hr.fer.zemris.Util;
import hr.fer.zemris.irg.Object3d;
import hr.fer.zemris.irg.window.PreviewWindow;
import hr.fer.zemris.util.IVector;
import hr.fer.zemris.util.Vector;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjParser implements ObjectParser {
    private final String filename;
    private Object3d object;
    private boolean parsed = false;
    public ObjParser(String filename) {
        this.filename = filename;
    }
    @Override
    public void parse() {
        String str;
        try(InputStream is = PreviewWindow.class.getClassLoader().getResourceAsStream(filename+".obj")) {
            if(is==null) throw new RuntimeException("Datoteka "+filename+".obj je nedostupna.");
            byte[] data = is.readAllBytes();
            str=new String(data);
        } catch(IOException ex) {
            throw new RuntimeException("Greška pri čitanju datoteke.", ex);
        }

        String[] lines=str.split("\n\r|\n|\r");
        int index = 0;

        List<IVector> polygons = new ArrayList<>();
        List<IVector> vertices = new ArrayList<>();
        Map<Integer, IVector> normals = new HashMap<>();

        for (var k:lines){
            if (k.startsWith("vn ")) {
                String[] vector = k.split("vn +")[1].split(" +");
                float v0 = Float.parseFloat(vector[0])-1;
                float v1 = Float.parseFloat(vector[1])-1;
                float v2 = Float.parseFloat(vector[2])-1;
                normals.put(index++, new Vector(new double[]{v0, v1, v2}));
            }
            else if (k.startsWith("v ")) {
                String[] vector = k.split("v +")[1].split(" +");
                float x = Float.parseFloat(vector[0]);
                float y = Float.parseFloat(vector[1]);
                float z = Float.parseFloat(vector[2]);
                vertices.add(new Vector(new double[]{x, y, z, 1}));
            }
            else if (k.startsWith("f ")) {
                String[] vector = k.split("f +")[1].split(" +");
                int v0;
                int v1;
                int v2;
                if (vector[0].contains("/")){
                    v0 = Integer.parseInt(vector[0].split("/+")[0]) - 1;
                    v1 = Integer.parseInt(vector[1].split("/+")[0]) - 1;
                    v2 = Integer.parseInt(vector[2].split("/+")[0]) - 1;
                }
                else {
                    v0 = Integer.parseInt(vector[0]) - 1;
                    v1 = Integer.parseInt(vector[1]) - 1;
                    v2 = Integer.parseInt(vector[2]) - 1;
                }
                polygons.add(new Vector(new double[]{v0, v1, v2, 1}));
            }
        }

        if (normals.isEmpty()) normals = Util.calculateNormals(polygons, vertices);

        object = new Object3d(polygons, vertices, normals);

        parsed = true;
    }

    @Override
    public Object3d getObject() {
        if (!parsed) throw new RuntimeException("Not parsed.");

        return object;
    }
}
