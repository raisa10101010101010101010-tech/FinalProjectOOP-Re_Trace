package retrace.minigame;

import retrace.core.Difficulty;
import retrace.core.GameContext;

public enum MiniGameType {
    NONOGRAM("Nonogram"),
    SNAKE("Snake"),
    MINESWEEPER("Minesweeper");

    private final String label;

    MiniGameType(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    public String getKey() { return name().toLowerCase(); }

    /** Factory: membuat minigame sesuai tipe. */
    public MiniGame create(GameContext ctx, Difficulty difficulty) {
        return switch (this) {
            case NONOGRAM -> new NonogramGame(ctx, difficulty);
            case SNAKE -> new SnakeGame(ctx, difficulty);
            case MINESWEEPER -> new MinesweeperGame(ctx, difficulty);
        };
    }
}