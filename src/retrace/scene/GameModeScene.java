package retrace.scene;

import retrace.core.*;
import retrace.minigame.MiniGameType;
import retrace.puzzle.Puzzle;
import retrace.puzzle.PuzzleFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class GameModeScene extends Scene {
    private final MiniGameType[] types = MiniGameType.values();
    private final MenuList typeMenu;
    private MenuList stageMenu;
    private boolean stageFocus;
    private Difficulty difficulty = Difficulty.NORMAL;

    public GameModeScene(GameContext ctx) {
        super(ctx);
        String[] labels = new String[types.length + 1];
        for (int i = 0; i < types.length; i++) labels[i] = types[i].getLabel();
        labels[types.length] = "Back";
        typeMenu = new MenuList(labels);
        buildStageMenu();
    }

    @Override
    public void onEnter() {
        ctx.getMusic().play(ctx.getAssets().getMenuTrack());
    }

    private void buildStageMenu() {
        int selected = typeMenu.getSelected();
        if (selected >= types.length) {
            stageMenu = null;
            return;
        }
        MiniGameType type = types[selected];
        int count = StoryScript.stageCountOf(type);
        String[] labels = new String[count];
        for (int i = 0; i < count; i++) {
            Puzzle puzzle = PuzzleFactory.create(StoryScript.tierOf(type, i));
            int best = ctx.getScoreBoard().getBest(type, i);
            labels[i] = "Stage " + (i + 1) + "  " + puzzle.describe(type) + "  Best: " + (best > 0 ? best : "-");
        }
        stageMenu = new MenuList(labels);
    }

    @Override
    public void update(double dt) { }

    @Override
    public void keyPressed(int keyCode) {
        if (keyCode == KeyEvent.VK_ESCAPE) {
            ctx.getScenes().changeTo(new MenuScene(ctx));
            return;
        }
        if (keyCode == KeyEvent.VK_SHIFT) {
            difficulty = difficulty.next();
            return;
        }
        if (stageFocus) {
            if (stageMenu.navigate(keyCode)) return;
            if (keyCode == KeyEvent.VK_LEFT) {
                stageFocus = false;
            } else if (Input.isConfirm(keyCode)) {
                ctx.getScenes().changeTo(new PlayScene(ctx, types[typeMenu.getSelected()],
                        stageMenu.getSelected(), difficulty));
            }
        } else {
            if (typeMenu.navigate(keyCode)) {
                buildStageMenu();
            } else if (keyCode == KeyEvent.VK_RIGHT && stageMenu != null) {
                stageFocus = true;
            } else if (Input.isConfirm(keyCode)) {
                if (stageMenu == null) ctx.getScenes().changeTo(new MenuScene(ctx));
                else stageFocus = true;
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(new Color(178, 106, 68));
        g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(Config.FONT_TITLE);
        GFX.drawCentered(g, "Game Mode", Config.WIDTH / 2, 90);

        g.setColor(new Color(0, 0, 0, 70));
        g.fillRect(30, 130, 280, 340);
        g.fillRect(330, 130, 440, 340);

        typeMenu.render(g, 170, 200, 60, Config.FONT_BIG, !stageFocus);
        g.setFont(Config.FONT);
        g.setColor(Config.TEXT);
        g.drawString("Stages", 350, 160);
        if (stageMenu != null) stageMenu.render(g, 550, 210, 48, Config.FONT_LIST, stageFocus);

        g.setFont(Config.FONT);
        g.setColor(Config.ACCENT);
        GFX.drawCentered(g, "Difficulty: " + difficulty.getLabel() + " (SHIFT to change)", Config.WIDTH / 2, 515);

        g.setFont(Config.FONT_SMALL);
        g.setColor(Config.TEXT);
        GFX.drawCentered(g, "Up/Down: select   Right/Left: switch list   SPACE: play   ESC: back",
                Config.WIDTH / 2, 565);
    }
}