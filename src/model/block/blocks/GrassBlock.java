package model.block.blocks;

import model.block.AbstractBlock;
import model.block.BlockType;
import model.block.TerrainBlock;
import model.world.World;
import org.joml.Vector3f;

/**
 * Grass-covered dirt block.
 * Can transform back to dirt when covered.
 *
 * @see TerrainBlock
 * @see DirtBlock
 */
public class GrassBlock extends TerrainBlock {
    private static final float BREAK_TIME = 1.0f;
    private static final boolean IS_OPAQUE = true;
    private static final String TEXTURE_PATH = "resources/textures/grass.png";


    public GrassBlock(Vector3f position) {
        super(position, BREAK_TIME, IS_OPAQUE);
    }

    @Override
    public BlockType getType() {
        return BlockType.GRASS;
    }

    @Override
    public boolean isUnbreakable() {
        return false;
    }

    @Override
    public String getTexturePath() {
        return TEXTURE_PATH;
    }

    @Override
    public void onBreak(World world) {
        // Specific breaking behavior
    }

    @Override
    public void onPlace(World world) {
        // Specific placement behavior
    }

    @Override
    public void onUpdate(World world) {
        Vector3f abovePos = new Vector3f(position.x(), position.y() + 1, position.z());
        AbstractBlock blockAbove = world.getBlock(abovePos);

        if (blockAbove != null) {
            BlockType aboveType = blockAbove.getType();

            // Transform instantly if covered by dirt or grass block
            if (aboveType == BlockType.DIRT || aboveType == BlockType.GRASS) {
                world.placeBlock(position, BlockType.DIRT);
            }
        }
    }
}