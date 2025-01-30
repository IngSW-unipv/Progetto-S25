package controller.event;

/**
 * Record representing menu-related events - either menu actions or game start requests.
 * Immutable data carrier for menu event information.
 */
public record MenuEvent(MenuAction action, String worldName, long seed) implements GameEvent {

    /**
     * Creates a menu action event without world data.
     *
     * @param action Menu action to trigger
     * @return MenuEvent for the action
     */
    public static MenuEvent action(MenuAction action) {
        return new MenuEvent(action, null, 0);
    }

    /**
     * Creates a game start event with world data.
     *
     * @param worldName Name of world to load/create
     * @param seed World generation seed
     * @return MenuEvent for starting game
     */
    public static MenuEvent startGame(String worldName, long seed) {
        return new MenuEvent(null, worldName, seed);
    }

    @Override
    public EventType getType() {
        return EventType.MENU;
    }
}