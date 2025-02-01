package model.save;

import model.block.BlockModification;
import model.block.BlockType;
import org.joml.Vector3f;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorldSaveData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<BlockModification> modifications;
    private final float playerX;
    private final float playerY;
    private final float playerZ;
    private final float playerPitch;
    private final float playerYaw;

    public WorldSaveData(Map<Vector3f, BlockType> modifiedBlocks, Vector3f playerPosition, float pitch, float yaw) {
        // Create BlockModification objects for each modified block
        this.modifications = new ArrayList<>();
        modifiedBlocks.forEach((pos, type) ->
                modifications.add(new BlockModification(pos, type))
        );

        this.playerX = playerPosition.x();
        this.playerY = playerPosition.y();
        this.playerZ = playerPosition.z();
        this.playerPitch = pitch;
        this.playerYaw = yaw;
    }

    public List<BlockModification> getModifications() {
        return modifications != null ? modifications : new ArrayList<>();
    }

    public Vector3f getPlayerPosition() {
        return new Vector3f(playerX, playerY, playerZ);
    }

    public float getPlayerPitch() {
        return playerPitch;
    }

    public float getPlayerYaw() {
        return playerYaw;
    }
}