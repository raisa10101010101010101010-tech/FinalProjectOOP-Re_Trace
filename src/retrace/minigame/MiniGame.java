package retrace.minigame;

import retrace.core.Difficulty;
import retrace.core.GameContext;

import java.awt.Graphics2D;

/** Kelas dasar semua minigame: menghitung kesalahan, waktu, dan status menang/gagal. */
public abstract class MiniGame {
    public enum State { PLAYING, WON, FAILED }

    public interface Listener {
        void onError(MiniGame source, int errorCount);

        void onWin(MiniGame source, int score);

        void onFail(MiniGame source);
    }

    protected final GameContext ctx;
    private final MiniGameType type;
    private final Difficulty difficulty;
    private Listener listener;
    private State state = State.PLAYING;
    private int errors;
    private double elapsed;

    protected MiniGame(GameContext ctx, MiniGameType type, Difficulty difficulty) {
        this.ctx = ctx;
        this.type = type;
        this.difficulty = difficulty;
    }

    public void setListener(Listener listener) { this.listener = listener; }

    public final void update(double dt) {
        if (state != State.PLAYING) return;
        if (isClockRunning()) elapsed += dt;
        onUpdate(dt);
    }

    public final void keyPressed(int keyCode) {
        if (state == State.PLAYING) onKeyPressed(keyCode);
    }

    protected void onUpdate(double dt) { }

    protected abstract void onKeyPressed(int keyCode);

    protected boolean isClockRunning() { return true; }

    protected abstract int computeScore();

    public abstract void render(Graphics2D g, int x, int y, int w, int h);

    public abstract String getStatus();

    public abstract String getControlsHint();

    /** Kesalahan ke-N. Gagal bila jumlah kesalahan melebihi batas kesulitan. */
    protected final void registerError() {
        if (state != State.PLAYING) return;
        errors++;
        boolean failed = errors > difficulty.getAllowedErrors();
        if (failed) state = State.FAILED;
        if (listener != null) {
            listener.onError(this, errors);
            if (failed) listener.onFail(this);
        }
    }

    protected final void win() {
        if (state != State.PLAYING) return;
        state = State.WON;
        if (listener != null) listener.onWin(this, computeScore());
    }

    public String getTitle() { return type.getLabel(); }

    public MiniGameType getType() { return type; }

    public Difficulty getDifficulty() { return difficulty; }

    public State getState() { return state; }

    public int getErrors() { return errors; }

    public double getElapsed() { return elapsed; }
}