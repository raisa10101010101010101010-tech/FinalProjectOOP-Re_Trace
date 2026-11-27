package retrace.command;

import retrace.puzzle.NonogramBoard;

public class MarkBoxCommand extends BoxCommand {
    public MarkBoxCommand(NonogramBoard board, int row, int col) {
        super(board, row, col, NonogramBoard.MARKED);
    }
}