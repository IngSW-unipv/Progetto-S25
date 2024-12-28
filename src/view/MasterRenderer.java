package view;

import model.Light;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import model.Camera;

import org.joml.Matrix4f;

public class MasterRenderer {
    private int vaoID;
    private int vboID;
    private int eboID;
    private int vertexCount;

    private ShaderProgram shader;
    private Matrix4f projectionMatrix;

    public MasterRenderer() {
        shader = new ShaderProgram("resources/vertex.glsl", "resources/fragment.glsl");
        projectionMatrix = new Matrix4f().perspective(
                (float) Math.toRadians(70.0f),
                1280.0f / 720.0f,
                0.1f,
                1000.0f
        );
        createCube(); // Aggiungi questa chiamata

        // Abilita il depth testing
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public void render(Camera camera) {
        // Crea una sorgente di luce (puoi personalizzare colore e direzione)
        Light light = new Light(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.0f, -1.0f, 0.0f));



        shader.start(); // Attiva lo shader
        shader.loadMatrix("viewMatrix", camera.getViewMatrix()); // Carica la matrice della vista
        shader.loadMatrix("projectionMatrix", projectionMatrix); // Carica la matrice di proiezione
        shader.loadLight(light); // Passa la luce agli shader

        GL30.glBindVertexArray(vaoID); // Associa l'array dei vertici
        GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0); // Disegna gli oggetti
        GL30.glBindVertexArray(0); // Disassocia l'array dei vertici

        shader.stop(); // Disattiva lo shader
    }


    private void createCube() {
        float[] vertices = {
                // Front face
                -0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                // Back face
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f
        };

        int[] indices = {
                // Front face
                0, 1, 2,
                0, 2, 3,
                // Back face
                4, 5, 6,
                4, 6, 7,
                // Right face
                3, 2, 6,
                3, 6, 7,
                // Left face
                0, 1, 5,
                0, 5, 4,
                // Top face
                0, 3, 7,
                0, 7, 4,
                // Bottom face
                1, 2, 6,
                1, 6, 5
        };

        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);

        eboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboID);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

        vertexCount = indices.length;
        GL30.glBindVertexArray(0);
    }

    public void prepare() {
        GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }
    public void render() {
        GL30.glBindVertexArray(vaoID);
        GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }

    public void cleanUp() {
        GL15.glDeleteBuffers(vboID);
        GL15.glDeleteBuffers(eboID);
        GL30.glDeleteVertexArrays(vaoID);
    }
}