package view.shader;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

/**
 * Manages OpenGL shader programs including compilation, linking and uniforms.
 * Handles both vertex and fragment shaders.
 *
 * @see ShaderUtils
 */
public class ShaderProgram {
    /** OpenGL program ID */
    private final int programID;


    /**
     * Creates and links shader program from source files.
     *
     * @param vertexPath Path to vertex shader source file
     * @param fragmentPath Path to fragment shader source file
     * @throws RuntimeException if shader compilation or linking fails
     */
    public ShaderProgram(String vertexPath, String fragmentPath) {
        String vertexSource = ShaderUtils.loadShaderFile(vertexPath);
        String fragmentSource = ShaderUtils.loadShaderFile(fragmentPath);

        // Compile shaders
        int vertexID = loadShader(vertexSource, GL20.GL_VERTEX_SHADER);
        int fragmentID = loadShader(fragmentSource, GL20.GL_FRAGMENT_SHADER);

        // Create program
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexID);
        GL20.glAttachShader(programID, fragmentID);

        // Link and validate
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);

        if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Shader linking failed: " +
                GL20.glGetProgramInfoLog(programID, 500));
        }

        // Cleanup
        GL20.glDeleteShader(vertexID);
        GL20.glDeleteShader(fragmentID);
    }

    private static int loadShader(String source, int type) {
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, source);
        GL20.glCompileShader(shaderID);

        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Shader compilation failed: " +
                GL20.glGetShaderInfoLog(shaderID, 500));
        }

        return shaderID;
    }

    /**
     * Loads 4x4 matrix uniform
     */
    public void loadMatrix(String name, Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            matrix.get(buffer);

            int location = GL20.glGetUniformLocation(programID, name);
            if (location == -1) {
                throw new RuntimeException("Unknown uniform: " + name);
            }

            GL20.glUniformMatrix4fv(location, false, buffer);
        }
    }

    /**
     * Activates shader program
     */
    public void start() {
        GL20.glUseProgram(programID);
    }

    /**
     * Deactivates shader program
     */
    public void stop() {
        GL20.glUseProgram(0);
    }

    /**
     * Deletes shader program
     */
    public void cleanup() {
        stop();
        GL20.glDeleteProgram(programID);
    }

    public int getProgramID() {
        return programID;
    }
}