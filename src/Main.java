import model.Model;
import view.View;
import controller.Controller;

public class Main {
    public static void main(String[] args) {
        // Creazione del modello di gioco
        Model model = new Model();

        // Creazione della vista
        View view = new View();
        view.createDisplay();

        // Creazione del controller
        Controller controller = new Controller(model, view);

        // Ciclo di gioco
        while (!controller.shouldClose()) {
            controller.handleInput();
            controller.updateGame();
            view.render(model.getGame(), model.getCamera());
            view.updateDisplay();
        }

        // Chiusura delle risorse
        view.closeDisplay();
    }
}