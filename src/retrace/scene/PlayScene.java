package retrace.scene;

import retrace.core.*;
import retrace.minigame.MiniGameObserver;
import retrace.minigame.MiniGameStrategy;
import retrace.minigame.MiniGameType;
import retrace.minigame.PlayerInput;
import retrace.puzzle.PuzzleFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/** Plays one Game Mode stage (no story overlay). The score goes to the score board. */
public class PlayScene extends Scene implements MiniGameObserver {
    private final MiniGameType type;
    private final int stageIndex;
    private final int tier;
    private final Difficulty difficulty;
    private final MiniGameStrategy game;
    private boolean finished, won, newBest;
    private int score;

    public PlayScene(GameContext ctx, MiniGameType type, int stageIndex, Difficulty difficulty) {
        super(ctx);
        this.type = type;
        this.stageIndex = stageIndex;
        this.difficulty = difficulty;
        this.tier = StoryScript.tierOf(type, stageIndex);
        this.game = type.createStrategy(ctx, PuzzleFactory.create(tier), difficulty);
        this.game.addObserver(this);
    }

    @Override
    public void onEnter() {
        ctx.getMusic().play(ctx.getAssets().getMinigameTrack(type, Math.min(3, tier)));
    }

    @Override
    public void onPlayerInput(MiniGameStrategy source, PlayerInput input) {
        if (!input.valid()) ctx.getVhs().pulse();
    }

    @Override
    public void onWin(MiniGameStrategy source, int score) {
        finished = true;
        won = true;
        this.score = score;
        newBest = ctx.getScoreBoard().submit(type, stageIndex, score);
    }

    @Override
    public void onFail(MiniGameStrategy source) {
        finished = true;
        won = false;
    }

    @Override
    public void update(double dt) {
        if (!finished) game.update(dt);
    }

    @Override
    public void keyPressed(int keyCode) {
        if (finished) {
            if (Input.isConfirm(keyCode)) ctx.getScenes().changeTo(new GameModeScene(ctx));
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            ctx.getScenes().changeTo(new GameModeScene(ctx));
        } else {
            game.keyPressed(keyCode);
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Config.DARK);
        g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);
        HUD.draw(g, game, "Game Mode  -  Stage " + (stageIndex + 1) + "  -  " + difficulty.getLabel(), "ESC: exit");
        HUD.drawGame(g, game);

        if (!finished) return;
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);
        g.setColor(Config.TEXT);
        g.setFont(Config.FONT_TITLE);
        GFX.drawCentered(g, won ? "Cleared!" : "Failed", Config.WIDTH / 2, 230);
        g.setFont(Config.FONT_BIG);
        if (won) {
            GFX.drawCentered(g, "Score: " + score, Config.WIDTH / 2, 290);
            if (newBest) GFX.drawCentered(g, "NEW BEST!", Config.WIDTH / 2, 335);
        } else {
            GFX.drawCentered(g, "Too many errors", Config.WIDTH / 2, 290);
        }
        g.setFont(Config.FONT);
        GFX.drawCentered(g, "[SPACE] back", Config.WIDTH / 2, 420);
    }
}