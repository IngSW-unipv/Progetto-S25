package view;

import model.Camera;
import model.Game;
import view.renderer.MasterRenderer;
import view.window.WindowManager;

public class View {
    private WindowManager displayManager;
    private MasterRenderer renderer;

    public View() {
        this.displayManager = new WindowManager();
    }

    public void createDisplay() {
        displayManager.createDisplay();
        this.renderer = new MasterRenderer();
        // Load the cube mesh data after creating renderer
        this.renderer.loadCube(new Game().getCube());
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
}