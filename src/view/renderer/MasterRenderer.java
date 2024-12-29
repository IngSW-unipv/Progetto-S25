package view.renderer;

import model.BlockType;
import model.Camera;
import model.Cube;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import view.shader.ShaderProgram;
import view.window.WindowManager;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class MasterRenderer {
    private int vaoID;
    private int vboID;
    private int eboID;
    private int vertexCount;
    private ShaderProgram shader;
    private Matrix4f projectionMatrix;
    private Matrix4f modelMatrix; // Aggiunta
    private TextureManager textureManager;
    private Map<BlockType, Integer> blockTextureIds;

    private WindowManager windowManager;

    public MasterRenderer(WindowManager windowManager) {
        this.windowManager = windowManager;
        shader = new ShaderProgram("resources/shaders/block_vertex.glsl", "resources/shaders/block_fragment.glsl");
        textureManager = new TextureManager();
        blockTextureIds = new HashMap<>();

        updateProjectionMatrix();

        modelMatrix = new Matrix4f().identity();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_FRONT);
    }

    private void updateProjectionMatrix() {
        projectionMatrix = new Matrix4f().perspective(
            (float) Math.toRadians(70.0f),
            windowManager.getAspectRatio(),
            0.1f,
            1000.0f
        );
    }

    public void render(Camera camera) {
        updateProjectionMatrix();

        prepare();
        shader.start();

        shader.loadMatrix("viewMatrix", camera.getViewMatrix());
        shader.loadMatrix("projectionMatrix", projectionMatrix);
        shader.loadMatrix("modelMatrix", modelMatrix);

        GL30.glBindVertexArray(vaoID);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        textureManager.bindTexture(blockTextureIds.get(BlockType.DIRT), 0);
        GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

        shader.stop();
    }

    public void prepare() {
        GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    private void loadBlockTexture(BlockType type) {
        if (!blockTextureIds.containsKey(type)) {
            int textureId = textureManager.loadTexture(type.getTexturePath());
            blockTextureIds.put(type, textureId);
        }
    }

    public void loadCube(Cube cube) {
        loadBlockTexture(cube.getType());
        float[] vertices = cube.getVertices();
        int[] indices = cube.getIndices();

        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 5 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        eboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboID);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

        vertexCount = indices.length;
        GL30.glBindVertexArray(0);
    }

    public void cleanUp() {
        GL15.glDeleteBuffers(vboID);
        GL15.glDeleteBuffers(eboID);
        GL30.glDeleteVertexArrays(vaoID);
        textureManager.cleanup();
        shader.cleanup();
    }
}