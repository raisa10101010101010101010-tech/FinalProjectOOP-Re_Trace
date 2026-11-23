package retrace.core;

import retrace.data.SaveManager;
import retrace.data.ScoreBoard;
import retrace.sprite.SpriteRegistry;

/** Menyimpan layanan bersama; di-inject ke semua scene dan minigame. */
public class GameContext {
    private final Input input = new Input();
    private final SceneManager scenes = new SceneManager();
    private final SpriteRegistry sprites = new SpriteRegistry();
    private final SaveManager saves = new SaveManager();
    private final ScoreBoard scoreBoard = new ScoreBoard();

    public Input getInput() { return input; }

    public SceneManager getScenes() { return scenes; }

    public SpriteRegistry getSprites() { return sprites; }

    public SaveManager getSaves() { return saves; }

    public ScoreBoard getScoreBoard() { return scoreBoard; }
}