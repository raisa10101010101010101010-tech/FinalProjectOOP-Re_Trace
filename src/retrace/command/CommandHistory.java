package retrace.command;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandHistory {
    private final Deque<Command> stack = new ArrayDeque<>();

    public void run(Command command) {
        command.execute();
        stack.push(command);
    }

    public boolean undo() {
        if (stack.isEmpty()) return false;
        stack.pop().undo();
        return true;
    }
}