package model.block;

import model.block.blocks.BedrockBlock;
import model.block.blocks.DirtBlock;
import model.block.blocks.GrassBlock;
import model.block.blocks.StoneBlock;
import org.joml.Vector3f;

/**
 * Factory for creating block instances based on type.
 * Uses static factory method pattern to instantiate appropriate block subclasses.
 *
 * @see AbstractBlock
 * @see BlockType
 * @see DirtBlock
 * @see GrassBlock
 * @see StoneBlock
 * @see BedrockBlock
 */
public class BlockFactory {
    /** Private constructor to prevent instantiation */
    private BlockFactory() {}

    /**
     * Creates block instance of specified type at position.
     *
     * @param type Block type determining class to instantiate
     * @param position Position in world coordinates
     * @return New block instance of appropriate subclass
     * @throws IllegalArgumentException if type is unknown
     */
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