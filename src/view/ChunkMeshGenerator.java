package view;

import model.Block;
import model.Chunk;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import java.util.ArrayList;
import java.util.List;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ChunkMeshGenerator {
    private float[] listToArray(List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private int[] listToIntArray(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private int createVAO(float[] vertices, float[] texCoords, int[] indices) {
        int vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        // Position VBO
        int vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);

        // Texture coordinates VBO
        int texCoordsVBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texCoordsVBO);
        FloatBuffer texCoordBuffer = BufferUtils.createFloatBuffer(texCoords.length);
        texCoordBuffer.put(texCoords).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texCoordBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(1);

        // Index buffer
        int eboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboID);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

        return vaoID;
    }

    public int generateMesh(Chunk chunk) {
        List<Float> vertices = new ArrayList<>();
        List<Float> texCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        int index = 0;
        for(int x = 0; x < Chunk.SIZE; x++) {
            for(int y = 0; y < Chunk.SIZE; y++) {
                for(int z = 0; z < Chunk.SIZE; z++) {
                    Block block = chunk.getBlock(x, y, z);
                    if(block != null) {
                        addBlockToMesh(x, y, z, vertices, texCoords, indices, index);
                        index += 24;
                    }
                }
            }
        }

        return createVAO(listToArray(vertices), listToArray(texCoords), listToIntArray(indices));
    }

    private void addBlockToMesh(int x, int y, int z, List<Float> vertices, List<Float> texCoords, List<Integer> indices, int startIndex) {
        // Front face
        vertices.add(x - 0.5f); vertices.add(y + 0.5f); vertices.add(z + 0.5f);
        vertices.add(x - 0.5f); vertices.add(y - 0.5f); vertices.add(z + 0.5f);
        vertices.add(x + 0.5f); vertices.add(y - 0.5f); vertices.add(z + 0.5f);
        vertices.add(x + 0.5f); vertices.add(y + 0.5f); vertices.add(z + 0.5f);

        // Add other faces similarly...

        // Add texture coordinates for each vertex
        for (int i = 0; i < 4; i++) {
            texCoords.add(0.0f);
            texCoords.add(0.0f);
        }

        // Add indices
        indices.add(startIndex);
        indices.add(startIndex + 1);
        indices.add(startIndex + 2);
        indices.add(startIndex);
        indices.add(startIndex + 2);
        indices.add(startIndex + 3);
    }

    public int getVertexCount(Chunk chunk) {
        int count = 0;
        for(int x = 0; x < Chunk.SIZE; x++) {
            for(int y = 0; y < Chunk.SIZE; y++) {
                for(int z = 0; z < Chunk.SIZE; z++) {
                    Block block = chunk.getBlock(x, y, z);
                    if(block != null) {
                        count += 36; // 6 vertices per face * 6 faces
                    }
                }
            }
        }
        return count;
    }
}