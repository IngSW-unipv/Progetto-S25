import controller.InputController;
import model.Model;
import view.View;
import controller.GameController;

public class Main {
    public static void main(String[] args) {
        Model model = new Model();

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