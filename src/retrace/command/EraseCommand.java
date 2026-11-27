package retrace.command;

import retrace.puzzle.NonogramBoard;

public class EraseCommand extends BoxCommand {
    public EraseCommand(NonogramBoard board, int row, int col) {
        super(board, row, col, NonogramBoard.EMPTY);
    }
}