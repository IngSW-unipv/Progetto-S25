package model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Chunk {
    private final ChunkPosition position;
    private final Map<Position, Block> blocks = new HashMap<>();

    public Chunk(ChunkPosition position) {
        this.position = position;
    }

    public void setBlock(Block block) {
        blocks.put(block.getPosition(), block);
    }

    public Block getBlock(Position position) {
        return blocks.get(position);
    }

    public Collection<Block> getBlocks() {
        return blocks.values();
    }

    public ChunkPosition getPosition() {
        return position;
    }
}