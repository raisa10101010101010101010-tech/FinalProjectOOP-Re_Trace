package retrace.scene;

import retrace.core.Config;
import retrace.core.Difficulty;
import retrace.core.GameContext;
import retrace.core.GFX;
import retrace.core.Input;
import retrace.core.Scene;
import retrace.minigame.MiniGame;
import retrace.minigame.MiniGameType;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class PlayScene extends Scene implements MiniGame.Listener {
    private final MiniGameType type;
    private final Difficulty difficulty;
    private final MiniGame game;
    private boolean finished;
    private boolean won;
    private int score;
    private int rank;

    public PlayScene(GameContext ctx, MiniGameType type, Difficulty difficulty) {
        super(ctx);
        this.type = type;
        this.difficulty = difficulty;
        this.game = type.create(ctx, difficulty);
        this.game.setListener(this);
    }

    @Override
    public void onError(MiniGame source, int errorCount) { }

    @Override
    public void onWin(MiniGame source, int score) {
        finished = true;
        won = true;
        this.score = score;
        this.rank = ctx.getScoreBoard().submit(type, score);
    }

    @Override
    public void onFail(MiniGame source) {
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
            return;
        }
        if (keyCode == KeyEvent.VK_ESCAPE) ctx.getScenes().changeTo(new GameModeScene(ctx));
        else game.keyPressed(keyCode);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Config.DARK);
        g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);
        HUD.draw(g, game, "Game Mode  -  " + difficulty.getLabel(), "ESC: keluar");
        HUD.drawGame(g, game);

        if (!finished) return;
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);
        g.setColor(Config.TEXT);
        g.setFont(Config.FONT_TITLE);
        GFX.drawCentered(g, won ? "Selesai!" : "Gagal", Config.WIDTH / 2, 230);
        g.setFont(Config.FONT_BIG);
        if (won) {
            GFX.drawCentered(g, "Skor: " + score, Config.WIDTH / 2, 290);
            GFX.drawCentered(g, rank == 1 ? "REKOR BARU!"
                    : rank > 0 ? "Peringkat #" + rank : "Tidak masuk 5 besar", Config.WIDTH / 2, 335);
        } else {
            GFX.drawCentered(g, "Terlalu banyak kesalahan", Config.WIDTH / 2, 290);
        }
        g.setFont(Config.FONT);
        GFX.drawCentered(g, "[SPACE] kembali", Config.WIDTH / 2, 420);
    }
}