package view;

import controller.event.*;
import model.BlockType;
import model.Camera;
import model.Cube;
import view.renderer.MasterRenderer;
import view.window.WindowManager;

public class View {
    private boolean f11Pressed = false;

    private WindowManager displayManager;
    private MasterRenderer renderer;

    public View() {
        this.displayManager = new WindowManager();

        EventBus.getInstance().subscribe(EventType.INPUT, this::onEvent);
    }

    public void createDisplay() {
        displayManager.createDisplay();
        this.renderer = new MasterRenderer(displayManager);

        // RIMUOVERE, caricare l arraylist di entita da renderizzare da World
        this.renderer.loadCube(new Cube(BlockType.DIRT));
    }

    public WindowManager getDisplayManager() {
        return displayManager;
    }

    public void render(Camera camera) {
        renderer.render(camera);
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
}