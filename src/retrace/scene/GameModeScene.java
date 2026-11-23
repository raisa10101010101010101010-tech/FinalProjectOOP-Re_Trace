package retrace.scene;

import retrace.core.Config;
import retrace.core.Difficulty;
import retrace.core.GameContext;
import retrace.core.GFX;
import retrace.core.Input;
import retrace.core.MenuList;
import retrace.core.Scene;
import retrace.minigame.MiniGameType;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.List;

public class GameModeScene extends Scene {
    private final MiniGameType[] types = MiniGameType.values();
    private final MenuList menu;
    private Difficulty difficulty = Difficulty.NORMAL;

    public GameModeScene(GameContext ctx) {
        super(ctx);
        String[] labels = new String[types.length + 1];
        for (int i = 0; i < types.length; i++) labels[i] = types[i].getLabel();
        labels[types.length] = "Back";
        menu = new MenuList(labels);
    }

    @Override
    public void update(double dt) { }

    @Override
    public void keyPressed(int keyCode) {
        if (menu.navigate(keyCode)) return;
        if (keyCode == KeyEvent.VK_LEFT) {
            difficulty = difficulty.previous();
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            difficulty = difficulty.next();
        } else if (Input.isBack(keyCode)) {
            ctx.getScenes().changeTo(new MenuScene(ctx));
        } else if (Input.isConfirm(keyCode)) {
            int selected = menu.getSelected();
            if (selected == types.length) ctx.getScenes().changeTo(new MenuScene(ctx));
            else ctx.getScenes().changeTo(new PlayScene(ctx, types[selected], difficulty));
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(new Color(178, 106, 68));
        g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(Config.FONT_TITLE);
        GFX.drawCentered(g, "Game Mode", Config.WIDTH / 2, 100);

        menu.render(g, 250, 220, 55);

        g.setFont(Config.FONT);
        g.setColor(Config.ACCENT);
        GFX.drawCentered(g, "< " + difficulty.getLabel() + " (boleh salah "
                + difficulty.getAllowedErrors() + "x) >", 250, 470);

        int selected = menu.getSelected();
        if (selected < types.length) renderScoreBoard(g, types[selected]);

        g.setFont(Config.FONT_SMALL);
        g.setColor(Config.TEXT);
        GFX.drawCentered(g, "Up/Down: pilih   Left/Right: kesulitan   SPACE: main   SHIFT: kembali",
                Config.WIDTH / 2, 565);
    }

    private void renderScoreBoard(Graphics2D g, MiniGameType type) {
        g.setColor(new Color(0, 0, 0, 90));
        g.fillRect(470, 170, 290, 260);
        g.setColor(Config.TEXT);
        g.setFont(Config.FONT);
        g.drawString("Score Board", 490, 205);
        g.setFont(Config.FONT_SMALL);
        g.drawString(type.getLabel(), 490, 228);

        List<Integer> list = ctx.getScoreBoard().getScores(type);
        g.setFont(Config.FONT_BIG);
        for (int i = 0; i < 5; i++) {
            String value = i < list.size() ? String.valueOf(list.get(i)) : "-";
            g.drawString((i + 1) + ".  " + value, 490, 275 + i * 36);
        }
    }
}