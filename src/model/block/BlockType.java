package model.block;

/**
 * Block types with associated properties and textures.
 * Defines hardness, appearance and break mechanics.
 */
public enum BlockType {
    DIRT("dirt.png", 1.0f, true),        // Standard opaque
    GRASS("grass.png", 1.0f, false),     // Test transparent
    STONE("stone.png", 5.0f, true),      // Standard opaque
    BEDROCK("bedrock.png", Float.POSITIVE_INFINITY, true); // Unbreakable

    /** Block properties */
    private final String texturePath;
    private final float breakTime;
    private final boolean opaque;


    /**
     * Creates block type with texture and properties
     */
    BlockType(String textureFile, float breakTime, boolean opaque) {
        String TEXTURE_DIRECTORY = System.getProperty("user.dir") + "/resources/textures/";
        this.texturePath = TEXTURE_DIRECTORY + textureFile;
        this.breakTime = breakTime;
        this.opaque = opaque;
    }

    /** Gets texture path */
    public String getTexturePath() {
        return texturePath;
    }

    /** Gets break duration */
    public float getBreakTime() {
        return breakTime;
    }

    /** Gets opacity state */
    public boolean isOpaque() {
        return opaque;
    }

    /** Checks if unbreakable */
    public boolean isUnbreakable() {
        return Float.isInfinite(breakTime);
    }
}