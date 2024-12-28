package view;

import model.Camera;
import model.Game;

public class View {
    private DisplayManager displayManager;
    private MasterRenderer renderer;

    public View() {
        this.displayManager = new DisplayManager();
    }

    public void createDisplay() {
        displayManager.createDisplay();
        this.renderer = new MasterRenderer();
        // Load the cube mesh data after creating renderer
        this.renderer.loadCube(new Game().getCube());
    }

    public DisplayManager getDisplayManager() {
        return displayManager;
    }

    public void render(Game game, Camera camera) {
        renderer.render(game, camera);
    }

    public void updateDisplay() {
        displayManager.updateDisplay();
    }

    public void closeDisplay() {
        renderer.cleanUp();
        displayManager.closeDisplay();
    }
}