package model.block;

/**
 * Defines available block types in the voxel world.
 * Each type has unique properties and behavior.
 *
 * @see BlockFactory
 * @see TerrainBlock
 */
public enum BlockType {
    /** Standard dirt block */
    DIRT,

    /** Surface vegetation block */
    GRASS,

    /** Basic stone block */
    STONE,

    /** Indestructible base layer block */
    BEDROCK
}