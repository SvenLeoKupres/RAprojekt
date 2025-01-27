package hr.fer.zemris.irg.window;

public class AnimationFinishedException extends RuntimeException {
    public AnimationFinishedException() {
        super("No more frames to render");
    }
}
