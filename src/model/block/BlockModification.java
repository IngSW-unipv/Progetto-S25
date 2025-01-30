package model.block;

import model.block.BlockType;
import org.joml.Vector3f;

import java.io.Serial;
import java.io.Serializable;

public class BlockModification implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final float x;
    private final float y;
    private final float z;
    private final BlockType type;

    public BlockModification(Vector3f position, BlockType type) {
        this.x = position.x();
        this.y = position.y();
        this.z = position.z();
        this.type = type;
    }

    public Vector3f getPosition() {
        return new Vector3f(x, y, z);
    }

    public BlockType getType() {
        return type;
    }
}