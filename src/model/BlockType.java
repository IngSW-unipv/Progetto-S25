package model;

public enum BlockType {
    DIRT("dirt.png"),
    GRASS("grass.png"),
    STONE("stone.png"),
    BEDROCK("bedrock.png");

    private final String texturePath;

    BlockType(String texturePath) {
        this.texturePath = System.getProperty("user.dir") + "/resources/textures/" + texturePath;
    }

    public String getTexturePath() {
        return texturePath;
    }
}