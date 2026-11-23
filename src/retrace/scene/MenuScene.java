package retrace.scene;

import retrace.core.Config;
import retrace.core.GameContext;
import retrace.core.GFX;
import retrace.core.Input;
import retrace.core.MenuList;
import retrace.core.Scene;
import retrace.sprite.SpriteId;

import java.awt.Color;
import java.awt.Graphics2D;

public class MenuScene extends Scene {
    private final MenuList menu = new MenuList("Story Mode", "Game Mode", "Quit");
    private String message = "";

    public MenuScene(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void onEnter() {
        menu.setEnabled(1, Config.DEBUG_UNLOCK_GAME_MODE || ctx.getSaves().isStoryCompleted());
    }

    @Override
    public void update(double dt) { }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Config.MENU_BG);
        g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(Config.FONT_TITLE);
        GFX.drawCentered(g, "Re:Trace", Config.WIDTH / 2, 150);
        g.setColor(Config.ACCENT);
        g.setFont(Config.FONT);
        GFX.drawCentered(g, "A short puzzling plot-driven game", Config.WIDTH / 2, 190);

        ctx.getSprites().draw(g, SpriteId.MC_PORTRAIT, 70, 230, 120, 170);
        menu.render(g, Config.WIDTH / 2, 300, 55);

        g.setFont(Config.FONT_SMALL);
        g.setColor(Config.TEXT);
        if (!message.isEmpty()) GFX.drawCentered(g, message, Config.WIDTH / 2, 500);
        GFX.drawCentered(g, "Up/Down: pilih   |   SPACE: konfirmasi", Config.WIDTH / 2, 565);
    }

    @Override
    public void keyPressed(int keyCode) {
        if (menu.navigate(keyCode)) {
            message = "";
            return;
        }
        if (!Input.isConfirm(keyCode)) return;
        switch (menu.getSelected()) {
            case 0 -> ctx.getScenes().changeTo(new StoryMenuScene(ctx));
            case 1 -> {
                if (menu.isSelectedEnabled()) ctx.getScenes().changeTo(new GameModeScene(ctx));
                else message = "Selesaikan Story Mode untuk membuka Game Mode.";
            }
            case 2 -> System.exit(0);
            default -> { }
        }
    }
}