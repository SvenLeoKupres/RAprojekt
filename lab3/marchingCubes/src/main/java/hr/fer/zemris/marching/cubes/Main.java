package hr.fer.zemris.marching.cubes;

import javax.swing.*;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Terrain terrain;
        String work;
        int width, height, depth;
        try {
            work = args[3];
            width = Integer.parseInt(args[0]);
            height = Integer.parseInt(args[1]);
            depth = Integer.parseInt(args[2]);
        }
        catch (IndexOutOfBoundsException e) {
            System.out.println("Wrong number of arguments.");
            return;
        }

        terrain = new Terrain(width,height,depth);
        if (work.equals("perlin2d")) {
            terrain.buildTerrain(new PerlinNoise2D(width,height));
        }
        else if (work.equals("perlin3d")) {
            terrain.buildTerrain(new PerlinNoise3D(width, height, depth));
        }
        else if (work.equals("spheroid")) {
            double a = (double) (width-1)/2;
            double b = (double) (height-1)/2;
            double c = (double) (depth-1)/2;
            terrain.buildTerrain((x,y,z)->{
                double x_d = x-a;
                double y_d = y-b;
                double z_d = z-c;
                double sum=(x_d/a)*(x_d/a) + (y_d/b)*(y_d/b) + (z_d/c)*(z_d/c);
                // int res = ((width-1)*(height-1)/4);
                return sum < 1;
            });
        }
        else if (work.equals("cube")) {
            terrain.buildTerrain((x,y,z)-> !(x != 0 && x != width-1 && y != 0 && y != height-1 && z != 0 && z != depth-1));
        }
        else if (work.equals("cone")) {
            double a = (double) (width-1)/2;
            double b = (double) (height-1)/2;
            double c = (double) (depth-1)/2;
            terrain.buildTerrain((x,y,z) -> {
                if (y==0) return true;
                return y-b>-Math.sqrt(Math.pow(x-a, 2)+Math.pow(z-c, 2));
            });
        }
        else {
            System.out.println("\"" + work + "\" not recognized");
            return;
        }

        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Save document");
        if(jfc.showSaveDialog(null)!=JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(
                    null,
                    "Nista nije snimljeno.",
                    "Upozorenje",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Path path = Path.of(jfc.getSelectedFile().toPath().toString());

            if (path.toString().isEmpty()){
                OutputStream os = new BufferedOutputStream(Files.newOutputStream(path));
                String str = terrain.buildObject(new ObjFileBuilder());
                os.write(str.getBytes());
                os.close();
            }
            else {
                OutputStream os = new BufferedOutputStream(Files.newOutputStream(path));
                String str = terrain.buildObject(new ObjFileBuilder());
                os.write(str.getBytes());
                os.close();
            }
        }
        catch (IllegalArgumentException er) {
            JOptionPane.showMessageDialog(
                    null,
                    "Datoteka vec postoji na danom putu.",
                    "Upozorenje",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        catch (IOException ignored){}

        JOptionPane.showMessageDialog(
                null,
                "Datoteka je snimljena.",
                "Informacija",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
