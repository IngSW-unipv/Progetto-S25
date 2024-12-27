public class Main {
    public static void main(String[] args) {
        DisplayManager displayManager = new DisplayManager();
        displayManager.createDisplay(); // Assicurati che la finestra sia inizializzata prima

        InputManager inputManager = new InputManager(displayManager.getWindow(), displayManager);
        MasterRenderer renderer = new MasterRenderer();
        Game game = new Game();

        while (!displayManager.shouldClose()) {
            inputManager.pollInput();
            game.update();
            renderer.prepare();
            renderer.render(game);
            displayManager.updateDisplay();
        }

        displayManager.closeDisplay();
    }
}