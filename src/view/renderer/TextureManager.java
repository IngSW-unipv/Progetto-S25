package view.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    private Map<String, Integer> textureMap;

    public TextureManager() {
        this.textureMap = new HashMap<>();
    }

    public int loadTexture(String path) {
        if (textureMap.containsKey(path)) {
            return textureMap.get(path);
        }

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer imageData = STBImage.stbi_load(path, width, height, channels, 4);

        if (imageData == null) {
            throw new RuntimeException("Failed to load texture: " + path + "\n" + STBImage.stbi_failure_reason());
        }

        int textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width.get(), height.get(),
                0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        STBImage.stbi_image_free(imageData);
        textureMap.put(path, textureID);

        return textureID;
    }

    public void bindTexture(int textureID, int slot) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + slot);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
    }

    public void cleanup() {
        for (int textureID : textureMap.values()) {
            GL11.glDeleteTextures(textureID);
        }
    }
}