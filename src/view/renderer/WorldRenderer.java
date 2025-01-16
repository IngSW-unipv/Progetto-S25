package view.renderer;

import model.Block;
import model.Camera;

import java.util.List;

/**
 * Interface for rendering the world in a 3D environment.
 * It defines the method for rendering a list of blocks from the world
 * based on the current camera view.
 */
public interface WorldRenderer {

    /**
     * Renders the world by drawing the provided list of blocks from the perspective of the given camera.
     *
     * @param blocks The list of blocks that make up the world to be rendered.
     * @param camera The camera used to determine the viewpoint for rendering.
     */
    void render(List<Block> blocks, Camera camera);
}
