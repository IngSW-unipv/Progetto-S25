package model.save;

import model.world.WorldData;

import java.io.*;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;

public class WorldManager {
    private static final String WORLDS_DIR = "worlds";
    private static final String SAVES_DIR = WORLDS_DIR + File.separator + "saves";
    private static final String METADATA_FILE = WORLDS_DIR + File.separator + "worlds.properties";

    public static void initialize() {
        File worldsDir = new File(WORLDS_DIR);
        File savesDir = new File(SAVES_DIR);
        if (!worldsDir.exists()) worldsDir.mkdirs();
        if (!savesDir.exists()) savesDir.mkdirs();
    }

    public static void saveWorldMetadata(WorldData world) {
        try {
            Properties props = loadProperties();
            props.setProperty(world.name() + ".seed", String.valueOf(world.seed()));
            try (FileOutputStream out = new FileOutputStream(METADATA_FILE)) {
                props.store(out, "World Metadata");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Log o gestione errore
        }
    }

    public static void saveWorldData(String worldName, WorldSaveData saveData) {
        File worldDir = new File(SAVES_DIR + File.separator + worldName);
        if (!worldDir.exists()) worldDir.mkdirs();

        File saveFile = new File(worldDir, "data.dat");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            out.writeObject(saveData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public static void deleteWorld(String worldName) {
        // Remove from metadata
        Properties props = loadProperties();
        props.remove(worldName + ".seed");
        try (FileOutputStream out = new FileOutputStream(METADATA_FILE)) {
            props.store(out, "World Metadata");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Delete world save directory
        File worldDir = new File(SAVES_DIR + File.separator + worldName);
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
}