package hr.fer.zemris.marching.cubes;

/**
 * Part of the code found at <a href="https://adrianb.io/2014/08/09/perlinnoise.html">...</a>
 */
public class PerlinNoise3D extends PerlinNoise {
    private final int width, height, depth;
    private final int cell_x_amount, cell_y_amount, cell_z_amount;
    // private final double[][][][] gradient_vectors;

    public PerlinNoise3D(int width, int height, int depth) {
        this(width, height, depth, width, height, depth);
    }

    public PerlinNoise3D(int width, int height, int depth, int cell_x_amount, int cell_y_amount, int cell_z_amount) {
        this.width = width;
        this.height = height;
        this.depth = depth;

        this.cell_x_amount = cell_x_amount;
        this.cell_y_amount = cell_y_amount;
        this.cell_z_amount = cell_z_amount;

        // this.gradient_vectors = new double[cell_x_amount][cell_y_amount][cell_z_amount][3];

//        Random r = new Random();
//        for (int x = 0; x < cell_x_amount; x++) {
//            for (int y = 0; y < cell_y_amount; y++) {
//                for (int z = 0; z < cell_z_amount; z++) {
//                    double[] vector = gradient_vectors[x][y][z];
//                    vector[0] = r.nextDouble();
//                    vector[1] = r.nextDouble();
//                    vector[2] = r.nextDouble();
//                    double length = Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
//                    vector[0] /= length;
//                    vector[1] /= length;
//                    vector[2] /= length;
//                }
//            }
//        }
    }

    @Override
    public boolean respectsFunction(int x, int y, int z) {
        x = x & 255;
        y = y & 255;
        z = z & 255;

        //global coordinates
        double xf = (double) x / width;                 // Calculate the "unit cube" that the point asked will be located in
        double yf = (double) y / height;                // The left bound is ( |_x_|,|_y_|,|_z_| ) and the right bound is that + 1
        double zf = (double) z / depth;                 // Next we calculate the location (from 0.0 to 1.0) in that cube.

        // coordinates within the cell;
        double u = xf*cell_x_amount;
        double v = yf*cell_y_amount;
        double w = zf*cell_z_amount;

        // cell coordinates
        int x_cell = (int) u;
        int y_cell = (int) v;
        int z_cell = (int) w;

        u = fade(u - x_cell);
        v = fade(v - y_cell);
        w = fade(w - z_cell);

        int aaa, aab, aba, abb, baa, bab, bba, bbb;
        aaa = p[p[p[x_cell  ] + y_cell  ] + z_cell  ];
        aab = p[p[p[x_cell  ] + y_cell  ] + z_cell+1];
        aba = p[p[p[x_cell  ] + y_cell+1] + z_cell  ];
        abb = p[p[p[x_cell  ] + y_cell+1] + z_cell+1];
        baa = p[p[p[x_cell+1] + y_cell  ] + z_cell  ];
        bab = p[p[p[x_cell+1] + y_cell  ] + z_cell+1];
        bba = p[p[p[x_cell+1] + y_cell+1] + z_cell  ];
        bbb = p[p[p[x_cell+1] + y_cell+1] + z_cell+1];

        double x1, x2, y1, y2;
        x1 = l_interpolate(grad (aaa, xf  , yf  , zf),           // The gradient function calculates the dot product between a pseudorandom
                grad (baa, xf-1, yf  , zf),                   // gradient vector and the vector from the input coordinate to the 8
                u);                                              // surrounding points in its unit cube.
        x2 = l_interpolate(grad (aba, xf  , yf-1, zf),        // This is all then linearly interpolated together as a sort of weighted average based on the
                grad (bba, xf-1, yf-1, zf),                // faded (u,v,w) values we made earlier.
                u);
        y1 = l_interpolate(x1, x2, v);

        x1 = l_interpolate(    grad (aab, xf  , yf  , zf-1),
                grad (bab, xf-1, yf  , zf-1),
                u);
        x2 = l_interpolate(    grad (abb, xf  , yf-1, zf-1),
                grad (bbb, xf-1, yf-1, zf-1),
                u);
        y2 = l_interpolate (x1, x2, v);

        return l_interpolate (y1, y2, w) >= 0.7;
    }

    private static double grad(int hash, double x, double y, double z)
    {
        return switch (hash & 0xF) {
            case 0x0 -> x + y;
            case 0x1 -> -x + y;
            case 0x2 -> x - y;
            case 0x3 -> -x - y;
            case 0x4 -> x + z;
            case 0x5 -> -x + z;
            case 0x6 -> x - z;
            case 0x7 -> -x - z;
            case 0x8 -> y + z;
            case 0x9, 0xD -> -y + z;
            case 0xA -> y - z;
            case 0xB, 0xF -> -y - z;
            case 0xC -> y + x;
            case 0xE -> y - x;
            default -> 0; // never happens
        };
    }
}
