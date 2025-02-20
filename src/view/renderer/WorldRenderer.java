package view.renderer;

import model.block.AbstractBlock;
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
     * @param abstractBlocks Blocks to render
     * @param camera View camera
     * @param world Game world reference
     */
    void render(List<AbstractBlock> abstractBlocks, Camera camera, World world);
}