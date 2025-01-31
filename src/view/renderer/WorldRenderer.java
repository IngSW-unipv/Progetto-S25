package view.renderer;

import model.block.Block;
import model.player.Camera;
import model.world.World;

import java.util.List;

/**
 * Defines interface for 3D world rendering.
 * Implementations handle block rendering from camera perspective.
 */
public interface WorldRenderer {

    /**
     * Renders visible world blocks from camera view.
     *
     * @param blocks Blocks to render
     * @param camera View camera
     * @param world Game world reference
     */
    void render(List<Block> blocks, Camera camera, World world);
}