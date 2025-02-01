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
 * Serializable container for world save state.
 * Stores block modifications, player position and orientation.
 */
public class WorldSaveData implements Serializable {
    /** Serialization version */
    @Serial
    private static final long serialVersionUID = 1L;

    /** World state data */
    private final List<BlockModification> modifications;
    private final float playerX;
    private final float playerY;
    private final float playerZ;
    private final float playerPitch;
    private final float playerYaw;

    /**
     * Creates save data from current world state
     * @param modifiedBlocks Map of modified block positions and types
     * @param playerPosition Player's current position
     * @param pitch Player's vertical rotation
     * @param yaw Player's horizontal rotation
     */
    public WorldSaveData(Map<Vector3f, BlockType> modifiedBlocks, Vector3f playerPosition, float pitch, float yaw) {
        // Convert block modifications to serializable form
        this.modifications = new ArrayList<>();
        modifiedBlocks.forEach((pos, type) ->
                modifications.add(new BlockModification(pos, type))
        );

        // Store player state
        this.playerX = playerPosition.x();
        this.playerY = playerPosition.y();
        this.playerZ = playerPosition.z();
        this.playerPitch = pitch;
        this.playerYaw = yaw;
    }

    /** Gets block modifications list */
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