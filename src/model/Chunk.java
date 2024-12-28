package model;

import org.joml.Vector3i;

import java.util.Random;

public class Chunk {
    public static final int SIZE = 16;
    private final Vector3i position;
    private final Block[][][] blocks;

    public Chunk(Vector3i position) {
        this.position = position;
        this.blocks = new Block[SIZE][SIZE][SIZE];
        generateTerrain();
    }

    private void generateTerrain() {
        Random random = new Random(position.hashCode());
        for(int x = 0; x < SIZE; x++) {
            for(int z = 0; z < SIZE; z++) {
                int height = 8 + random.nextInt(4);
                for(int y = 0; y < SIZE; y++) {
                    if(y < height) {
                        blocks[x][y][z] = new Block(BlockType.DIRT);
                    }
                }
            }
        }
    }

    public Block getBlock(int x, int y, int z) {
        if(x < 0 || x >= SIZE || y < 0 || y >= SIZE || z < 0 || z >= SIZE) return null;
        return blocks[x][y][z];
    }
}