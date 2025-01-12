package config;

import java.io.*;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "src/config/game_config.properties";

    public static void saveConfig() {
        Properties props = new Properties();
        props.setProperty("RENDER_DISTANCE", String.valueOf(GameConfig.RENDER_DISTANCE));
        props.setProperty("CAMERA_MOVE_SPEED", String.valueOf(GameConfig.CAMERA_MOVE_SPEED));
        props.setProperty("CAMERA_MOUSE_SENSITIVITY", String.valueOf(GameConfig.CAMERA_MOUSE_SENSITIVITY));
        props.setProperty("CAMERA_MOVEMENT_INCREMENT", String.valueOf(GameConfig.CAMERA_MOVEMENT_INCREMENT));
        props.setProperty("GRAVITY", String.valueOf(GameConfig.GRAVITY));
        props.setProperty("JUMP_FORCE", String.valueOf(GameConfig.JUMP_FORCE));
        props.setProperty("TERMINAL_VELOCITY", String.valueOf(GameConfig.TERMINAL_VELOCITY));

        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Game Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            saveConfig(); // Save default values
            return;
        }

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(configFile)) {
            props.load(in);

            GameConfig.RENDER_DISTANCE = Integer.parseInt(props.getProperty("RENDER_DISTANCE"));
            GameConfig.CAMERA_MOVE_SPEED = Float.parseFloat(props.getProperty("CAMERA_MOVE_SPEED"));
            GameConfig.CAMERA_MOUSE_SENSITIVITY = Float.parseFloat(props.getProperty("CAMERA_MOUSE_SENSITIVITY"));
            GameConfig.CAMERA_MOVEMENT_INCREMENT = Float.parseFloat(props.getProperty("CAMERA_MOVEMENT_INCREMENT"));
            GameConfig.GRAVITY = Float.parseFloat(props.getProperty("GRAVITY"));
            GameConfig.JUMP_FORCE = Float.parseFloat(props.getProperty("JUMP_FORCE"));
            GameConfig.TERMINAL_VELOCITY = Float.parseFloat(props.getProperty("TERMINAL_VELOCITY"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}