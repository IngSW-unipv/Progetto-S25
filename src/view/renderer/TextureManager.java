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
 * Manages loading and caching of OpenGL textures.
 * Provides centralized texture resource management and cleanup.
 */
public class TextureManager {
    /** Maps file paths to OpenGL texture IDs */
    private final Map<String, Integer> textureMap = new HashMap<>();


    /**
     * Loads texture from file or returns cached ID if already loaded
     *
     * @param path Path to texture file
     * @return OpenGL texture ID
     * @throws RuntimeException if texture loading fails
     */
    public int loadTexture(String path) {
        // Return cached texture if available
        if (textureMap.containsKey(path)) {
            return textureMap.get(path);
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Verify file exists
            File file = new File(path);
            if (!file.exists()) {
                throw new RuntimeException("Texture file not found: " + file.getAbsolutePath());
            }

            // Load image data
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);
            ByteBuffer imageData = STBImage.stbi_load(path, width, height, channels, 4);

            if (imageData == null) {
                throw new RuntimeException("Failed to load texture: " + path +
                    "\nReason: " + STBImage.stbi_failure_reason());
            }

            // Generate and configure texture
            int textureID = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

            // Set texture parameters
            configureTexture();

            // Upload texture data
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,
                width.get(), height.get(), 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);

            // Generate mipmaps
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

            // Cleanup and cache
            STBImage.stbi_image_free(imageData);
            textureMap.put(path, textureID);

            return textureID;
        }
    }

    /**
     * Sets OpenGL texture parameters
     */
    private void configureTexture() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    }

    /**
     * Binds texture to specified texture unit
     *
     * @param textureID OpenGL texture ID
     * @param slot Texture unit slot
     */
    public void bindTexture(int textureID, int slot) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + slot);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
    }

    /**
     * Deletes all loaded textures and clears cache
     */
    public void cleanup() {
        for (int textureID : textureMap.values()) {
            GL11.glDeleteTextures(textureID);
        }
        textureMap.clear();
    }
}