package model.world;

import java.util.Random;

/**
 * Generates coherent noise for terrain generation using improved Perlin noise algorithm
 * Supports 2D and 3D noise with configurable octaves for natural-looking landscapes
 */
public class PerlinNoiseGenerator {
    /** Configuration constants for noise generation */
    private static final int OCTAVES = 4;
    private static final double PERSISTENCE = 0.5;

    /** State for deterministic noise generation */
    private final int[] p;


    /**
     * Creates noise generator with specified seed
     * @param seed Controls the randomization pattern
     */
    public PerlinNoiseGenerator(long seed) {
        Random random = new Random(seed);
        this.p = new int[512];

        // Build permutation table
        for (int i = 0; i < 256; i++) {
            p[i] = i;
        }

        // Randomize permutations
        for (int i = 0; i < 256; i++) {
            int j = random.nextInt(256);
            int temp = p[i];
            p[i] = p[j];
            p[j] = temp;
        }

        // Mirror for easier lookup
        System.arraycopy(p, 0, p, 256, 256);
    }

    /**
     * Generates 2D noise for terrain height maps
     * @return Noise value between -1 and 1
     */
    public double noise(double x, double z) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double maxValue = 0;

        // Layer multiple noise octaves
        for (int i = 0; i < OCTAVES; i++) {
            total += generateNoise(x * frequency, z * frequency) * amplitude;
            maxValue += amplitude;
            amplitude *= PERSISTENCE;  // Reduce amplitude for each octave
            frequency *= 2;           // Increase frequency for each octave
        }

        return total / maxValue;  // Normalize to -1,1 range
    }

    /**
     * Generates 3D noise for caves and features
     * @return Noise value between -1 and 1
     */
    public double noise3D(double x, double y, double z) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double maxValue = 0;

        // Layer multiple noise octaves
        for (int i = 0; i < OCTAVES; i++) {
            total += generateNoise3D(x * frequency, y * frequency, z * frequency) * amplitude;
            maxValue += amplitude;
            amplitude *= PERSISTENCE;
            frequency *= 2;
        }

        return total / maxValue;
    }

    /**
     * Generates single octave of 2D noise
     */
    private double generateNoise(double x, double z) {
        // Get unit cube containing point
        int xi = (int) Math.floor(x) & 255;
        int zi = (int) Math.floor(z) & 255;

        // Get relative position in cube
        double xf = x - Math.floor(x);
        double zf = z - Math.floor(z);

        // Compute fade curves
        double u = fade(xf);
        double v = fade(zf);

        // Hash coordinates of cube corners
        int aa = p[p[xi] + zi];
        int ab = p[p[xi] + zi + 1];
        int ba = p[p[xi + 1] + zi];
        int bb = p[p[xi + 1] + zi + 1];

        // Interpolate between corner gradients
        double x1 = lerp(grad(aa, xf, 0, zf), grad(ba, xf - 1, 0, zf), u);
        double x2 = lerp(grad(ab, xf, 0, zf - 1), grad(bb, xf - 1, 0, zf - 1), u);

        return lerp(x1, x2, v);
    }

    /**
     * Generates single octave of 3D noise
     */
    private double generateNoise3D(double x, double y, double z) {
        // Get unit cube containing point
        int xi = (int) Math.floor(x) & 255;
        int yi = (int) Math.floor(y) & 255;
        int zi = (int) Math.floor(z) & 255;

        // Get relative position in cube
        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);
        double zf = z - Math.floor(z);

        // Compute fade curves
        double u = fade(xf);
        double v = fade(yf);
        double w = fade(zf);

        // Hash coordinates of cube corners
        int aaa = p[p[p[xi] + yi] + zi];
        int aba = p[p[p[xi] + yi + 1] + zi];
        int aab = p[p[p[xi] + yi] + zi + 1];
        int abb = p[p[p[xi] + yi + 1] + zi + 1];
        int baa = p[p[p[xi + 1] + yi] + zi];
        int bba = p[p[p[xi + 1] + yi + 1] + zi];
        int bab = p[p[p[xi + 1] + yi] + zi + 1];
        int bbb = p[p[p[xi + 1] + yi + 1] + zi + 1];

        // Interpolate between corner gradients
        double x1 = lerp(
                lerp(grad(aaa, xf, yf, zf), grad(baa, xf - 1, yf, zf), u),
                lerp(grad(aba, xf, yf - 1, zf), grad(bba, xf - 1, yf - 1, zf), u),
                v
        );

        double x2 = lerp(
                lerp(grad(aab, xf, yf, zf - 1), grad(bab, xf - 1, yf, zf - 1), u),
                lerp(grad(abb, xf, yf - 1, zf - 1), grad(bbb, xf - 1, yf - 1, zf - 1), u),
                v
        );

        return lerp(x1, x2, w);
    }

    /**
     * Applies smoothstep curve for smoother interpolation
     */
    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    /**
     * Performs linear interpolation between values
     */
    private double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    /**
     * Computes gradient value for given hash and coordinates
     */
    private double grad(int hash, double x, double y, double z) {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : h == 12 || h == 14 ? x : z;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}