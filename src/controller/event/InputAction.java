package controller.event;

/**
 * Categorizes possible player input actions.
 */
public enum InputAction {
    /** Movement controls */
    MOVE_FORWARD,
    MOVE_BACKWARD,
    MOVE_LEFT,
    MOVE_RIGHT,
    SPRINT,
    MOVE_UP,
    MOVE_DOWN,

    /** Camera controls */
    LOOK_X,
    LOOK_Y,

    /** Window controls */
    TOGGLE_FULLSCREEN,

    /** Block interaction */
    PLACE_BLOCK,
    DESTROY_BLOCK,

    /** System actions */
    EXIT
}