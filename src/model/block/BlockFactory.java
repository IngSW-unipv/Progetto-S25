package model.block;

import model.block.blocks.BedrockBlock;
import model.block.blocks.DirtBlock;
import model.block.blocks.GrassBlock;
import model.block.blocks.StoneBlock;
import org.joml.Vector3f;

public class BlockFactory {
    private BlockFactory() {} // Prevent instantiation

    public static AbstractBlock createBlock(BlockType type, Vector3f position) {
        return switch (type) {
            case DIRT -> new DirtBlock(position);
            case GRASS -> new GrassBlock(position);
            case STONE -> new StoneBlock(position);
            case BEDROCK -> new BedrockBlock(position);
            default -> throw new IllegalArgumentException("Unknown block type: " + type);
        };
    }
}