package view.shader;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

/**
 * A class representing an OpenGL shader program.
 * It is responsible for loading, compiling, linking, and managing shaders.
 */
public class ShaderProgram {

    private int programID;

    /**
     * Creates a new shader program by loading, compiling, and linking vertex and fragment shaders.
     *
     * @param vertexShaderPath   The path to the vertex shader file.
     * @param fragmentShaderPath The path to the fragment shader file.
     */
    public ShaderProgram(String vertexShaderPath, String fragmentShaderPath) {
        // Load shader source code
        String vertexShaderSource = ShaderUtils.loadShaderFile(vertexShaderPath);
        String fragmentShaderSource = ShaderUtils.loadShaderFile(fragmentShaderPath);

        // Create and compile the shaders
        int vertexShaderID = loadShader(vertexShaderSource, GL20.GL_VERTEX_SHADER);
        int fragmentShaderID = loadShader(fragmentShaderSource, GL20.GL_FRAGMENT_SHADER);

        // Create the shader program and link the shaders
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);

        // Check for errors during linking
        if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetProgramInfoLog(programID, 500));
            throw new RuntimeException("Could not link shader program.");
        }

        // Clean up the compiled shaders (no longer needed after linking)
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
    }

    /**
     * Loads and compiles a shader from source code.
     *
     * @param source The source code of the shader.
     * @param type   The type of shader (vertex or fragment).
     * @return The shader ID.
     */
    private static int loadShader(String source, int type) {
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, source);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            throw new RuntimeException("Could not compile shader.");
        }
        return shaderID;
    }

    /**
     * Activates the shader program.
     */
    public void start() {
        GL20.glUseProgram(programID);
    }

    /**
     * Deactivates the shader program.
     */
    public void stop() {
        GL20.glUseProgram(0);
    }

    /**
     * Loads a 4x4 matrix as a uniform variable into the shader program.
     *
     * @param uniformName The name of the uniform variable in the shader.
     * @param matrix      The matrix to load into the shader.
     */
    public void loadMatrix(String uniformName, Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            matrix.get(buffer);
            int location = GL20.glGetUniformLocation(programID, uniformName);
            if (location == -1) {
                throw new RuntimeException("Could not find uniform: " + uniformName);
            }
            GL20.glUniformMatrix4fv(location, false, buffer);
        }
    }

    /**
     * Cleans up the OpenGL resources used by the shader program.
     */
    public void cleanup() {
        stop();
        GL20.glDeleteProgram(programID);
    }

    /**
     * Gets the OpenGL program ID of the shader program.
     *
     * @return The program ID.
     */
    public int getProgramID() {
        return programID;
    }
}