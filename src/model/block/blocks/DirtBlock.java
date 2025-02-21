package model.block.blocks;

import model.block.AbstractBlock;
import model.block.BlockType;
import model.block.TerrainBlock;
import org.joml.Vector3f;
import model.world.World;
import util.GameClock;

/**
 * Basic dirt block implementation.
 * Can transform into grass when exposed to air.
 *
 * @see TerrainBlock
 * @see GrassBlock
 */
public class DirtBlock extends TerrainBlock {
    private static final float BREAK_TIME = 1.0f;
    private static final boolean IS_OPAQUE = true;
    private static final String TEXTURE_PATH = "resources/textures/dirt.png";

    // Time in seconds before dirt transforms to grass when exposed
    private static final float TRANSFORMATION_TIME = 5.0f;

    // Track time since block was exposed
    private float exposureTimer = 0.0f;
    private boolean isExposed = false;


    public DirtBlock(Vector3f position) {
        super(position, BREAK_TIME, IS_OPAQUE);
    }

    @Override
    public BlockType getType() {
        return BlockType.DIRT;
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

        boolean currentlyExposed = blockAbove == null || !blockAbove.isOpaque();

        if (currentlyExposed) {
            // Block is exposed to air
            if (!isExposed) {
                // Just became exposed, start timer
                isExposed = true;
                exposureTimer = 0.0f;
            } else {
                // Already exposed, increment timer
                exposureTimer += GameClock.getInstance().getDeltaTime();

                // Check if timer exceeded transformation time
                if (exposureTimer >= TRANSFORMATION_TIME) {
                    // Replace this dirt block with grass
                    world.placeBlock(position, BlockType.GRASS);
                }
            }
        } else {
            // Not exposed, reset timer and state
            isExposed = false;
            exposureTimer = 0.0f;
        }
    }
}