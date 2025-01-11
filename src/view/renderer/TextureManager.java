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

public class TextureManager {
    private final Map<String, Integer> textureMap;

    public TextureManager() {
        this.textureMap = new HashMap<>();
    }

    public int loadTexture(String path) {
        if (textureMap.containsKey(path)) {
            return textureMap.get(path);
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Verifica esistenza file
            File file = new File(path);
            if (!file.exists()) {
                throw new RuntimeException("Texture file not found: " + file.getAbsolutePath());
            }

            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            // Carica l'immagine
            STBImage.stbi_set_flip_vertically_on_load(true);
            ByteBuffer imageData = STBImage.stbi_load(path, width, height, channels, 4);
            if (imageData == null) {
                throw new RuntimeException("Failed to load texture: " + path + "\nReason: " + STBImage.stbi_failure_reason());
            }

            // Crea e configura la texture OpenGL
            int textureID = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

            // Imposta i parametri della texture
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            // Carica i dati della texture
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width.get(), height.get(),
                    0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);

            // Genera i mipmaps
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

            // Libera la memoria
            STBImage.stbi_image_free(imageData);

            // Salva l'ID della texture nella mappa
            textureMap.put(path, textureID);

            return textureID;
        }
    }

    public void bindTexture(int textureID, int slot) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + slot);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
    }

    public void cleanup() {
        for (int textureID : textureMap.values()) {
            GL11.glDeleteTextures(textureID);
        }
        textureMap.clear();
    }
}