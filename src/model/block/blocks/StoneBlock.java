package model.block.blocks;

import model.block.BlockType;
import model.block.TerrainBlock;
import model.world.World;
import org.joml.Vector3f;

public class StoneBlock extends TerrainBlock {
    private static final float BREAK_TIME = 7.0f;
    private static final boolean IS_OPAQUE = true;
    private static final String TEXTURE_PATH = "resources/textures/stone.png";

    public StoneBlock(Vector3f position) {
        super(position, BREAK_TIME, IS_OPAQUE);
    }

    @Override
    public BlockType getType() {
        return BlockType.STONE;
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