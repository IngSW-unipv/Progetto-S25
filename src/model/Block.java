package model;

import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Block {
    private final BlockType type;
    private final Position position;
    private final BoundingBox boundingBox;
    private boolean[] visibleFaces;
    private boolean isVisible = true;
    private boolean isHighlighted = false;

    // Indici per ogni faccia
    private static final int FRONT = 0;  // Z+
    private static final int BACK = 1;   // Z-
    private static final int TOP = 2;    // Y+
    private static final int BOTTOM = 3; // Y-
    private static final int RIGHT = 4;  // X+
    private static final int LEFT = 5;   // X-

    public Block(BlockType type, Position position) {
        this.type = type;
        this.position = position;
        this.visibleFaces = new boolean[]{true, true, true, true, true, true};
        this.boundingBox = new BoundingBox(1.0f, 1.0f, 1.0f);
        this.boundingBox.update(new Vector3f(position.x(), position.y(), position.z()));
    }

    public void updateVisibleFaces(World world) {
        // Controllo se il blocco è completamente nascosto
        if (isCompletelyHidden(world)) {
            Arrays.fill(visibleFaces, false);
            return;
        }

        // Se c'è un blocco sopra, nascondi tutte le facce tranne quelle laterali che sono esposte
        if (hasAdjacentBlock(world, 0, 1, 0)) {
            visibleFaces[TOP] = false;
            visibleFaces[FRONT] = !hasAdjacentBlock(world, 0, 0, 1) && !hasAdjacentBlock(world, 0, 1, 1);
            visibleFaces[BACK] = !hasAdjacentBlock(world, 0, 0, -1) && !hasAdjacentBlock(world, 0, 1, -1);
            visibleFaces[RIGHT] = !hasAdjacentBlock(world, 1, 0, 0) && !hasAdjacentBlock(world, 1, 1, 0);
            visibleFaces[LEFT] = !hasAdjacentBlock(world, -1, 0, 0) && !hasAdjacentBlock(world, -1, 1, 0);
            visibleFaces[BOTTOM] = !hasAdjacentBlock(world, 0, -1, 0);
            return;
        }

        // Controllo normale per blocchi esposti
        visibleFaces[TOP] = true;
        visibleFaces[FRONT] = !hasAdjacentBlock(world, 0, 0, 1);
        visibleFaces[BACK] = !hasAdjacentBlock(world, 0, 0, -1);
        visibleFaces[RIGHT] = !hasAdjacentBlock(world, 1, 0, 0);
        visibleFaces[LEFT] = !hasAdjacentBlock(world, -1, 0, 0);
        visibleFaces[BOTTOM] = !hasAdjacentBlock(world, 0, -1, 0);
    }

    private boolean isCompletelyHidden(World world) {
        return hasAdjacentBlock(world, 0, 1, 0) && // blocco sopra
                hasAdjacentBlock(world, 0, 0, 1) && // fronte
                hasAdjacentBlock(world, 0, 0, -1) && // dietro
                hasAdjacentBlock(world, 1, 0, 0) && // destra
                hasAdjacentBlock(world, -1, 0, 0); // sinistra
    }

    private boolean hasAdjacentBlock(World world, int dx, int dy, int dz) {
        Position adjacentPos = new Position(
                position.x() + dx,
                position.y() + dy,
                position.z() + dz
        );
        return world.getBlock(adjacentPos) != null;
    }

    public float[] getVertices() {
        List<Float> visibleVertices = new ArrayList<>();
        float x = position.x();
        float y = position.y();
        float z = position.z();

        // Faccia frontale (Z+)
        if (visibleFaces[FRONT]) {
            addFaceVertices(visibleVertices,
                    x - 0.5f, y + 0.5f, z + 0.5f, 0.0f, 0.0f,
                    x + 0.5f, y + 0.5f, z + 0.5f, 1.0f, 0.0f,
                    x + 0.5f, y - 0.5f, z + 0.5f, 1.0f, 1.0f,
                    x - 0.5f, y - 0.5f, z + 0.5f, 0.0f, 1.0f
            );
        }

        // Faccia posteriore (Z-)
        if (visibleFaces[BACK]) {
            addFaceVertices(visibleVertices,
                    x + 0.5f, y + 0.5f, z - 0.5f, 0.0f, 0.0f,
                    x - 0.5f, y + 0.5f, z - 0.5f, 1.0f, 0.0f,
                    x - 0.5f, y - 0.5f, z - 0.5f, 1.0f, 1.0f,
                    x + 0.5f, y - 0.5f, z - 0.5f, 0.0f, 1.0f
            );
        }

        // Faccia superiore (Y+)
        if (visibleFaces[TOP]) {
            addFaceVertices(visibleVertices,
                    x - 0.5f, y + 0.5f, z - 0.5f, 0.0f, 0.0f,
                    x + 0.5f, y + 0.5f, z - 0.5f, 1.0f, 0.0f,
                    x + 0.5f, y + 0.5f, z + 0.5f, 1.0f, 1.0f,
                    x - 0.5f, y + 0.5f, z + 0.5f, 0.0f, 1.0f
            );
        }

        // Faccia inferiore (Y-)
        if (visibleFaces[BOTTOM]) {
            addFaceVertices(visibleVertices,
                    x - 0.5f, y - 0.5f, z + 0.5f, 0.0f, 0.0f,
                    x + 0.5f, y - 0.5f, z + 0.5f, 1.0f, 0.0f,
                    x + 0.5f, y - 0.5f, z - 0.5f, 1.0f, 1.0f,
                    x - 0.5f, y - 0.5f, z - 0.5f, 0.0f, 1.0f
            );
        }

        // Faccia destra (X+)
        if (visibleFaces[RIGHT]) {
            addFaceVertices(visibleVertices,
                    x + 0.5f, y + 0.5f, z + 0.5f, 0.0f, 0.0f,
                    x + 0.5f, y + 0.5f, z - 0.5f, 1.0f, 0.0f,
                    x + 0.5f, y - 0.5f, z - 0.5f, 1.0f, 1.0f,
                    x + 0.5f, y - 0.5f, z + 0.5f, 0.0f, 1.0f
            );
        }

        // Faccia sinistra (X-)
        if (visibleFaces[LEFT]) {
            addFaceVertices(visibleVertices,
                    x - 0.5f, y + 0.5f, z - 0.5f, 0.0f, 0.0f,
                    x - 0.5f, y + 0.5f, z + 0.5f, 1.0f, 0.0f,
                    x - 0.5f, y - 0.5f, z + 0.5f, 1.0f, 1.0f,
                    x - 0.5f, y - 0.5f, z - 0.5f, 0.0f, 1.0f
            );
        }

        float[] result = new float[visibleVertices.size()];
        for (int i = 0; i < visibleVertices.size(); i++) {
            result[i] = visibleVertices.get(i);
        }
        return result;
    }

    private void addFaceVertices(List<Float> vertices,
                                 float x1, float y1, float z1, float u1, float v1,
                                 float x2, float y2, float z2, float u2, float v2,
                                 float x3, float y3, float z3, float u3, float v3,
                                 float x4, float y4, float z4, float u4, float v4) {
        vertices.add(x1); vertices.add(y1); vertices.add(z1); vertices.add(u1); vertices.add(v1);
        vertices.add(x2); vertices.add(y2); vertices.add(z2); vertices.add(u2); vertices.add(v2);
        vertices.add(x3); vertices.add(y3); vertices.add(z3); vertices.add(u3); vertices.add(v3);
        vertices.add(x4); vertices.add(y4); vertices.add(z4); vertices.add(u4); vertices.add(v4);
    }

    public int[] getIndices() {
        // Conta il numero di facce visibili
        int visibleFaceCount = 0;
        for (boolean visible : visibleFaces) {
            if (visible) visibleFaceCount++;
        }

        int[] indices = new int[visibleFaceCount * 6];
        int indexCount = 0;
        int vertexOffset = 0;

        for (int face = 0; face < 6; face++) {
            if (visibleFaces[face]) {
                // Primo triangolo
                indices[indexCount++] = vertexOffset;
                indices[indexCount++] = vertexOffset + 1;
                indices[indexCount++] = vertexOffset + 2;
                // Secondo triangolo
                indices[indexCount++] = vertexOffset;
                indices[indexCount++] = vertexOffset + 2;
                indices[indexCount++] = vertexOffset + 3;
                vertexOffset += 4;
            }
        }

        return indices;
    }

    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public BlockType getType() {
        return type;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isVisible() {
        return isVisible;
    }
}