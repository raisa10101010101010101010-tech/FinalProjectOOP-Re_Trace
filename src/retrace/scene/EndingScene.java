package retrace.scene;

import retrace.asset.SpriteId;
import retrace.core.*;

import java.awt.Graphics2D;

public class EndingScene extends Scene {
    public EndingScene(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void onEnter() {
        ctx.getMusic().play(ctx.getAssets().getEndingTrack());
    }

    @Override
    public void update(double dt) { }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Config.ACCENT);
        g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);
        ctx.getAssets().draw(g, SpriteId.MC_PORTRAIT, 60, 200, 140, 200);

        g.setColor(Config.DARK);
        g.setFont(Config.FONT_BIG);
        g.drawString("The End", 240, 220);
        g.setFont(Config.FONT);
        GFX.drawWrapped(g, StoryScript.ENDING_TEXT, 240, 270, 500, 30);
        g.drawString("Game Mode is now unlocked!", 240, 400);
        g.setFont(Config.FONT_SMALL);
        GFX.drawCentered(g, "[SPACE] back to menu", Config.WIDTH / 2, 565);
    }

    @Override
    public void keyPressed(int keyCode) {
        if (Input.isConfirm(keyCode)) ctx.getScenes().changeTo(new MenuScene(ctx));
    }
}