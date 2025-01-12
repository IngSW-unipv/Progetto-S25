package menu;

public class MenuModel {
    private MenuState currentState;

    public MenuModel() {
        this.currentState = MenuState.MAIN;
    }

    public MenuState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(MenuState state) {
        this.currentState = state;
    }
}