package model.block;

import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

public abstract class TerrainBlock extends AbstractBlock {
    private final float breakTime;
    private final boolean opaque;

    protected TerrainBlock(Vector3f position, float breakTime, boolean opaque) {
        super(position);
        this.breakTime = breakTime;
        this.opaque = opaque;
    }

    public abstract String getTexturePath();

    @Override
    public float getBreakTime() {
        return breakTime;
    }

    @Override
    public boolean isUnbreakable() {
        return breakTime == Float.POSITIVE_INFINITY;
    }

    @Override
    public boolean isOpaque() {
        return opaque;
    }

    @Override
    public float[] getVertices() {
        List<Float> vertices = new ArrayList<>();
        float x = position.x();
        float y = position.y();
        float z = position.z();

        if (visibleFaces[FRONT]) {
            addFaceVertices(vertices,
                    x - 0.5f, y + 0.5f, z + 0.5f, 0.0f, 0.0f,
                    x + 0.5f, y + 0.5f, z + 0.5f, 1.0f, 0.0f,
                    x + 0.5f, y - 0.5f, z + 0.5f, 1.0f, 1.0f,
                    x - 0.5f, y - 0.5f, z + 0.5f, 0.0f, 1.0f
            );
        }

        if (visibleFaces[BACK]) {
            addFaceVertices(vertices,
                    x + 0.5f, y + 0.5f, z - 0.5f, 0.0f, 0.0f,
                    x - 0.5f, y + 0.5f, z - 0.5f, 1.0f, 0.0f,
                    x - 0.5f, y - 0.5f, z - 0.5f, 1.0f, 1.0f,
                    x + 0.5f, y - 0.5f, z - 0.5f, 0.0f, 1.0f
            );
        }

        if (visibleFaces[TOP]) {
            addFaceVertices(vertices,
                    x - 0.5f, y + 0.5f, z - 0.5f, 0.0f, 0.0f,
                    x + 0.5f, y + 0.5f, z - 0.5f, 1.0f, 0.0f,
                    x + 0.5f, y + 0.5f, z + 0.5f, 1.0f, 1.0f,
                    x - 0.5f, y + 0.5f, z + 0.5f, 0.0f, 1.0f
            );
        }

        if (visibleFaces[BOTTOM]) {
            addFaceVertices(vertices,
                    x - 0.5f, y - 0.5f, z + 0.5f, 0.0f, 0.0f,
                    x + 0.5f, y - 0.5f, z + 0.5f, 1.0f, 0.0f,
                    x + 0.5f, y - 0.5f, z - 0.5f, 1.0f, 1.0f,
                    x - 0.5f, y - 0.5f, z - 0.5f, 0.0f, 1.0f
            );
        }

        if (visibleFaces[RIGHT]) {
            addFaceVertices(vertices,
                    x + 0.5f, y + 0.5f, z + 0.5f, 0.0f, 0.0f,
                    x + 0.5f, y + 0.5f, z - 0.5f, 1.0f, 0.0f,
                    x + 0.5f, y - 0.5f, z - 0.5f, 1.0f, 1.0f,
                    x + 0.5f, y - 0.5f, z + 0.5f, 0.0f, 1.0f
            );
        }

        if (visibleFaces[LEFT]) {
            addFaceVertices(vertices,
                    x - 0.5f, y + 0.5f, z - 0.5f, 0.0f, 0.0f,
                    x - 0.5f, y + 0.5f, z + 0.5f, 1.0f, 0.0f,
                    x - 0.5f, y - 0.5f, z + 0.5f, 1.0f, 1.0f,
                    x - 0.5f, y - 0.5f, z - 0.5f, 0.0f, 1.0f
            );
        }

        float[] result = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            result[i] = vertices.get(i);
        }
        return result;
    }

    @Override
    public int[] getIndices() {
        int visibleFaceCount = 0;
        for (boolean visible : visibleFaces) {
            if (visible) visibleFaceCount++;
        }

        int[] indices = new int[visibleFaceCount * 6];
        int indexCount = 0;
        int vertexOffset = 0;

        for (int face = 0; face < 6; face++) {
            if (visibleFaces[face]) {
                indices[indexCount++] = vertexOffset;
                indices[indexCount++] = vertexOffset + 1;
                indices[indexCount++] = vertexOffset + 2;
                indices[indexCount++] = vertexOffset;
                indices[indexCount++] = vertexOffset + 2;
                indices[indexCount++] = vertexOffset + 3;
                vertexOffset += 4;
            }
        }

        return indices;
    }
}