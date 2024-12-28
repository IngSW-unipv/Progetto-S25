package view;

import model.Game;

public class View {

    private DisplayManager displayManager;
    private MasterRenderer renderer;

    public View() {
        this.displayManager = new DisplayManager();
    }

    public DisplayManager getDisplayManager() {
        return displayManager;
    }

    public void createDisplay() {
        displayManager.createDisplay(); // Inizializza il contesto OpenGL
        this.renderer = new MasterRenderer(); // Non deve essere inizializzato nel costruttore della classe View. Invece, crealo dopo che il contesto OpenGL è stato creato
    }

    public void render(Game game) {
        renderer.prepare();
        renderer.render();
    }

    public void updateDisplay() {
        displayManager.updateDisplay();
    }

    public void closeDisplay() {
        renderer.cleanUp();
        displayManager.closeDisplay();
    }
}