package model;

/**
 * Represents the types of blocks available in the game, each with a texture and a break time.
 */
public enum BlockType {
    DIRT("dirt.png", 1.0f),                     // Breakable in 1 second
    GRASS("grass.png", 1.0f),                   // Breakable in 1 second
    STONE("stone.png", 5.0f),                   // Breakable in 5 seconds
    BEDROCK("bedrock.png", Float.POSITIVE_INFINITY);      // Unbreakable

    private final String texturePath;                               // Path to the block's texture file
    private final float breakTime;                                  // Time required to break the block in seconds

    /**
     * Constructs a BlockType with the specified texture file and break time.
     *
     * @param textureFile The name of the texture file for this block type.
     * @param breakTime   The time (in seconds) required to break the block.
     */
    BlockType(String textureFile, float breakTime) {
        String TEXTURE_DIRECTORY = System.getProperty("user.dir") + "/resources/textures/";
        this.texturePath = TEXTURE_DIRECTORY + textureFile;
        this.breakTime = breakTime;
    }

    /**
     * Gets the full path to the texture file for this block type.
     *
     * @return The texture file path.
     */
    public String getTexturePath() {
        return texturePath;
    }

    /**
     * Gets the time required to break this block type.
     *
     * @return The break time in seconds.
     */
    public float getBreakTime() {
        return breakTime;
    }

    /**
     * Determines if the block is unbreakable.
     *
     * @return {@code true} if the block cannot be broken, otherwise {@code false}.
     */
    public boolean isUnbreakable() {
        return Float.isInfinite(breakTime);
    }
}