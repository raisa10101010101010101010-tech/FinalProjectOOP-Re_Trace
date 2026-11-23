package retrace.scene;

import retrace.core.Config;
import retrace.core.Difficulty;
import retrace.core.GameContext;
import retrace.core.GFX;
import retrace.core.Input;
import retrace.core.MenuList;
import retrace.core.Scene;
import retrace.data.SaveData;

import java.awt.Color;
import java.awt.Graphics2D;

public class StoryMenuScene extends Scene {
    private enum Step { MODE, SLOT, DIFFICULTY }

    private final MenuList modeMenu = new MenuList("New File", "Load State", "Back");
    private final MenuList difficultyMenu;
    private MenuList slotMenu;
    private Step step = Step.MODE;
    private boolean creatingNew;
    private int chosenSlot;
    private String message = "";

    public StoryMenuScene(GameContext ctx) {
        super(ctx);
        Difficulty[] all = Difficulty.values();
        String[] labels = new String[all.length + 1];
        for (int i = 0; i < all.length; i++) {
            labels[i] = all[i].getLabel() + " (boleh salah " + all[i].getAllowedErrors() + "x)";
        }
        labels[all.length] = "Back";
        difficultyMenu = new MenuList(labels);
    }

    @Override
    public void onEnter() {
        modeMenu.setEnabled(1, anySave());
    }

    private boolean anySave() {
        for (int s = 1; s <= Config.SAVE_SLOTS; s++) {
            if (ctx.getSaves().exists(s)) return true;
        }
        return false;
    }

    private String slotLabel(int slot) {
        SaveData data = ctx.getSaves().load(slot);
        if (data == null) return "Slot " + slot + " - (kosong)";
        return "Slot " + slot + " - " + StoryScript.stage(data.getStageIndex()).title()
                + " | " + data.getDifficulty().getLabel() + " | " + GFX.formatTime(data.getPlaySeconds());
    }

    private void openSlotMenu(boolean forNew) {
        creatingNew = forNew;
        String[] labels = new String[Config.SAVE_SLOTS + 1];
        for (int s = 1; s <= Config.SAVE_SLOTS; s++) labels[s - 1] = slotLabel(s);
        labels[Config.SAVE_SLOTS] = "Back";
        slotMenu = new MenuList(labels);
        if (!forNew) {
            for (int s = 1; s <= Config.SAVE_SLOTS; s++) slotMenu.setEnabled(s - 1, ctx.getSaves().exists(s));
        }
        step = Step.SLOT;
        message = "";
    }

    private MenuList currentMenu() {
        return switch (step) {
            case MODE -> modeMenu;
            case SLOT -> slotMenu;
            case DIFFICULTY -> difficultyMenu;
        };
    }

    @Override
    public void keyPressed(int keyCode) {
        MenuList menu = currentMenu();
        if (menu.navigate(keyCode)) return;
        if (Input.isBack(keyCode)) {
            goBack();
            return;
        }
        if (!Input.isConfirm(keyCode) || !menu.isSelectedEnabled()) return;
        int choice = menu.getSelected();
        switch (step) {
            case MODE -> onModeChoice(choice);
            case SLOT -> onSlotChoice(choice);
            case DIFFICULTY -> onDifficultyChoice(choice);
        }
    }

    private void goBack() {
        switch (step) {
            case MODE -> ctx.getScenes().changeTo(new MenuScene(ctx));
            case SLOT -> step = Step.MODE;
            case DIFFICULTY -> step = Step.SLOT;
        }
    }

    private void onModeChoice(int choice) {
        switch (choice) {
            case 0 -> openSlotMenu(true);
            case 1 -> openSlotMenu(false);
            default -> ctx.getScenes().changeTo(new MenuScene(ctx));
        }
    }

    private void onSlotChoice(int choice) {
        if (choice == Config.SAVE_SLOTS) {
            step = Step.MODE;
            return;
        }
        chosenSlot = choice + 1;
        if (creatingNew) {
            step = Step.DIFFICULTY;
            return;
        }
        SaveData data = ctx.getSaves().load(chosenSlot);
        if (data == null) message = "File save rusak atau tidak ditemukan.";
        else ctx.getScenes().changeTo(new StoryScene(ctx, data));
    }

    private void onDifficultyChoice(int choice) {
        if (choice == Difficulty.values().length) {
            step = Step.SLOT;
            return;
        }
        SaveData data = ctx.getSaves().createNew(chosenSlot, Difficulty.values()[choice]);
        if (data == null) message = "Gagal membuat file save.";
        else ctx.getScenes().changeTo(new StoryScene(ctx, data));
    }

    @Override
    public void update(double dt) { }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Config.DARK);
        g.fillRect(0, 0, Config.WIDTH, Config.HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(Config.FONT_TITLE);
        GFX.drawCentered(g, "Story Mode", Config.WIDTH / 2, 110);

        g.setColor(Config.ACCENT);
        g.setFont(Config.FONT);
        String subtitle = switch (step) {
            case MODE -> "Mulai cerita baru atau lanjutkan";
            case SLOT -> creatingNew ? "New File - pilih slot (slot terisi akan ditimpa)" : "Load State - pilih slot";
            case DIFFICULTY -> "Pilih tingkat kesulitan";
        };
        GFX.drawCentered(g, subtitle, Config.WIDTH / 2, 160);

        currentMenu().render(g, Config.WIDTH / 2, 250, 55);

        g.setFont(Config.FONT_SMALL);
        g.setColor(Config.TEXT);
        if (!message.isEmpty()) GFX.drawCentered(g, message, Config.WIDTH / 2, 500);
        GFX.drawCentered(g, "Up/Down: pilih   |   SPACE: konfirmasi   |   SHIFT: kembali", Config.WIDTH / 2, 565);
    }
}