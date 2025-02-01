package view;

import controller.event.*;
import view.renderer.MasterRenderer;
import view.window.WindowManager;

/**
 * Core view component responsible for managing the game's graphical interface.
 * Handles window creation, rendering initialization, and display updates.
 * Provides interface between OpenGL rendering and game logic through the WindowManager.
 * Manages user interface elements and responds to fullscreen toggle events.
 *
 * @see WindowManager
 * @see MasterRenderer
 */
public class View {
    /** Manager for the GLFW window */
    private final WindowManager displayManager;

    /** Primary renderer for game graphics */
    private MasterRenderer renderer;

    /** Tracks F11 key state for fullscreen toggle */
    private boolean f11Pressed = false;


    /**
     * Initializes window manager and input subscriptions.
     */
    public View() {
        // Initialize window manager
        this.displayManager = new WindowManager();

        // Subscribe to fullscreen toggle events
        EventBus.getInstance().subscribe(EventType.INPUT, this::onEvent);
    }

    /**
     * Creates display window and initializes renderer.
     */
    public void createDisplay() {
        // Create window and initialize renderer
        displayManager.createDisplay();
        this.renderer = new MasterRenderer(displayManager);
    }

    /**
     * Gets window manager instance.
     *
     * @return Active window manager
     */
    public WindowManager getDisplayManager() {
        return displayManager;
    }

    /**
     * Updates display each frame.
     */
    public void updateDisplay() {
        displayManager.updateDisplay();
    }

    /**
     * Cleans up resources and closes window.
     */
    public void closeDisplay() {
        // Clean up renderer and window
        renderer.cleanUp();
        displayManager.closeDisplay();
    }

    /**
     * Handles input events for fullscreen toggle.
     *
     * @param event Input event to process
     */
    public void onEvent(GameEvent event) {
        // Check for fullscreen toggle input
        if (event instanceof InputEvent inputEvent &&
                inputEvent.action() == InputAction.TOGGLE_FULLSCREEN) {
            handleFullscreenToggle(inputEvent.value());
        }
    }

    /**
     * Processes fullscreen toggle input.
     *
     * @param value Input value (positive triggers toggle)
     */
    private void handleFullscreenToggle(float value) {
        // Toggle fullscreen on key press
        if (value > 0 && !f11Pressed) {
            displayManager.toggleFullscreen();
            f11Pressed = true;
        }
        // Reset flag on key release
        else if (value == 0) {
            f11Pressed = false;
        }
    }
}