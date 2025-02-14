package model.save;

import model.world.WorldData;

import java.io.*;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages world save data and metadata.
 * Handles saving/loading world states and properties.
 */
public class WorldManager {
    /** Save directories and files */
    private static final String WORLDS_DIR = "worlds";
    private static final String SAVES_DIR = WORLDS_DIR + File.separator + "saves";
    private static final String METADATA_FILE = WORLDS_DIR + File.separator + "worlds.properties";


    /** Creates required directories */
    public static void initialize() {
        new File(WORLDS_DIR).mkdirs();
        new File(SAVES_DIR).mkdirs();
    }

    /**
     * Saves world metadata, checking for duplicates first.
     * @throws IllegalArgumentException if world with same name exists
     */
    public static void saveWorldMetadata(WorldData world) {
        try {
            Properties props = loadProperties();
            props.setProperty(world.name() + ".seed", String.valueOf(world.seed()));
            try (FileOutputStream out = new FileOutputStream(METADATA_FILE)) {
                props.store(out, "World Metadata");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Saves world data to binary file */
    public static void saveWorldData(String worldName, WorldSaveData saveData) {
        // Create or update world directory
        File worldDir = new File(SAVES_DIR + File.separator + worldName);
        worldDir.mkdirs();

        // Save to existing file location or create new
        File saveFile = new File(worldDir, "data.dat");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            out.writeObject(saveData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Loads world data from file */
    public static WorldSaveData loadWorldData(String worldName) {
        File saveFile = new File(SAVES_DIR + File.separator + worldName + File.separator + "data.dat");
        if (!saveFile.exists()) return null;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(saveFile))) {
            return (WorldSaveData) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Gets list of available worlds */
    public static List<WorldData> getWorlds() {
        List<WorldData> worlds = new ArrayList<>();
        Properties props = loadProperties();

        for (String key : props.stringPropertyNames()) {
            if (key.endsWith(".seed")) {
                String worldName = key.substring(0, key.length() - 5);
                long seed = Long.parseLong(props.getProperty(key));
                worlds.add(new WorldData(worldName, seed));
            }
        }

        return worlds;
    }

    /** Loads world properties file */
    private static Properties loadProperties() {
        Properties props = new Properties();
        File metadataFile = new File(METADATA_FILE);

        if (!metadataFile.exists()) {
            // Create directories if they don't exist
            new File(WORLDS_DIR).mkdirs();
            return props;
        }

        try (FileInputStream in = new FileInputStream(metadataFile)) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return props;
    }

    /** Removes world data and metadata */
    public static void deleteWorld(String worldName) {
        // Remove metadata
        Properties props = loadProperties();
        props.remove(worldName + ".seed");
        try (FileOutputStream out = new FileOutputStream(METADATA_FILE)) {
            props.store(out, "World Metadata");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Delete save files
        File worldDir = new File(SAVES_DIR + File.separator + worldName);
        if (worldDir.exists()) {
            File[] files = worldDir.listFiles();
            if (files != null) {
                for (File file : files) file.delete();
            }
            worldDir.delete();
        }
    }

    /**
     * Gets seed for specified world name
     * @param worldName Name of world to get seed for
     * @return World seed or 0 if not found
     */
    public static long getWorldSeed(String worldName) {
        Properties props = loadProperties();
        String seedStr = props.getProperty(worldName + ".seed");
        return seedStr != null ? Long.parseLong(seedStr) : 0;
    }
}