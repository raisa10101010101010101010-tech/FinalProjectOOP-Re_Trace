package retrace.scene;

import retrace.core.Config;
import retrace.core.GameContext;
import retrace.core.GFX;
import retrace.core.Input;
import retrace.core.MenuList;
import retrace.core.Scene;
import retrace.data.SaveData;
import retrace.minigame.MiniGame;
import retrace.sprite.SpriteId;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class StoryScene extends Scene implements MiniGame.Listener {
    private enum Phase { INTRO, PLAYING, FAILED, PAUSED }

    private static final double CLEAR_DELAY = 1.5;

    private final SaveData data;
    private final int stageIndex;
    private final StoryScript.StageDefinition stage;
    private final ErrorOverlay overlay = new ErrorOverlay();
    private final MenuList pauseMenu = new MenuList("Resume", "Save State", "Load State", "Quit to Menu");
    private MiniGame game;
    private Phase phase = Phase.INTRO;
    private boolean cleared;
    private boolean finishing;
    private double clearTimer;
    private String pauseMessage = "";

    public StoryScene(GameContext ctx, SaveData data) {
        super(ctx);
        this.data = data;
        this.stageIndex = Math.max(0, Math.min(data.getStageIndex(), StoryScript.stageCount() - 1));
        this.stage = StoryScript.stage(stageIndex);
        data.setStageIndex(stageIndex);
        startGame();
    }

    private void startGame() {
        game = stage.type().create(ctx, data.getDifficulty());
        game.setListener(this);
        overlay.reset();
        cleared = false;
        finishing = false;
    }

    // ---- MiniGame.Listener ----

    @Override
    public void onError(MiniGame source, int errorCount) {
        data.addError();
        overlay.addError(StoryScript.errorText(errorCount));
    }

    @Override
    public void onWin(MiniGame source, int score) {
        cleared = true;
        clearTimer = CLEAR_DELAY;
    }

    @Override
    public void onFail(MiniGame source) {
        phase = Phase.FAILED;
    }

    // ---- alur scene ----

    @Override
    public void update(double dt) {
        if (phase == Phase.PLAYING) overlay.update(dt);
        if (phase != Phase.PLAYING) return;
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

    private void finishStage() {
        boolean last = stageIndex >= StoryScript.stageCount() - 1;
        if (last) {
            ctx.getSaves().setStoryCompleted(true);
            ctx.getScenes().changeTo(new EndingScene(ctx));
        } else {
            data.setStageIndex(stageIndex + 1);
            ctx.getSaves().save(data); // auto-save setiap tahap selesai
            ctx.getScenes().changeTo(new StoryScene(ctx, data));
        }
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
                    pauseMessage = "";
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
            case 1 -> pauseMessage = ctx.getSaves().save(data)
                    ? "State tersimpan di Slot " + data.getSlot() + "."
                    : "Gagal menyimpan state.";
            case 2 -> loadState();
            case 3 -> ctx.getScenes().changeTo(new MenuScene(ctx));
            default -> { }
        }
    }

    private void loadState() {
        SaveData loaded = ctx.getSaves().load(data.getSlot());
        if (loaded == null) {
            pauseMessage = "Tidak ada state tersimpan.";
            return;
        }
        ctx.getScenes().changeTo(new StoryScene(ctx, loaded));
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
        HUD.draw(g, game, "Ingatan " + (stageIndex + 1) + "/" + StoryScript.stageCount()
                + "  -  " + data.getDifficulty().getLabel(), "ESC: jeda");
        HUD.drawGame(g, game);
        overlay.render(g, phase != Phase.FAILED);

        if (phase == Phase.FAILED) renderFailed(g);
        if (phase == Phase.PAUSED) renderPause(g);
    }

    private void renderIntro(Graphics2D g) {
        ctx.getSprites().draw(g, SpriteId.MC_PORTRAIT, 60, 200, 140, 200);
        g.setColor(Config.TEXT);
        g.setFont(Config.FONT_BIG);
        g.drawString(stage.title() + " - " + stage.type().getLabel(), 240, 220);
        g.setFont(Config.FONT);
        GFX.drawWrapped(g, stage.introText(), 240, 270, 500, 30);
        g.setFont(Config.FONT_SMALL);
        GFX.drawCentered(g, "[SPACE] mulai", Config.WIDTH / 2, 565);
    }

    private void renderFailed(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);
        g.setColor(Config.TEXT);
        g.setFont(Config.FONT_BIG);
        GFX.drawCentered(g, "Ingatan memudar...", Config.WIDTH / 2, 230);
        g.setFont(Config.FONT);
        GFX.drawWrapped(g, StoryScript.FAIL_TEXT, 100, 280, 600, 30);
        GFX.drawCentered(g, "[SPACE] coba lagi", Config.WIDTH / 2, 380);
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
        if (!pauseMessage.isEmpty()) GFX.drawCentered(g, pauseMessage, Config.WIDTH / 2, 480);
    }
}