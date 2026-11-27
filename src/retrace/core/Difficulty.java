package retrace.core;

/** Player difficulty: allowed errors, save checkpoints, and snake speed. */
public enum Difficulty {
    EASY("Easy", 5, 2, 4.0, 0.20, 8.0, 0,
            "Memories will be gentler to relive. You can err up to 5 times. "
                    + "You can be assured and save your progress after every 2 memories."),
    NORMAL("Normal", 3, 3, 5.0, 0.35, 11.0, 3,
            "Memories will be steadier to relive. You can err up to 3 times. "
                    + "You can be assured and save your progress after every 3 memories."),
    HARD("Hard", 1, 6, 6.0, 0.55, 14.0, 6,
            "Memories will be harder to relive. You only can err once. "
                    + "You can only be assured and save your progress after 6 memories.");

    private final String label;
    private final int allowedErrors;
    private final int saveInterval;          // stages (memories) between save opportunities
    private final double snakeStartSpeed;    // cells per second
    private final double snakeAcceleration;  // cells per second, per second
    private final double snakePeakSpeed;
    private final int snakeExtraTarget;      // extra points needed in Snake
    private final String description;

    Difficulty(String label, int allowedErrors, int saveInterval, double snakeStartSpeed,
               double snakeAcceleration, double snakePeakSpeed, int snakeExtraTarget, String description) {
        this.label = label;
        this.allowedErrors = allowedErrors;
        this.saveInterval = saveInterval;
        this.snakeStartSpeed = snakeStartSpeed;
        this.snakeAcceleration = snakeAcceleration;
        this.snakePeakSpeed = snakePeakSpeed;
        this.snakeExtraTarget = snakeExtraTarget;
        this.description = description;
    }

    public String getLabel() { return label; }
    public int getAllowedErrors() { return allowedErrors; }
    public int getSaveInterval() { return saveInterval; }
    public double getSnakeStartSpeed() { return snakeStartSpeed; }
    public double getSnakeAcceleration() { return snakeAcceleration; }
    public double getSnakePeakSpeed() { return snakePeakSpeed; }
    public int getSnakeExtraTarget() { return snakeExtraTarget; }
    public String getDescription() { return description; }

    public Difficulty next() { return values()[(ordinal() + 1) % values().length]; }

    public Difficulty previous() { return values()[(ordinal() + values().length - 1) % values().length]; }
}