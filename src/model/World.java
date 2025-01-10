package model;

import java.util.*;

public class World {
    private static final int RENDER_DISTANCE = 8;
    private Set<Block> blocks = new HashSet<>();

    public World() {
        initializeTestWorld();
    }

    public List<Block> getVisibleBlocks() {
        return new ArrayList<>(blocks);
    }

    private void initializeTestWorld() {
        blocks.add(new Block(BlockType.DIRT, new Position(0, 0, 0)));
        blocks.add(new Block(BlockType.DIRT, new Position(0, 0, 1)));
        blocks.add(new Block(BlockType.GRASS, new Position(0, 1, 0)));
        blocks.add(new Block(BlockType.DIRT, new Position(1, 0, 0)));

        blocks.add(new Block(BlockType.GRASS, new Position(0, 1, 1)));
        blocks.add(new Block(BlockType.DIRT, new Position(1, 0, 1)));
        blocks.add(new Block(BlockType.GRASS, new Position(1, 1, 0)));
        blocks.add(new Block(BlockType.GRASS, new Position(1, 1, 1)));
    }
}