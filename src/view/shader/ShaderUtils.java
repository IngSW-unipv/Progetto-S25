package view.shader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for loading shader source code from files.
 * Provides a method to read the contents of a shader file into a string.
 */
public class ShaderUtils {

    /**
     * Loads the source code from a shader file.
     *
     * @param filePath The path to the shader file.
     * @return The source code of the shader as a string.
     * @throws RuntimeException if there is an error reading the shader file.
     */
    public static String loadShaderFile(String filePath) {
        // Print the current working directory for debugging purposes
        //System.out.println(System.getProperty("user.dir"));

        StringBuilder shaderSource = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");  // Append each line of the shader file
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading shader file: " + filePath);  // Throw exception if file reading fails
        }
        return shaderSource.toString();  // Return the complete shader source code
    }
}
