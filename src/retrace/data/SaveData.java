package retrace.data;

import retrace.core.Difficulty;

/** Isi satu file save Story Mode. */
public class SaveData {
    private final int slot;
    private final Difficulty difficulty;
    private int stageIndex;
    private int totalErrors;
    private double playSeconds;
    private String savedAt = "-";

    public SaveData(int slot, Difficulty difficulty) {
        this.slot = slot;
        this.difficulty = difficulty;
    }

    public int getSlot() { return slot; }

    public Difficulty getDifficulty() { return difficulty; }

    public int getStageIndex() { return stageIndex; }

    public void setStageIndex(int stageIndex) { this.stageIndex = stageIndex; }

    public int getTotalErrors() { return totalErrors; }

    public void setTotalErrors(int totalErrors) { this.totalErrors = totalErrors; }

    public void addError() { totalErrors++; }

    public double getPlaySeconds() { return playSeconds; }

    public void setPlaySeconds(double playSeconds) { this.playSeconds = playSeconds; }

    public void addPlaySeconds(double seconds) { playSeconds += seconds; }

    public String getSavedAt() { return savedAt; }

    public void setSavedAt(String savedAt) { this.savedAt = savedAt; }
}