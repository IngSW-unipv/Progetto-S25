package model;

public enum BlockType {
    DIRT("dirt.png"),
    GRASS("grass.png"),
    STONE("stone.png"),
    BEDROCK("bedrock.png");

    private final String texturePath;

    BlockType(String texturePath) {
        this.texturePath = texturePath;
    }

    public String getTexturePath() {
        return "resources/" + texturePath;
    }
}