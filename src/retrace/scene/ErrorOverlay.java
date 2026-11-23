package retrace.scene;

import retrace.core.Config;
import retrace.core.GFX;

import java.awt.Color;
import java.awt.Graphics2D;

final class ErrorOverlay {
    /** Alpha 26 dari 255 = sekitar 10% opak, atau 90% transparan. */
    private static final Color LAYER = new Color(0, 0, 0, 26);
    private static final double TEXT_SECONDS = 3.5;

    private int layers;
    private String text = "";
    private double textTimer;

    void addError(String message) {
        layers++;
        text = message;
        textTimer = TEXT_SECONDS;
    }

    void reset() {
        layers = 0;
        text = "";
        textTimer = 0;
    }

    void update(double dt) {
        if (textTimer > 0) textTimer -= dt;
    }

    void render(Graphics2D g, boolean showText) {
        g.setColor(LAYER);
        for (int i = 0; i < layers; i++) g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);

        if (showText && textTimer > 0) {
            double alpha = Math.min(1.0, textTimer / 0.6);
            g.setColor(new Color(0, 0, 0, (int) (190 * alpha)));
            g.fillRect(40, Config.HEIGHT - 190, Config.WIDTH - 80, 130);
            g.setColor(new Color(255, 240, 220, (int) (255 * alpha)));
            g.setFont(Config.FONT);
            GFX.drawWrapped(g, text, 60, Config.HEIGHT - 155, Config.WIDTH - 120, 28);
        }
    }
}