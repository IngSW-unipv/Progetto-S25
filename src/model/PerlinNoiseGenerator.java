package model;

import java.util.Random;

public class PerlinNoiseGenerator {
    private static final int OCTAVES = 4;
    private static final double PERSISTENCE = 0.5;
    private static final double SCALE = 50.0;

    private final long seed;
    private final Random random;
    private final int[] p;

    public PerlinNoiseGenerator(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
        this.p = new int[512];

        for(int i = 0; i < 256; i++) {
            p[i] = i;
        }

        for(int i = 0; i < 256; i++) {
            int j = random.nextInt(256);
            int temp = p[i];
            p[i] = p[j];
            p[j] = temp;
        }

        for(int i = 0; i < 256; i++) {
            p[256 + i] = p[i];
        }
    }

    public double noise(double x, double z) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double maxValue = 0;

        for(int i = 0; i < OCTAVES; i++) {
            total += generateNoise(x * frequency, z * frequency) * amplitude;
            maxValue += amplitude;
            amplitude *= PERSISTENCE;
            frequency *= 2;
        }

        return total/maxValue;
    }

    public double noise3D(double x, double y, double z) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double maxValue = 0;

        for(int i = 0; i < OCTAVES; i++) {
            total += generateNoise3D(x * frequency, y * frequency, z * frequency) * amplitude;
            maxValue += amplitude;
            amplitude *= PERSISTENCE;
            frequency *= 2;
        }

        return total/maxValue;
    }

    private double generateNoise(double x, double z) {
        int xi = (int)Math.floor(x) & 255;
        int zi = (int)Math.floor(z) & 255;
        double xf = x - Math.floor(x);
        double zf = z - Math.floor(z);

        double u = fade(xf);
        double v = fade(zf);

        int aa = p[p[xi] + zi];
        int ab = p[p[xi] + zi + 1];
        int ba = p[p[xi + 1] + zi];
        int bb = p[p[xi + 1] + zi + 1];

        double x1 = lerp(grad(aa, xf, 0, zf), grad(ba, xf-1, 0, zf), u);
        double x2 = lerp(grad(ab, xf, 0, zf-1), grad(bb, xf-1, 0, zf-1), u);

        return lerp(x1, x2, v);
    }

    private double generateNoise3D(double x, double y, double z) {
        int xi = (int)Math.floor(x) & 255;
        int yi = (int)Math.floor(y) & 255;
        int zi = (int)Math.floor(z) & 255;

        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);
        double zf = z - Math.floor(z);

        double u = fade(xf);
        double v = fade(yf);
        double w = fade(zf);

        int aaa = p[p[p[xi] + yi] + zi];
        int aba = p[p[p[xi] + yi + 1] + zi];
        int aab = p[p[p[xi] + yi] + zi + 1];
        int abb = p[p[p[xi] + yi + 1] + zi + 1];
        int baa = p[p[p[xi + 1] + yi] + zi];
        int bba = p[p[p[xi + 1] + yi + 1] + zi];
        int bab = p[p[p[xi + 1] + yi] + zi + 1];
        int bbb = p[p[p[xi + 1] + yi + 1] + zi + 1];

        double x1 = lerp(
            lerp(
                grad(aaa, xf, yf, zf),
                grad(baa, xf-1, yf, zf),
                u
            ),
            lerp(
                grad(aba, xf, yf-1, zf),
                grad(bba, xf-1, yf-1, zf),
                u
            ),
            v
        );

        double x2 = lerp(
            lerp(
                grad(aab, xf, yf, zf-1),
                grad(bab, xf-1, yf, zf-1),
                u
            ),
            lerp(
                grad(abb, xf, yf-1, zf-1),
                grad(bbb, xf-1, yf-1, zf-1),
                u
            ),
            v
        );

        return lerp(x1, x2, w);
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y, double z) {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : h == 12 || h == 14 ? x : z;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}