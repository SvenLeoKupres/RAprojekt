package hr.fer.zemris.marching.cubes;

public interface Function {
    /**
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return 0 if on function, 1 if in function, -1 if outside of function
     */
    boolean respectsFunction(int x, int y, int z);
}
