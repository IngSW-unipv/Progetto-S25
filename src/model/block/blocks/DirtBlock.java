package model.block.blocks;

import model.block.BlockType;
import model.block.TerrainBlock;
import org.joml.Vector3f;
import model.world.World;

public class DirtBlock extends TerrainBlock {
    private static final float BREAK_TIME = 1.0f;
    private static final boolean IS_OPAQUE = true;
    private static final String TEXTURE_PATH = "resources/textures/dirt.png";

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
    protected void onUpdate(World world) {
        // Check if can transform to grass
    }
}