package view.renderer;

import model.Block;
import model.Camera;

import java.util.List;

public interface WorldRenderer {
    void render(List<Block> blocks, Camera camera);
}