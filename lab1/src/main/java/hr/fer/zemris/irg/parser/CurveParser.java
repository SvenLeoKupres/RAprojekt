package hr.fer.zemris.irg.parser;

import hr.fer.zemris.util.IVector;

import java.util.List;

public interface CurveParser extends IParser {
    List<IVector> getCurve();
}
