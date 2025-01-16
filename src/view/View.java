package view;

import controller.event.*;
import view.renderer.MasterRenderer;
import view.window.WindowManager;

/**
 * The View class is responsible for managing the display and rendering of the game.
 * It handles the creation, update, and closure of the game window, as well as input events related to the view.
 */
public class View {
    private final WindowManager displayManager;  // Manages the window display
    private MasterRenderer renderer;             // Handles the rendering of objects in the window

    private boolean f11Pressed = false;         // Tracks if the F11 key has been pressed for fullscreen toggle

    /**
     * Constructs a new View instance, initializing the display manager and subscribing to input events.
     */
    public View() {
        this.displayManager = new WindowManager(); // Initializes the window manager

        // Subscribe to input events for handling fullscreen toggle
        EventBus.getInstance().subscribe(EventType.INPUT, this::onEvent);
    }

    /**
     * Creates the display by initializing the window and the renderer.
     */
    public void createDisplay() {
        displayManager.createDisplay();           // Creates the display window
        this.renderer = new MasterRenderer(displayManager);  // Initializes the renderer
    }

    /**
     * Gets the display manager for the window.
     *
     * @return the WindowManager instance responsible for managing the display.
     */
    public WindowManager getDisplayManager() {
        return displayManager;
    }

    /**
     * Updates the display, typically called every frame to refresh the window.
     */
    public void updateDisplay() {
        displayManager.updateDisplay();  // Updates the window content
    }

    /**
     * Closes the display and cleans up the resources used by the renderer.
     */
    public void closeDisplay() {
        renderer.cleanUp();              // Cleans up the renderer resources
        displayManager.closeDisplay();   // Closes the display window
    }

    /**
     * Handles the input events. Specifically listens for the fullscreen toggle event.
     *
     * @param event the event that triggered the method.
     */
    public void onEvent(GameEvent event) {
        if (event instanceof InputEvent inputEvent && inputEvent.action() == InputAction.TOGGLE_FULLSCREEN) {
            handleFullscreenToggle(inputEvent.value());
        }
    }

    /**
     * Toggles the fullscreen mode based on the input value.
     *
     * @param value the input value, where a positive value triggers fullscreen mode.
     */
    private void handleFullscreenToggle(float value) {
        if (value > 0 && !f11Pressed) {
            displayManager.toggleFullscreen();  // Toggles fullscreen mode
            f11Pressed = true;  // Prevents multiple toggles while key is held down
        } else if (value == 0) {
            f11Pressed = false;  // Resets the flag when the key is released
        }
    }
}