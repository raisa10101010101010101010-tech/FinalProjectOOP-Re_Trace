package retrace.core;

import java.awt.Color;
import java.awt.Graphics2D;

/** Mengelola scene aktif dan transisi fade out / fade in antar scene. */
public class SceneManager {
    private enum Phase { NONE, FADE_OUT, FADE_IN }

    private static final double FADE_SECONDS = 0.6;

    private Scene current;
    private Scene pending;
    private Phase phase = Phase.NONE;
    private double fade;

    public void setImmediate(Scene scene) {
        current = scene;
        current.onEnter();
    }

    /** Layar memudar ke hitam, scene diganti, lalu layar muncul kembali. */
    public void changeTo(Scene next) {
        if (phase != Phase.NONE) return;
        pending = next;
        phase = Phase.FADE_OUT;
        fade = 0;
    }

    public void update(double dt) {
        switch (phase) {
            case FADE_OUT -> {
                fade += dt / FADE_SECONDS;
                if (fade >= 1) {
                    fade = 1;
                    current.onExit();
                    current = pending;
                    pending = null;
                    current.onEnter();
                    phase = Phase.FADE_IN;
                }
            }
            case FADE_IN -> {
                fade -= dt / FADE_SECONDS;
                if (fade <= 0) {
                    fade = 0;
                    phase = Phase.NONE;
                }
            }
            default -> { }
        }
        if (current != null) current.update(dt);
    }

    public void render(Graphics2D g) {
        if (current != null) current.render(g);
        if (fade > 0) {
            g.setColor(new Color(0, 0, 0, (int) (Math.min(1.0, fade) * 255)));
            g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);
        }
    }

    public void keyPressed(int keyCode) {
        if (phase == Phase.NONE && current != null) current.keyPressed(keyCode);
    }
}