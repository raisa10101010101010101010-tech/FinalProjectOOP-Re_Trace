package retrace.command;

public interface Command {
    void execute();
    void undo();
}