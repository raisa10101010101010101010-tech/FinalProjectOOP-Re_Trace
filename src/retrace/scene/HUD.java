package retrace.scene;

import retrace.core.Config;
import retrace.core.GFX;
import retrace.minigame.MiniGameStrategy;

import java.awt.Graphics2D;

final class HUD {
    static final int AREA_X = 40;
    static final int AREA_Y = 80;
    static final int AREA_W = 720;
    static final int AREA_H = 450;
    private HUD() { }

    static void draw(Graphics2D g, MiniGameStrategy game, String subtitle, String escHint) {
        g.setColor(Config.TEXT);
        g.setFont(Config.FONT_BIG);
        g.drawString(game.getTitle(), 40, 42);
        g.setFont(Config.FONT_SMALL);
        g.drawString(subtitle, 40, 64);

        g.setFont(Config.FONT);
        GFX.drawRight(g, "Errors: " + game.getErrors() + "/" + game.getDifficulty().getAllowedErrors(), 760, 40);
        g.setFont(Config.FONT_SMALL);
        GFX.drawRight(g, game.getStatus() + "   " + GFX.formatTime(game.getElapsed()), 760, 64);
        GFX.drawCentered(g, game.getControlsHint() + "   |   " + escHint, Config.WIDTH / 2, 565);
    }

    static void drawGame(Graphics2D g, MiniGameStrategy game) {
        game.render(g, AREA_X, AREA_Y, AREA_W, AREA_H);
    }
}