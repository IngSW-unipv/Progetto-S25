package view.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the loading, binding, and cleanup of textures in the OpenGL context.
 * It stores textures in a map to avoid reloading the same texture multiple times.
 */
public class TextureManager {
    private final Map<String, Integer> textureMap;

    /**
     * Constructs a new TextureManager.
     * Initializes the texture map to store loaded textures.
     */
    public TextureManager() {
        this.textureMap = new HashMap<>();
    }

    /**
     * Loads a texture from the specified file path.
     * If the texture has already been loaded, returns the cached texture ID.
     *
     * @param path The file path to the texture image.
     * @return The OpenGL texture ID.
     * @throws RuntimeException if the texture file is not found or cannot be loaded.
     */
    public int loadTexture(String path) {
        // Check if the texture is already loaded
        if (textureMap.containsKey(path)) {
            return textureMap.get(path);
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Verify file existence
            File file = new File(path);
            if (!file.exists()) {
                throw new RuntimeException("Texture file not found: " + file.getAbsolutePath());
            }

            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            // Load the image
            STBImage.stbi_set_flip_vertically_on_load(true);
            ByteBuffer imageData = STBImage.stbi_load(path, width, height, channels, 4);
            if (imageData == null) {
                throw new RuntimeException("Failed to load texture: " + path + "\nReason: " + STBImage.stbi_failure_reason());
            }

            // Create and configure OpenGL texture
            int textureID = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

            // Set texture parameters
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            // Load texture data into OpenGL
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width.get(), height.get(),
                    0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);

            // Generate mipmaps
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

            // Free image memory
            STBImage.stbi_image_free(imageData);

            // Cache the texture ID
            textureMap.put(path, textureID);

            return textureID;
        }
    }

    /**
     * Binds a texture to the specified texture unit.
     *
     * @param textureID The OpenGL texture ID.
     * @param slot The texture unit to bind the texture to.
     */
    public void bindTexture(int textureID, int slot) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + slot);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
    }

    /**
     * Cleans up all loaded textures by deleting them from OpenGL.
     * This should be called when the application is done using textures.
     */
    public void cleanup() {
        for (int textureID : textureMap.values()) {
            GL11.glDeleteTextures(textureID);
        }
        textureMap.clear();
    }
}
