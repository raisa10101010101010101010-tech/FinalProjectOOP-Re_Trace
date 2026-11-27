package retrace.scene;

import retrace.asset.SpriteId;
import retrace.core.*;
import retrace.data.SaveData;
import retrace.minigame.MiniGameObserver;
import retrace.minigame.MiniGameStrategy;
import retrace.minigame.PlayerInput;
import retrace.puzzle.PuzzleFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class StoryScene extends Scene implements MiniGameObserver {
    private enum Phase { INTRO, PLAYING, FAILED, PAUSED, CHECKPOINT }

    private static final double CLEAR_DELAY = 1.5;

    private final SaveData data;
    private final int stageIndex;
    private final StoryScript.StageDefinition stage;
    private final ErrorOverlay overlay = new ErrorOverlay();
    private final MenuList pauseMenu = new MenuList("Resume", "Load State", "Quit to Menu");
    private final MenuList checkpointMenu = new MenuList("Save and continue", "Continue without saving");
    private MiniGameStrategy game;
    private Phase phase = Phase.INTRO;
    private boolean cleared, finishing;
    private double clearTimer;
    private String message = "";

    public StoryScene(GameContext ctx, SaveData data) {
        super(ctx);
        this.data = data;
        this.stageIndex = Math.max(0, Math.min(data.getStageIndex(), StoryScript.stageCount() - 1));
        this.stage = StoryScript.stage(stageIndex);
        data.setStageIndex(stageIndex);
        startGame();
    }

    private void startGame() {
        // Strategy pattern: the minigame logic is chosen from the stage definition
        game = stage.type().createStrategy(ctx, PuzzleFactory.create(stage.tier()), data.getDifficulty());
        game.addObserver(this);
        overlay.reset();
        cleared = false;
        finishing = false;
    }

    @Override
    public void onEnter() {
        ctx.getMusic().play(ctx.getAssets().getMinigameTrack(stage.type(), stage.arc()));
    }

    // ---- Observer callbacks ----

    @Override
    public void onPlayerInput(MiniGameStrategy source, PlayerInput input) {
        if (!input.valid()) ctx.getVhs().pulse();
    }

    @Override
    public void onError(MiniGameStrategy source, int errorCount) {
        data.addError();
        overlay.addError(StoryScript.errorText(errorCount));
    }

    @Override
    public void onWin(MiniGameStrategy source, int score) {
        cleared = true;
        clearTimer = CLEAR_DELAY;
    }

    @Override
    public void onFail(MiniGameStrategy source) {
        phase = Phase.FAILED;
    }

    // ---- flow ----

    @Override
    public void update(double dt) {
        if (phase != Phase.PLAYING) return;
        overlay.update(dt);
        if (cleared) {
            if (!finishing) {
                clearTimer -= dt;
                if (clearTimer <= 0) {
                    finishing = true;
                    finishStage();
                }
            }
            return;
        }
        data.addPlaySeconds(dt);
        game.update(dt);
    }

    private boolean isCheckpoint() {
        return (stageIndex + 1) % data.getDifficulty().getSaveInterval() == 0;
    }

    private void finishStage() {
        if (stageIndex >= StoryScript.stageCount() - 1) {
            ctx.getSaves().setStoryCompleted(true);
            ctx.getScenes().changeTo(new EndingScene(ctx));
        } else if (isCheckpoint()) {
            phase = Phase.CHECKPOINT;
            message = "";
        } else {
            advance();
        }
    }

    private void advance() {
        data.setStageIndex(stageIndex + 1);
        ctx.getScenes().changeTo(new StoryScene(ctx, data));
    }

    @Override
    public void keyPressed(int keyCode) {
        switch (phase) {
            case INTRO -> {
                if (Input.isConfirm(keyCode)) phase = Phase.PLAYING;
            }
            case PLAYING -> {
                if (keyCode == KeyEvent.VK_ESCAPE) {
                    phase = Phase.PAUSED;
                    message = "";
                } else if (!cleared) {
                    game.keyPressed(keyCode);
                }
            }
            case FAILED -> {
                if (Input.isConfirm(keyCode)) {
                    startGame();
                    phase = Phase.PLAYING;
                }
            }
            case PAUSED -> handlePauseKey(keyCode);
            case CHECKPOINT -> handleCheckpointKey(keyCode);
        }
    }

    private void handlePauseKey(int keyCode) {
        if (pauseMenu.navigate(keyCode)) return;
        if (keyCode == KeyEvent.VK_ESCAPE) {
            phase = Phase.PLAYING;
            return;
        }
        if (!Input.isConfirm(keyCode)) return;
        switch (pauseMenu.getSelected()) {
            case 0 -> phase = Phase.PLAYING;
            case 1 -> loadState();
            case 2 -> ctx.getScenes().changeTo(new MenuScene(ctx));
            default -> { }
        }
    }

    private void loadState() {
        SaveData loaded = ctx.getSaves().load(data.getSlot());
        if (loaded == null) message = "No saved state to load.";
        else ctx.getScenes().changeTo(new StoryScene(ctx, loaded));
    }

    private void handleCheckpointKey(int keyCode) {
        if (checkpointMenu.navigate(keyCode) || !Input.isConfirm(keyCode)) return;
        if (checkpointMenu.getSelected() == 0) {
            data.setStageIndex(stageIndex + 1);
            if (ctx.getSaves().save(data)) advance();
            else message = "Could not write the save file. Try again or continue without saving.";
        } else {
            advance();
        }
    }

    // ---- render ----

    @Override
    public void render(Graphics2D g) {
        g.setColor(stage.background());
        g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);

        if (phase == Phase.INTRO) {
            renderIntro(g);
            return;
        }
        HUD.draw(g, game, "Memory " + (stageIndex + 1) + "/" + StoryScript.stageCount()
                + "  -  " + stage.title() + "  -  " + data.getDifficulty().getLabel(), "ESC: pause");
        HUD.drawGame(g, game);
        overlay.render(g, phase == Phase.PLAYING);

        if (phase == Phase.FAILED) renderFailed(g);
        if (phase == Phase.PAUSED) renderPause(g);
        if (phase == Phase.CHECKPOINT) renderCheckpoint(g);
    }

    private void renderIntro(Graphics2D g) {
        ctx.getAssets().draw(g, SpriteId.MC_PORTRAIT, 60, 200, 140, 200);
        g.setColor(Config.TEXT);
        g.setFont(Config.FONT_BIG);
        g.drawString(stage.title() + " - " + stage.type().getLabel(), 240, 220);
        g.setFont(Config.FONT);
        GFX.drawWrapped(g, stage.introText(), 240, 270, 500, 30);
        g.setFont(Config.FONT_SMALL);
        GFX.drawCentered(g, "[SPACE] begin", Config.WIDTH / 2, 565);
    }

    private void renderFailed(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);
        g.setColor(Config.TEXT);
        g.setFont(Config.FONT_BIG);
        GFX.drawCentered(g, "The memory fades...", Config.WIDTH / 2, 230);
        g.setFont(Config.FONT);
        GFX.drawWrapped(g, StoryScript.FAIL_TEXT, 100, 280, 600, 30);
        GFX.drawCentered(g, "[SPACE] try again", Config.WIDTH / 2, 380);
    }

    private void renderPause(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);
        g.setColor(Config.TEXT);
        g.setFont(Config.FONT_BIG);
        GFX.drawCentered(g, "PAUSE", Config.WIDTH / 2, 170);
        pauseMenu.render(g, Config.WIDTH / 2, 250, 50);
        g.setFont(Config.FONT);
        g.setColor(Config.ACCENT);
        if (!message.isEmpty()) GFX.drawCentered(g, message, Config.WIDTH / 2, 480);
    }

    private void renderCheckpoint(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);
        g.setColor(Config.TEXT);
        g.setFont(Config.FONT_BIG);
        GFX.drawCentered(g, "Memory restored", Config.WIDTH / 2, 170);
        g.setFont(Config.FONT);
        GFX.drawCentered(g, "You can be assured now. Save your progress?", Config.WIDTH / 2, 215);
        checkpointMenu.render(g, Config.WIDTH / 2, 300, 55);
        g.setColor(Config.ACCENT);
        if (!message.isEmpty()) GFX.drawWrapped(g, message, 100, 440, 600, 26);
    }
}