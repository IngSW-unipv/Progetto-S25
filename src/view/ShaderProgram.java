package view;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class ShaderProgram {

    private int programID;

    public ShaderProgram(String vertexShaderPath, String fragmentShaderPath) {
        // Carica il codice sorgente degli shader
        String vertexShaderSource = ShaderUtils.loadShaderFile(vertexShaderPath);
        String fragmentShaderSource = ShaderUtils.loadShaderFile(fragmentShaderPath);

        // Crea e compila gli shader
        int vertexShaderID = loadShader(vertexShaderSource, GL20.GL_VERTEX_SHADER);
        int fragmentShaderID = loadShader(fragmentShaderSource, GL20.GL_FRAGMENT_SHADER);

        // Crea il programma shader e linka gli shader
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);

        // Controlla eventuali errori nel linking
        if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetProgramInfoLog(programID, 500));
            throw new RuntimeException("Could not link shader program.");
        }

        // Pulisce gli shader compilati (non più necessari dopo il linking)
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
    }

    // Metodo per caricare un singolo shader
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

    // Metodo per attivare il programma shader
    public void start() {
        GL20.glUseProgram(programID);
    }

    // Metodo per disattivare il programma shader
    public void stop() {
        GL20.glUseProgram(0);
    }

    // Metodo per caricare una matrice come uniform
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

    // Metodo per ottenere l'ID del programma shader
    public int getProgramID() {
        return programID;
    }

    // Metodo per attivare il programma shader (alias bind)
    public void bind() {
        GL20.glUseProgram(programID);
    }

    // Metodo per disattivare il programma shader (alias unbind)
    public static void unbind() {
        GL20.glUseProgram(0);
    }
}