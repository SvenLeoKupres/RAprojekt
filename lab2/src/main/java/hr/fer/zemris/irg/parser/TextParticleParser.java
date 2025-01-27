package hr.fer.zemris.irg.parser;

import hr.fer.zemris.irg.particles.IColoriser;
import hr.fer.zemris.irg.particles.ITimer;
import hr.fer.zemris.irg.particles.ParticleSource;
import hr.fer.zemris.irg.window.PreviewWindow;

import java.io.IOException;
import java.io.InputStream;

public class TextParticleParser implements ParticleParser {
    private boolean parsed = false;
    private ParticleSource source = null;
    private IColoriser coloriser = null;
    private ITimer timer = null;

    @Override
    public void parse() {
        String str;
        try(InputStream is = PreviewWindow.class.getClassLoader().getResourceAsStream("particles.txt")) {
            if(is==null) throw new RuntimeException("Datoteka je nedostupna.");
            byte[] data = is.readAllBytes();
            str=new String(data);
        } catch(IOException ex) {
            throw new RuntimeException("Greška pri čitanju datoteke.", ex);
        }
        String[] lines=str.split("\n\r|\n|\r");
        for (var k:lines){

        }

        parsed = true;
    }

    @Override
    public ParticleSource getParticleSource() {
        if (!parsed) throw new RuntimeException("Not parsed");
        return source;
    }

    @Override
    public IColoriser getColoriser() {
        if (!parsed) throw new RuntimeException("Not parsed");
        return coloriser;
    }

    @Override
    public ITimer getTimer() {
        if (!parsed) throw new RuntimeException("Not parsed");
        return timer;
    }

}
