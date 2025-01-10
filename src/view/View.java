package view;

import controller.event.*;
import view.renderer.MasterRenderer;
import view.window.WindowManager;

public class View {
    private final WindowManager displayManager;
    private MasterRenderer renderer;

    private boolean f11Pressed = false;

    public View() {
        this.displayManager = new WindowManager();

        EventBus.getInstance().subscribe(EventType.INPUT, this::onEvent);
    }

    public void createDisplay() {
        displayManager.createDisplay();
        this.renderer = new MasterRenderer(displayManager);
    }

    public WindowManager getDisplayManager() {
        return displayManager;
    }

    public void updateDisplay() {
        displayManager.updateDisplay();
    }

    public void closeDisplay() {
        renderer.cleanUp();
        displayManager.closeDisplay();
    }

    public void onEvent(GameEvent event) {
        if (event instanceof InputEvent inputEvent && inputEvent.action() == InputAction.TOGGLE_FULLSCREEN) {
            handleFullscreenToggle(inputEvent.value());
        }
    }

    private void handleFullscreenToggle(float value) {
        if (value > 0 && !f11Pressed) {
            displayManager.toggleFullscreen();
            f11Pressed = true;
        } else if (value == 0) {
            f11Pressed = false;
        }
    }
}