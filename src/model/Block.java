package model;

public class Block {
    private final BlockType type;
    private final Position position;

    private final float[] vertices;
    private final int[] indices;

    public Block(BlockType type, Position position) {
        this.type = type;
        this.position = position;
        this.vertices = createVertices();
        this.indices = generateIndices();
    }

    private float[] createVertices() {
        float x = position.getX();
        float y = position.getY();
        float z = position.getZ();

        return new float[] {
                // Front face (Z+)
                x - 0.5f, y + 0.5f, z + 0.5f,   0.0f, 0.0f,  // Top-left
                x + 0.5f, y + 0.5f, z + 0.5f,   1.0f, 0.0f,  // Top-right
                x + 0.5f, y - 0.5f, z + 0.5f,   1.0f, 1.0f,  // Bottom-right
                x - 0.5f, y - 0.5f, z + 0.5f,   0.0f, 1.0f,  // Bottom-left

                // Back face (Z-)
                x + 0.5f, y + 0.5f, z - 0.5f,   0.0f, 0.0f,  // Top-left
                x - 0.5f, y + 0.5f, z - 0.5f,   1.0f, 0.0f,  // Top-right
                x - 0.5f, y - 0.5f, z - 0.5f,   1.0f, 1.0f,  // Bottom-right
                x + 0.5f, y - 0.5f, z - 0.5f,   0.0f, 1.0f,  // Bottom-left

                // Top face (Y+)
                x - 0.5f, y + 0.5f, z - 0.5f,   0.0f, 0.0f,  // Back-left
                x + 0.5f, y + 0.5f, z - 0.5f,   1.0f, 0.0f,  // Back-right
                x + 0.5f, y + 0.5f, z + 0.5f,   1.0f, 1.0f,  // Front-right
                x - 0.5f, y + 0.5f, z + 0.5f,   0.0f, 1.0f,  // Front-left

                // Bottom face (Y-)
                x - 0.5f, y - 0.5f, z + 0.5f,   0.0f, 0.0f,  // Front-left
                x + 0.5f, y - 0.5f, z + 0.5f,   1.0f, 0.0f,  // Front-right
                x + 0.5f, y - 0.5f, z - 0.5f,   1.0f, 1.0f,  // Back-right
                x - 0.5f, y - 0.5f, z - 0.5f,   0.0f, 1.0f,  // Back-left

                // Right face (X+)
                x + 0.5f, y + 0.5f, z + 0.5f,   0.0f, 0.0f,  // Front-top
                x + 0.5f, y + 0.5f, z - 0.5f,   1.0f, 0.0f,  // Back-top
                x + 0.5f, y - 0.5f, z - 0.5f,   1.0f, 1.0f,  // Back-bottom
                x + 0.5f, y - 0.5f, z + 0.5f,   0.0f, 1.0f,  // Front-bottom

                // Left face (X-)
                x - 0.5f, y + 0.5f, z - 0.5f,   0.0f, 0.0f,  // Back-top
                x - 0.5f, y + 0.5f, z + 0.5f,   1.0f, 0.0f,  // Front-top
                x - 0.5f, y - 0.5f, z + 0.5f,   1.0f, 1.0f,  // Front-bottom
                x - 0.5f, y - 0.5f, z - 0.5f,   0.0f, 1.0f   // Back-bottom
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

    public BlockType getType() {
        return type;
    }

    public Position getPosition() { return position; }

    public float[] getVertices() {
        return vertices;
    }

    public int[] getIndices() {
        return indices;
    }
}