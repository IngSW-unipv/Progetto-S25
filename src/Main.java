import config.ConfigManager;
import controller.menu.MenuController;

/**
 * Main entry point for the voxel game application.
 * Handles configuration loading and initial menu setup.
 */
public class Main {

    /**
     * Starts the application by loading config and initializing menu.
     *
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
        // Load saved configuration and start menu
        ConfigManager.loadConfig();
        new MenuController();
    }
}