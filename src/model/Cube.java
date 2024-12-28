package model;

public class Cube {
    private final BlockType type;
    private float[] vertices;
    private int[] indices;

    public Cube(BlockType type) {
        this.type = type;
        this.vertices = createVertices();
        this.indices = generateIndices();
    }

    private float[] createVertices() {
        return new float[] {
                // Front face
                -0.5f,  0.5f,  0.5f,   0.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,   0.0f, 1.0f,
                0.5f, -0.5f,  0.5f,   1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,   1.0f, 0.0f,

                // Back face
                -0.5f,  0.5f, -0.5f,   0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f,   0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,   1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,   1.0f, 0.0f,

                // Top face
                -0.5f,  0.5f, -0.5f,   0.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,   0.0f, 1.0f,
                0.5f,  0.5f,  0.5f,   1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,   1.0f, 0.0f,

                // Bottom face
                -0.5f, -0.5f, -0.5f,   0.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,   0.0f, 1.0f,
                0.5f, -0.5f,  0.5f,   1.0f, 1.0f,
                0.5f, -0.5f, -0.5f,   1.0f, 0.0f,

                // Right face
                0.5f,  0.5f,  0.5f,   0.0f, 0.0f,
                0.5f, -0.5f,  0.5f,   0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,   1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,   1.0f, 0.0f,

                // Left face
                -0.5f,  0.5f,  0.5f,   1.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,   1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,   0.0f, 1.0f,
                -0.5f,  0.5f, -0.5f,   0.0f, 0.0f
        };
    }

    private int[] generateIndices() {
        int[] indices = new int[36];
        int i = 0;
        for (int face = 0; face < 6; face++) {
            int offset = face * 4;
            indices[i++] = offset;
            indices[i++] = offset + 1;
            indices[i++] = offset + 2;
            indices[i++] = offset;
            indices[i++] = offset + 2;
            indices[i++] = offset + 3;
        }
        return indices;
    }

    public float[] getVertices() {
        return vertices;
    }

    public int[] getIndices() {
        return indices;
    }

    public BlockType getType() {
        return type;
    }
}