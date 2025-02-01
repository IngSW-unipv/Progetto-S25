package model.block;

import org.joml.Vector3f;

import java.io.Serial;
import java.io.Serializable;

/**
 * Serializable record of block modifications in the world.
 * Stores position and type for saving/loading block changes.
 */
public class BlockModification implements Serializable {
    /** Serialization version */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Block coordinates */
    private final float x;
    private final float y;
    private final float z;

    /** Block type, null if removed */
    private final BlockType type;


    /**
     * Creates block modification record
     */
    public BlockModification(Vector3f position, BlockType type) {
        this.x = position.x();
        this.y = position.y();
        this.z = position.z();
        this.type = type;
    }

    /** Gets position of modified block */
    public Vector3f getPosition() {
        return new Vector3f(x, y, z);
    }

    /** Gets type of modified block */
    public BlockType getType() {
        return type;
    }
}