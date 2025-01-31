package view.shader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Utility for loading GLSL shader source code.
 * Handles file reading and error handling.
 */
public class ShaderUtils {

    /**
     * Reads shader source from file.
     *
     * @param filePath Path to shader file
     * @return Shader source as string
     * @throws RuntimeException if file read fails
     */
    public static String loadShaderFile(String filePath) {
        StringBuilder shaderSource = new StringBuilder();

        // Read shader file line by line
        try (BufferedReader reader = new BufferedReader(
                new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading shader: " + filePath);
        }

        return shaderSource.toString();
    }
}