package retrace.command;

import retrace.puzzle.NonogramBoard;

public class FillBoxCommand extends BoxCommand {
    public FillBoxCommand(NonogramBoard board, int row, int col) {
        super(board, row, col, NonogramBoard.FILLED);
    }
}