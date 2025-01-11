import controller.InputController;
import model.Model;
import view.View;
import controller.GameController;

public class Main {
    public static void main(String[] args) {
        // Create model with random seed by default
        //Model model = new Model();

        // Alternatively, you can specify a seed
        long specificSeed = 12345L;
        Model model = new Model(specificSeed);

        View view = new View();
        view.createDisplay();

        GameController gameController = new GameController(model);
        InputController inputController = new InputController(view.getDisplayManager().getWindow());

        while (model.getGameState().getRunning()) {
            inputController.pollInput();
            gameController.update();
            view.updateDisplay();
        }

        view.closeDisplay();
    }
}