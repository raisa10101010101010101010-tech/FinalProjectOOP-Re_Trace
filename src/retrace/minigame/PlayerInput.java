package retrace.minigame;

/** An input the player performed on the minigame map (Observer event). */
public record PlayerInput(Action action, int row, int col, boolean valid) {
    public enum Action { FILL, MARK, ERASE, REVEAL, FLAG, TURN, PAUSE }
}