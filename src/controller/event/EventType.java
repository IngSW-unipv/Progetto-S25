package controller.event;

/**
 * Categorizes different event types in the game.
 */
public enum EventType {
    /** User input events */
    INPUT,

    /** World generation events */
    WORLD_GENERATION,

    /** Rendering events */
    RENDER,

    /** Menu interaction events */
    MENU,

    /** Block modification events - needed for the leaderboard */
    BLOCK_MODIFICATION
}