package controller.event;

/**
 * Menu system action types.
 */
public enum MenuAction {
    /** Navigation actions */
    SHOW_MAIN_MENU,
    SHOW_WORLD_SELECT,
    SHOW_SETTINGS,
    SHOW_NEW_WORLD_DIALOG,
    SHOW_LOAD_WORLD_DIALOG,
    SHOW_STATISTICS,
    BACK_TO_MAIN,

    /** Game state actions */
    RESUME_GAME,
    TOGGLE_PAUSE,
    SAVE_SETTINGS,
    QUIT_GAME
}