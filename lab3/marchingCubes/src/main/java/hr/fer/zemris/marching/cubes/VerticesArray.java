package hr.fer.zemris.marching.cubes;


/**
 * {x, y, z}
 *  the points represent the halfway points on all the cube edges, where the cube center is in (0.5, 0.5, 0.5)
 *  The points are listed from the lowest height to the highest (y-coordinate), counterclockwise
 */
public class VerticesArray {
    public static double[][] array = new double[][]{
            {0.5, 0, 0},
            {1, 0, 0.5},
            {0.5, 0, 1},
            {0, 0, 0.5},

            {0.5, 1, 0},
            {1, 1, 0.5},
            {0.5, 1, 1},
            {0, 1, 0.5},

            {0, 0.5, 0},
            {1, 0.5, 0},
            {1, 0.5, 1},
            {0, 0.5, 1},
    };
}
