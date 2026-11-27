package retrace.core;

import retrace.asset.AssetManager;
import retrace.audio.MusicPlayer;
import retrace.data.SaveManager;
import retrace.data.ScoreBoard;
import retrace.fx.VhsEffect;

/** Shared services, injected into every scene and minigame. */
public class GameContext {
    private final Input input = new Input();
    private final SceneManager scenes = new SceneManager();
    private final AssetManager assets = AssetManager.getInstance(); // Singleton
    private final SaveManager saves = new SaveManager();
    private final ScoreBoard scoreBoard = new ScoreBoard();
    private final MusicPlayer music = new MusicPlayer();
    private final VhsEffect vhs = new VhsEffect();

    public Input getInput() { return input; }
    public SceneManager getScenes() { return scenes; }
    public AssetManager getAssets() { return assets; }
    public SaveManager getSaves() { return saves; }
    public ScoreBoard getScoreBoard() { return scoreBoard; }
    public MusicPlayer getMusic() { return music; }
    public VhsEffect getVhs() { return vhs; }
}