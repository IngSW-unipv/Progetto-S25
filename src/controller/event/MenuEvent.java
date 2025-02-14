package controller.event;

import model.statistics.DatabaseManager.WorldStats;
import java.util.List;

/**
 * Record representing menu-related events in the game.
 * Handles menu navigation, game start requests, and statistics display.
 * Implements an immutable data carrier pattern for menu event information.
 *
 * @see MenuAction
 * @see controller.menu.MenuController
 * @see model.statistics.DatabaseManager.WorldStats
 */
public record MenuEvent(MenuAction action, String worldName, long seed, List<WorldStats> statistics) implements GameEvent {

    /**
     * Creates a menu action event without additional data.
     * Used for basic menu navigation and system actions.
     *
     * @param action The menu action to trigger
     * @return New MenuEvent with specified action
     */
    public static MenuEvent action(MenuAction action) {
        return new MenuEvent(action, null, 0, null);
    }

    /**
     * Creates a game start event with world data.
     * Used when creating or loading a world to play.
     *
     * @param worldName Name of the world to load or create
     * @param seed World generation seed value
     * @return New MenuEvent configured for game start
     */
    public static MenuEvent startGame(String worldName, long seed) {
        return new MenuEvent(null, worldName, seed, null);
    }

    /**
     * Creates a statistics display event.
     * Used to show world leaderboard and player statistics.
     *
     * @param stats List of world statistics to display
     * @return New MenuEvent configured for statistics display
     */
    public static MenuEvent showStatistics(List<WorldStats> stats) {
        return new MenuEvent(MenuAction.SHOW_STATISTICS, null, 0, stats);
    }

    /**
     * Gets the event type for routing through the event system.
     *
     * @return MENU event type
     */
    @Override
    public EventType getType() {
        return EventType.MENU;
    }
}