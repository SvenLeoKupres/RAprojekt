package hr.fer.zemris.irg.parser;

import hr.fer.zemris.util.IVector;

public interface SceneParser extends IParser {
    IVector getViewpoint();
    IVector getEyepoint();
    IVector getLightSource();
    String getLighting();
}
