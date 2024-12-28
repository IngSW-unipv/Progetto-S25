package view;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Texture {
    private final int textureID;

    public Texture(String path) {
        try {
            textureID = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

            // Configurazione texture
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            BufferedImage image = ImageIO.read(new File(path));
            int width = image.getWidth();
            int height = image.getHeight();

            int[] pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);

            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    int pixel = pixels[y * width + x];
                    buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
                    buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green
                    buffer.put((byte) (pixel & 0xFF));         // Blue
                    buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
                }
            }
            buffer.flip();

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        } catch (IOException e) {
            throw new RuntimeException("Error loading texture: " + path, e);
        }
    }

    public void bind(int slot) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + slot);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
    }

    public void cleanup() {
        GL11.glDeleteTextures(textureID);
    }
}