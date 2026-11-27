package retrace.command;

import retrace.puzzle.NonogramBoard;

/** Base for commands that set one box of the nonogram board and can restore it. */
public abstract class BoxCommand implements Command {
    private final NonogramBoard board;
    private final int row, col, value;
    private int previous;

    protected BoxCommand(NonogramBoard board, int row, int col, int value) {
        this.board = board;
        this.row = row;
        this.col = col;
        this.value = value;
    }

    @Override
    public void execute() {
        previous = board.get(row, col);
        board.set(row, col, value);
    }

    @Override
    public void undo() {
        board.set(row, col, previous);
    }
}