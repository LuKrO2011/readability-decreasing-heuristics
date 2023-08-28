package spoon.reflect.visitor;

/**
 * This enum represents the different types of characters that can be printed by the {@link RdcTokenWriter}.
 */
public enum CharacterType {
    /**
     * A newline character.
     */
    NEWLINE,
    /**
     * Increases the tab level.
     */
    INC_TAB,
    /**
     * Decreases the tab level.
     */
    DEC_TAB,
    /**
     * A space character.
     */
    SPACE
}
