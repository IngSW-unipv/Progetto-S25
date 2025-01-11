package model;

public enum BlockType {
    DIRT("dirt.png", 1.0f),  // 1 secondo
    GRASS("grass.png", 1.0f),  // 1 secondo
    STONE("stone.png", 5.0f),  // 5 secondi
    BEDROCK("bedrock.png", Float.POSITIVE_INFINITY);  // Non rompibile

    private final String texturePath;
    private final float breakTime;  // in secondi

    BlockType(String texturePath, float breakTime) {
        this.texturePath = System.getProperty("user.dir") + "/resources/textures/" + texturePath;
        this.breakTime = breakTime;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public float getBreakTime() {
        return breakTime;
    }
}