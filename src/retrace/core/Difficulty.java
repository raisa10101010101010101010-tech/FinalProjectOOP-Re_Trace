package retrace.core;

/** Tingkat kesulitan = jumlah kesalahan yang boleh dibuat dalam satu minigame. */
public enum Difficulty {
    EASY("Easy", 5),
    NORMAL("Normal", 3),
    HARD("Hard", 1);

    private final String label;
    private final int allowedErrors;

    Difficulty(String label, int allowedErrors) {
        this.label = label;
        this.allowedErrors = allowedErrors;
    }

    public String getLabel() { return label; }

    public int getAllowedErrors() { return allowedErrors; }

    public Difficulty next() {
        return values()[(ordinal() + 1) % values().length];
    }

    public Difficulty previous() {
        return values()[(ordinal() + values().length - 1) % values().length];
    }
}