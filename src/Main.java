import config.ConfigManager;
import menu.MenuController;
import menu.MenuModel;
import menu.MenuView;

/**
 * Main entry point of the application.
 * This class is responsible for loading the configuration and initializing the menu system.
 */
public class Main {

    /**
     * The main method that starts the application.
     * It loads the saved configuration and then initializes and displays the menu.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Load saved configuration from file
        ConfigManager.loadConfig();

        // Initialize the menu system
        MenuModel menuModel = new MenuModel(); // Create the model to store menu data
        MenuView menuView = new MenuView();    // Create the view to display the menu
        new MenuController(menuModel, menuView); // Create the controller to handle interactions between model and view

        // Make the menu visible to the user
        menuView.setVisible(true);
    }
}
