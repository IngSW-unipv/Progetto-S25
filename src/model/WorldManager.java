package model;

import java.io.*;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;

public class WorldManager {
    private static final String WORLDS_DIR = "worlds";
    private static final String METADATA_FILE = "worlds.properties";

    public static void initialize() {
        File worldsDir = new File(WORLDS_DIR);
        if (!worldsDir.exists()) {
            worldsDir.mkdir();
        }
    }

    public static void saveWorldMetadata(WorldData world) {
        Properties props = loadProperties();
        props.setProperty(world.name() + ".seed", String.valueOf(world.seed()));

        try (FileOutputStream out = new FileOutputStream(WORLDS_DIR + File.separator + METADATA_FILE)) {
            props.store(out, "World Metadata");
        } catch (IOException e) {
            e.printStackTrace();
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
        File metadataFile = new File(WORLDS_DIR + File.separator + METADATA_FILE);

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