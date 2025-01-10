package view;

import controller.event.*;
import model.Block;
import model.Camera;
import view.renderer.MasterRenderer;
import view.renderer.WorldObserver;
import view.window.WindowManager;

import java.util.List;

public class View implements WorldObserver {
    private boolean f11Pressed = false;

    private final WindowManager displayManager;
    private MasterRenderer renderer;

    private List<Block> currentBlocks;

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

    public void render(Camera camera) {
        renderer.render(currentBlocks, camera);
    }

    public void updateDisplay() {
        displayManager.updateDisplay();
    }

    public void closeDisplay() {
        renderer.cleanUp();
        displayManager.closeDisplay();
    }

    public void onEvent(GameEvent event) {
        if (event instanceof InputEvent inputEvent &&
                inputEvent.getAction() == InputAction.TOGGLE_FULLSCREEN) {
            if (inputEvent.getValue() > 0 && !f11Pressed) {
                displayManager.toggleFullscreen();
                f11Pressed = true;
            } else if (inputEvent.getValue() == 0) {
                f11Pressed = false;
            }
        }
    }

    @Override
    public void onWorldUpdate(List<Block> visibleBlocks) {
        this.currentBlocks = visibleBlocks;
    }
}