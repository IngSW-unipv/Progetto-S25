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
                // Front face (Z+)
                -0.5f,  0.5f,  0.5f,   0.0f, 0.0f,  // Top-left
                0.5f,  0.5f,  0.5f,   1.0f, 0.0f,  // Top-right
                0.5f, -0.5f,  0.5f,   1.0f, 1.0f,  // Bottom-right
                -0.5f, -0.5f,  0.5f,   0.0f, 1.0f,  // Bottom-left

                // Back face (Z-)
                0.5f,  0.5f, -0.5f,   0.0f, 0.0f,  // Top-left
                -0.5f,  0.5f, -0.5f,   1.0f, 0.0f,  // Top-right
                -0.5f, -0.5f, -0.5f,   1.0f, 1.0f,  // Bottom-right
                0.5f, -0.5f, -0.5f,   0.0f, 1.0f,  // Bottom-left

                // Top face (Y+)
                -0.5f,  0.5f, -0.5f,   0.0f, 0.0f,  // Back-left
                0.5f,  0.5f, -0.5f,   1.0f, 0.0f,  // Back-right
                0.5f,  0.5f,  0.5f,   1.0f, 1.0f,  // Front-right
                -0.5f,  0.5f,  0.5f,   0.0f, 1.0f,  // Front-left

                // Bottom face (Y-)
                -0.5f, -0.5f,  0.5f,   0.0f, 0.0f,  // Front-left
                0.5f, -0.5f,  0.5f,   1.0f, 0.0f,  // Front-right
                0.5f, -0.5f, -0.5f,   1.0f, 1.0f,  // Back-right
                -0.5f, -0.5f, -0.5f,   0.0f, 1.0f,  // Back-left

                // Right face (X+)
                0.5f,  0.5f,  0.5f,   0.0f, 0.0f,  // Front-top
                0.5f,  0.5f, -0.5f,   1.0f, 0.0f,  // Back-top
                0.5f, -0.5f, -0.5f,   1.0f, 1.0f,  // Back-bottom
                0.5f, -0.5f,  0.5f,   0.0f, 1.0f,  // Front-bottom

                // Left face (X-)
                -0.5f,  0.5f, -0.5f,   0.0f, 0.0f,  // Back-top
                -0.5f,  0.5f,  0.5f,   1.0f, 0.0f,  // Front-top
                -0.5f, -0.5f,  0.5f,   1.0f, 1.0f,  // Front-bottom
                -0.5f, -0.5f, -0.5f,   0.0f, 1.0f   // Back-bottom
        };
    }

    private int[] generateIndices() {
        int[] indices = new int[36];  // 6 facce * 2 triangoli * 3 vertici
        for (int face = 0; face < 6; face++) {
            int offset = face * 4;  // ogni faccia inizia con un offset di 4 vertici
            int index = face * 6;   // ogni faccia usa 6 indici

            // Primo triangolo della faccia (in senso antiorario)
            indices[index] = offset;
            indices[index + 1] = offset + 1;
            indices[index + 2] = offset + 2;

            // Secondo triangolo della faccia (in senso antiorario)
            indices[index + 3] = offset;
            indices[index + 4] = offset + 2;
            indices[index + 5] = offset + 3;
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