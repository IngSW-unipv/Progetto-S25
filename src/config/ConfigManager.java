package config;

import java.io.*;
import java.util.Properties;

/**
 * This class manages the loading and saving of game configuration settings.
 * It reads and writes the configuration to a properties file.
 */
public class ConfigManager {
    private static final String CONFIG_FILE = "src/config/game_config.properties"; // Path to the configuration file

    /**
     * Saves the current game configuration to a properties file.
     * It stores various game settings such as render distance, camera speed, gravity, etc.
     */
    public static void saveConfig() {
        Properties props = new Properties();
        props.setProperty("RENDER_DISTANCE", String.valueOf(GameConfig.RENDER_DISTANCE));
        props.setProperty("EYE_HEIGHT", String.valueOf(GameConfig.EYE_HEIGHT));
        props.setProperty("CAMERA_MOVE_SPEED", String.valueOf(GameConfig.CAMERA_MOVE_SPEED));
        props.setProperty("CAMERA_MOUSE_SENSITIVITY", String.valueOf(GameConfig.CAMERA_MOUSE_SENSITIVITY));
        props.setProperty("CAMERA_MOVEMENT_INCREMENT", String.valueOf(GameConfig.CAMERA_MOVEMENT_INCREMENT));
        props.setProperty("GRAVITY", String.valueOf(GameConfig.GRAVITY));
        props.setProperty("JUMP_FORCE", String.valueOf(GameConfig.JUMP_FORCE));
        props.setProperty("TERMINAL_VELOCITY", String.valueOf(GameConfig.TERMINAL_VELOCITY));
        props.setProperty("RAY_MAX_DISTANCE", String.valueOf(GameConfig.RAY_MAX_DISTANCE));
        props.setProperty("STEP", String.valueOf(GameConfig.STEP));

        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Game Configuration"); // Save properties with a comment
        } catch (IOException e) {
            e.printStackTrace(); // Print any IO exceptions
        }
    }

    /**
     * Loads the game configuration from a properties file.
     * If the configuration file does not exist, it creates one with default values.
     */
    public static void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            saveConfig(); // Save default values if the config file doesn't exist
            return;
        }

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(configFile)) {
            props.load(in); // Load properties from the file

            // Set game configuration values from the loaded properties
            GameConfig.RENDER_DISTANCE = Integer.parseInt(props.getProperty("RENDER_DISTANCE"));
            GameConfig.EYE_HEIGHT = Float.parseFloat(props.getProperty("EYE_HEIGHT"));
            GameConfig.CAMERA_MOVE_SPEED = Float.parseFloat(props.getProperty("CAMERA_MOVE_SPEED"));
            GameConfig.CAMERA_MOUSE_SENSITIVITY = Float.parseFloat(props.getProperty("CAMERA_MOUSE_SENSITIVITY"));
            GameConfig.CAMERA_MOVEMENT_INCREMENT = Float.parseFloat(props.getProperty("CAMERA_MOVEMENT_INCREMENT"));
            GameConfig.GRAVITY = Float.parseFloat(props.getProperty("GRAVITY"));
            GameConfig.JUMP_FORCE = Float.parseFloat(props.getProperty("JUMP_FORCE"));
            GameConfig.TERMINAL_VELOCITY = Float.parseFloat(props.getProperty("TERMINAL_VELOCITY"));
            GameConfig.RAY_MAX_DISTANCE = Float.parseFloat(props.getProperty("RAY_MAX_DISTANCE"));
            GameConfig.STEP = Float.parseFloat(props.getProperty("STEP"));
        } catch (IOException e) {
            e.printStackTrace(); // Print any IO exceptions
        }
    }
}
