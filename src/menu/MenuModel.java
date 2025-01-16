package menu;

public class MenuModel {

    /**
     * The current state of the menu, indicating which menu or screen is being displayed.
     */
    private MenuState currentState;

    /**
     * Constructs a MenuModel, initializing the current state to the main menu.
     */
    public MenuModel() {
        this.currentState = MenuState.MAIN;
    }

    /**
     * Returns the current state of the menu.
     *
     * @return The current state of the menu.
     */
    public MenuState getCurrentState() {
        return currentState;
    }

    /**
     * Sets the current state of the menu to the specified state.
     *
     * @param state The new state to set for the menu.
     */
    public void setCurrentState(MenuState state) {
        this.currentState = state;
    }
}
