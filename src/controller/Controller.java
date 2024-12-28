package controller;

import model.Model;
import view.View;

public class Controller {
    private InputManager inputManager;
    private Model model;
    private View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        this.inputManager = new InputManager(view.getDisplayManager().getWindow());
    }

    public void handleInput() {
        inputManager.pollInput(model.getCamera());
    }

    public void updateGame() {
        model.updateGame();
    }

    public boolean shouldClose() {
        return view.getDisplayManager().shouldClose();
    }
}