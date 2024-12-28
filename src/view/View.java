package view;

import model.Game;

public class View {

    private DisplayManager displayManager;
    private MasterRenderer renderer;

    public View() {
        this.displayManager = new DisplayManager();
        this.renderer = new MasterRenderer();
    }

    public DisplayManager getDisplayManager() {
        return displayManager;
    }

    public void createDisplay() {
        displayManager.createDisplay();
    }

    public void render(Game game) {
        renderer.prepare();
        renderer.render(game);
    }

    public void updateDisplay() {
        displayManager.updateDisplay();
    }

    public boolean shouldClose() {
        return displayManager.shouldClose();
    }

    public long getWindow() {
        return displayManager.getWindow();
    }

    public void closeDisplay(){
        displayManager.closeDisplay();
    }
}