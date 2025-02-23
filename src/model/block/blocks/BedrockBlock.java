package model.block.blocks;

import model.block.BlockType;
import model.block.TerrainBlock;
import model.world.World;
import org.joml.Vector3f;

/**
 * Indestructible bedrock block.
 * Forms bottom layer of world.
 *
 * @see TerrainBlock
 */
public class BedrockBlock extends TerrainBlock {
    private static final float BREAK_TIME = Float.POSITIVE_INFINITY;
    private static final boolean IS_OPAQUE = true;
    private static final String TEXTURE_PATH = "resources/textures/bedrock.png";

    public BedrockBlock(Vector3f position) {
        super(position, BREAK_TIME, IS_OPAQUE);
    }

    @Override
    public BlockType getType() {
        return BlockType.BEDROCK;
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
        // Specific update behavior
    }
}