package model.save;

import model.block.BlockModification;
import model.block.BlockType;
import org.joml.Vector3f;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Serializable world save state.
 * Stores block modifications and player data.
 */
public class WorldSaveData implements Serializable {
    /** Serialization version */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Save data */
    private final List<BlockModification> modifications;
    private final float playerX;
    private final float playerY;
    private final float playerZ;
    private final float playerPitch;
    private final float playerYaw;


    /** Creates save data from world state */
    public WorldSaveData(Map<Vector3f, BlockType> modifiedBlocks, Vector3f playerPosition, float pitch, float yaw) {
        this.modifications = new ArrayList<>();
        if (modifiedBlocks != null) {
            modifiedBlocks.forEach((pos, type) ->
                    modifications.add(new BlockModification(pos, type))
            );
        }

        this.playerX = playerPosition.x();
        this.playerY = playerPosition.y();
        this.playerZ = playerPosition.z();
        this.playerPitch = pitch;
        this.playerYaw = yaw;
    }

    /** Gets block modifications */
    public List<BlockModification> getModifications() {
        return modifications != null ? modifications : new ArrayList<>();
    }

    /** Gets saved player position */
    public Vector3f getPlayerPosition() {
        return new Vector3f(playerX, playerY, playerZ);
    }

    /** Gets saved pitch angle */
    public float getPlayerPitch() {
        return playerPitch;
    }

    /** Gets saved yaw angle */
    public float getPlayerYaw() {
        return playerYaw;
    }
}