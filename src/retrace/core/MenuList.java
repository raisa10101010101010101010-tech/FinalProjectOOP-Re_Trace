package retrace.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/** Menu vertikal: navigasi Up/Down, item bisa dinonaktifkan. */
public class MenuList {
    private final String[] labels;
    private final boolean[] enabled;
    private int selected;

    public MenuList(String... labels) {
        this.labels = labels.clone();
        this.enabled = new boolean[labels.length];
        Arrays.fill(enabled, true);
    }

    public void setEnabled(int index, boolean value) { enabled[index] = value; }

    public int getSelected() { return selected; }

    public boolean isSelectedEnabled() { return enabled[selected]; }

    public void moveUp() { selected = (selected + labels.length - 1) % labels.length; }

    public void moveDown() { selected = (selected + 1) % labels.length; }

    /** Mengembalikan true bila tombol dipakai untuk navigasi. */
    public boolean navigate(int keyCode) {
        if (keyCode == KeyEvent.VK_UP) { moveUp(); return true; }
        if (keyCode == KeyEvent.VK_DOWN) { moveDown(); return true; }
        return false;
    }

    public void render(Graphics2D g, int centerX, int firstBaselineY, int lineHeight) {
        g.setFont(Config.FONT_BIG);
        for (int i = 0; i < labels.length; i++) {
            String text = i == selected ? "> " + labels[i] + " <" : labels[i];
            if (!enabled[i]) g.setColor(new Color(120, 90, 70));
            else g.setColor(i == selected ? Config.ACCENT : Config.TEXT);
            GFX.drawCentered(g, text, centerX, firstBaselineY + i * lineHeight);
        }
    }
}