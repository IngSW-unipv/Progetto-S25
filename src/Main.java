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
            controller.handleInput();  // Gestisce gli input
            controller.updateGame();   // Aggiorna la logica del gioco
            view.render(model.getGame());  // Renderizza il gioco
            view.updateDisplay();  // Aggiorna la finestra
        }

        // Chiusura della finestra
        view.closeDisplay();
    }
}
