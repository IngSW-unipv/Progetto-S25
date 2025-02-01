package model.save;

import model.world.WorldData;
import java.io.*;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages saving and loading of world data and metadata.
 * Handles world storage, versioning, and cleanup operations.
 */
public class WorldManager {
    /** Storage directories and files */
    private static final String WORLDS_DIR = "worlds";
    private static final String SAVES_DIR = WORLDS_DIR + File.separator + "saves";
    private static final String METADATA_FILE = WORLDS_DIR + File.separator + "worlds.properties";


    /**
     * Creates required storage directories.
     * Called during application startup.
     */
    public static void initialize() {
        new File(WORLDS_DIR).mkdirs();
        new File(SAVES_DIR).mkdirs();
    }

    /**
     * Saves world metadata to properties file.
     * Each world gets unique UUID for identification.
     *
     * @param world World data to save metadata for
     */
    public static void saveWorldMetadata(WorldData world) {
        try {
            Properties props = loadProperties();
            String worldId = UUID.randomUUID().toString();

            // Store world properties
            props.setProperty(worldId + ".name", world.name());
            props.setProperty(worldId + ".seed", String.valueOf(world.seed()));

            // Save to metadata file
            try (FileOutputStream out = new FileOutputStream(METADATA_FILE)) {
                props.store(out, "World Metadata");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves world state data to binary file.
     * Creates new versioned save under world directory.
     *
     * @param worldName Name of world to save
     * @param saveData World state data to save
     */
    public static void saveWorldData(String worldName, WorldSaveData saveData) {
        Properties props = loadProperties();
        String newWorldId = UUID.randomUUID().toString();
        File worldDir = new File(SAVES_DIR + File.separator + newWorldId);
        worldDir.mkdirs();

        // Write serialized data
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(new File(worldDir, "data.dat")))) {
            out.writeObject(saveData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads most recent world save data.
     * Returns null if no saves exist.
     *
     * @param worldName World to load data for
     * @return Latest WorldSaveData or null
     */
    public static WorldSaveData loadWorldData(String worldName) {
        Properties props = loadProperties();

        // Find latest save based on modified time
        String latestSaveId = null;
        long latestModified = 0;

        File savesDir = new File(SAVES_DIR);
        if (savesDir.exists()) {
            for (File worldDir : savesDir.listFiles()) {
                File saveFile = new File(worldDir, "data.dat");
                if (saveFile.exists() && saveFile.lastModified() > latestModified) {
                    String id = worldDir.getName();
                    if (worldName.equals(props.getProperty(id + ".name"))) {
                        latestSaveId = id;
                        latestModified = saveFile.lastModified();
                    }
                }
            }
        }

        // Load save data if found
        if (latestSaveId != null) {
            try (ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream(new File(SAVES_DIR + File.separator + latestSaveId + File.separator + "data.dat")))) {
                return (WorldSaveData) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Gets list of all saved worlds.
     * Reads metadata for world info.
     *
     * @return List of WorldData objects
     */
    public static List<WorldData> getWorlds() {
        List<WorldData> worlds = new ArrayList<>();
        Properties props = loadProperties();

        // Parse metadata for world entries
        for (String key : props.stringPropertyNames()) {
            if (key.endsWith(".name")) {
                String id = key.substring(0, key.length() - 5);
                String name = props.getProperty(key);
                long seed = Long.parseLong(props.getProperty(id + ".seed"));
                worlds.add(new WorldData(name, seed));
            }
        }

        return worlds;
    }

    /**
     * Deletes world and all associated saves.
     * Removes metadata and save files.
     *
     * @param worldName World to delete
     */
    public static void deleteWorld(String worldName) {
        Properties props = loadProperties();
        List<String> idsToRemove = new ArrayList<>();

        // Find all saves for this world
        for (String key : props.stringPropertyNames()) {
            if (key.endsWith(".name") && worldName.equals(props.getProperty(key))) {
                String id = key.substring(0, key.length() - 5);
                idsToRemove.add(id);
            }
        }

        // Delete metadata and files
        for (String id : idsToRemove) {
            props.remove(id + ".name");
            props.remove(id + ".seed");

            File worldDir = new File(SAVES_DIR + File.separator + id);
            if (worldDir.exists()) {
                File[] files = worldDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
                worldDir.delete();
            }
        }

        // Update metadata file
        try (FileOutputStream out = new FileOutputStream(METADATA_FILE)) {
            props.store(out, "World Metadata");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads world metadata properties.
     * Creates empty properties if file missing.
     *
     * @return Properties containing world metadata
     */
    private static Properties loadProperties() {
        Properties props = new Properties();
        File metadataFile = new File(METADATA_FILE);

        if (metadataFile.exists()) {
            try (FileInputStream in = new FileInputStream(metadataFile)) {
                props.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return props;
    }
}