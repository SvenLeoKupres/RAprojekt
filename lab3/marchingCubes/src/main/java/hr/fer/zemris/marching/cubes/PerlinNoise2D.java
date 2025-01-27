package hr.fer.zemris.marching.cubes;

public class PerlinNoise2D extends PerlinNoise {
    private final int width, height;
    private final int cell_x_amount, cell_y_amount;

    public PerlinNoise2D(int width, int height) {
        this(width, height, width, height);
    }

    public PerlinNoise2D(int width, int height, int cell_x_amount, int cell_y_amount) {
        this.width = width;
        this.height = height;

        this.cell_x_amount = cell_x_amount;
        this.cell_y_amount = cell_y_amount;


    }

    @Override
    public boolean respectsFunction(int x, int y, int z) {
        //global coordinates
        double xf = (double) x / width;
        double yf = (double) y / height;

        // coordinates within the cell;
        double u = xf*cell_x_amount;
        double v = yf*cell_y_amount;

        // cell coordinates
        int x_cell = (int) u;
        int y_cell = (int) v;

        u = fade(u - x_cell);
        v = fade(v - y_cell);

        int aa, ab, ba, bb;
        aa = p[p[x_cell  ] + y_cell  ];
        ab = p[p[x_cell  ] + y_cell+1];
        ba = p[p[x_cell+1] + y_cell];
        bb = p[p[x_cell+1] + y_cell+1];

        double x1, x2;
        x1 = l_interpolate(grad(aa, xf, yf), grad (ba, xf-1, yf), u);
        x2 = l_interpolate(grad(ab, xf, yf-1), grad (bb, xf-1, yf-1), u);

        return l_interpolate(x1, x2, v) >= (double) z / 5;
    }

    /*
    (0, 1), (0, -1)
    (1, 0), (-1, 0)
     */
    private static double grad(int hash, double x, double y)
    {
        return switch (hash & 0xF) {
            case 0xF -> y;
            case 0xE -> -y;
            case 0xD -> x;
            case 0xC -> -x;
            case 0xB -> y;
            case 0xA -> -y;
            case 0x9 -> x;
            case 0x8 -> -x;
            case 0x7 -> y;
            case 0x6 -> -y;
            case 0x5 -> x;
            case 0x4 -> -x;
            case 0x3 -> y;
            case 0x2 -> -y;
            case 0x1 -> x;
            case 0x0 -> -x;

            default -> 0; // never happens
        };
    }
}
