package model;

import java.util.ArrayList;
import java.util.List;

public class World {
    //private List<> entities;
    private static final int RENDER_DISTANCE = 8;

    public World() {
        //entities = new ArrayList<>();
        initializeTestWorld();
    }

    private void initializeTestWorld() {
        // Add test block - will be replaced with proper world generation later
        //entities.add(new Block(BlockType.DIRT));
    }

    //public List<> getEntities() {
    //    return entities;
    //}
}