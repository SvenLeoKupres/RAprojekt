package hr.fer.zemris.irg.particles;

import java.awt.*;

public class InterpolatedColoriser implements IColoriser {
    private final Color startColor;
    private final Color endColor;
    private final UnaryFunction[] functions;

    /**
     *
     * @param startColor hex value of the starting color, must be <256
     * @param endColor hex value of the ending color, must be <256
     * @param functions three unary functions which describe the interpolation process for each of the RGB components
     */
    public InterpolatedColoriser(int startColor, int endColor, UnaryFunction[] functions) {
        if (startColor > 0xffffff || endColor > 0xffffff) throw new IllegalArgumentException("Color values must be less than 256");

        this.startColor = new Color(startColor);
        this.endColor = new Color(endColor);

        this.functions = functions;
    }

    @Override
    public Color calculateColor(double value) {
        int R = (int) (startColor.getRed() + functions[0].accept(value)*(endColor.getRed() - startColor.getRed()) + 0.5);
        int G = (int) (startColor.getGreen() + functions[1].accept(value)*(endColor.getGreen() - startColor.getGreen()) + 0.5);
        int B = (int) (startColor.getBlue() + functions[2].accept(value)*(endColor.getBlue() - startColor.getBlue()) + 0.5);
        return new Color(R, G, B);
    }
}
