package retrace.scene;

import retrace.asset.SpriteId;
import retrace.core.*;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class MenuScene extends Scene {
    // Intro timeline (seconds)
    private static final double B1_START = 0.2, B1_OPEN = 0.35, B1_CLOSE = 0.3; // first, quick blink
    private static final double B2_START = 1.05, B2_OPEN = 0.8;                  // second blink opens fully
    private static final double UNBLUR_END = 2.4;
    private static final double HOLD_END = UNBLUR_END + 2.0;                     // title rests centered 2 s
    private static final double MOVE_END = HOLD_END + 0.7;                       // title moves up

    private final MenuList menu = new MenuList("Story Mode", "Game Mode", "Exit");
    private double time;
    private String message = "";

    public MenuScene(GameContext ctx) {
        this(ctx, false);
    }

    public MenuScene(GameContext ctx, boolean playIntro) {
        super(ctx);
        time = playIntro ? 0 : MOVE_END + 10;
    }

    @Override
    public void onEnter() {
        ctx.getMusic().play(ctx.getAssets().getMenuTrack());
        menu.setEnabled(1, Config.DEBUG_UNLOCK_GAME_MODE || ctx.getSaves().isStoryCompleted());
    }

    @Override
    public void update(double dt) {
        time += dt;
    }

    @Override
    public void keyPressed(int keyCode) {
        if (time < MOVE_END) {
            if (Input.isConfirm(keyCode)) time = MOVE_END + 0.5; // skip intro
            return;
        }
        if (menu.navigate(keyCode)) {
            message = "";
            return;
        }
        if (!Input.isConfirm(keyCode)) return;
        switch (menu.getSelected()) {
            case 0 -> ctx.getScenes().changeTo(new StoryMenuScene(ctx));
            case 1 -> {
                if (menu.isSelectedEnabled()) ctx.getScenes().changeTo(new GameModeScene(ctx));
                else message = "Finish Story Mode to unlock Game Mode.";
            }
            case 2 -> System.exit(0);
            default -> { }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Config.MENU_BG);
        g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);

        double move = ease((time - HOLD_END) / 0.7);
        int titleY = (int) (310 + (150 - 310) * move);
        drawTitle(g, titleY, 26 * (1 - ease((time - B2_START) / (UNBLUR_END - B2_START))));

        double ui = clamp01((time - HOLD_END - 0.2) / 0.6);
        if (ui > 0) {
            Composite old = g.getComposite();
            g.setComposite(AlphaComposite.SrcOver.derive((float) ui));
            g.setColor(Config.ACCENT);
            g.setFont(Config.FONT);
            GFX.drawCentered(g, "A short puzzling plot-driven game", Config.WIDTH / 2, 190);
            ctx.getAssets().draw(g, SpriteId.MC_PORTRAIT, 70, 240, 120, 170);
            menu.render(g, Config.WIDTH / 2, 310, 55);
            g.setFont(Config.FONT_SMALL);
            g.setColor(Config.TEXT);
            if (!message.isEmpty()) GFX.drawCentered(g, message, Config.WIDTH / 2, 500);
            GFX.drawCentered(g, "Up/Down: select   |   SPACE: confirm   |   F1: VHS   |   F2: mute",
                    Config.WIDTH / 2, 565);
            g.setComposite(old);
        }

        drawLids(g, aperture());
        double fade = 1 - clamp01(time / 0.5); // fade in from black
        if (fade > 0) {
            g.setColor(new Color(0, 0, 0, (int) (fade * 255)));
            g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);
        }
    }

    /** Draws the title; a positive radius blurs it by stacking offset translucent copies. */
    private void drawTitle(Graphics2D g, int baseline, double radius) {
        g.setFont(Config.FONT_TITLE);
        g.setColor(Color.WHITE);
        if (radius < 0.5) {
            GFX.drawCentered(g, "Re:Trace", Config.WIDTH / 2, baseline);
            return;
        }
        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.SrcOver.derive(0.1f));
        for (int ring = 1; ring <= 3; ring++) {
            double rr = radius * ring / 3.0;
            for (int i = 0; i < 8; i++) {
                double a = i * Math.PI / 4;
                GFX.drawCentered(g, "Re:Trace", Config.WIDTH / 2 + (int) (Math.cos(a) * rr),
                        baseline + (int) (Math.sin(a) * rr));
            }
        }
        g.setComposite(old);
    }

    /** Eye opening 0 (closed) to 1 (fully open). */
    private double aperture() {
        if (time < B1_START) return 0;
        if (time < B1_START + B1_OPEN) return 0.16 * ease((time - B1_START) / B1_OPEN);
        if (time < B1_START + B1_OPEN + B1_CLOSE) {
            return 0.16 * (1 - ease((time - B1_START - B1_OPEN) / B1_CLOSE));
        }
        if (time < B2_START) return 0;
        return ease((time - B2_START) / B2_OPEN);
    }

    /** First-person eyelids: two black lids with curved edges. */
    private void drawLids(Graphics2D g, double o) {
        if (o >= 1) return;
        double w = Config.WIDTH, h = Config.HEIGHT;
        double edge = h / 2 * (1 - o);          // lid edge at the screen sides
        double middle = h / 2 - o * h / 0.7;    // lid edge at the center
        Path2D.Double lid = new Path2D.Double();
        lid.moveTo(0, 0);
        lid.lineTo(w, 0);
        lid.lineTo(w, edge);
        lid.quadTo(w / 2, 2 * middle - edge, 0, edge);
        lid.closePath();
        g.setColor(Color.BLACK);
        g.fill(lid);
        AffineTransform old = g.getTransform();
        g.translate(0, h);
        g.scale(1, -1);
        g.fill(lid);
        g.setTransform(old);
    }

    private static double clamp01(double x) { return Math.max(0, Math.min(1, x)); }

    private static double ease(double x) {
        x = clamp01(x);
        return x * x * (3 - 2 * x);
    }
}