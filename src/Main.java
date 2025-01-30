import config.ConfigManager;
import controller.menu.MenuController;

/**
 * Main entry point of the application.
 * This class is responsible for loading the configuration and initializing the view.menu system.
 */
public class Main {

    /**
     * The main method that starts the application.
     * It loads the saved configuration and then initializes and displays the view.menu.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        ConfigManager.loadConfig();
        new MenuController();
    }
}
