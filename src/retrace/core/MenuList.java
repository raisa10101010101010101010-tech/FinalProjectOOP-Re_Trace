package retrace.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/** Vertical menu with a select cursor (arrow keys). Items can be disabled. */
public class MenuList {
    private static final Color DISABLED = new Color(120, 90, 70);
    private static final Color UNFOCUSED = new Color(200, 170, 140);

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

    public boolean navigate(int keyCode) {
        if (keyCode == KeyEvent.VK_UP) {
            selected = (selected + labels.length - 1) % labels.length;
            return true;
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            selected = (selected + 1) % labels.length;
            return true;
        }
        return false;
    }

    public void render(Graphics2D g, int centerX, int firstBaselineY, int lineHeight) {
        render(g, centerX, firstBaselineY, lineHeight, Config.FONT_BIG, true);
    }

    public void render(Graphics2D g, int centerX, int firstBaselineY, int lineHeight, Font font, boolean focused) {
        g.setFont(font);
        for (int i = 0; i < labels.length; i++) {
            boolean sel = i == selected;
            String text = sel && focused ? "> " + labels[i] + " <" : labels[i];
            if (!enabled[i]) g.setColor(DISABLED);
            else if (sel) g.setColor(Config.ACCENT);
            else g.setColor(focused ? Config.TEXT : UNFOCUSED);
            GFX.drawCentered(g, text, centerX, firstBaselineY + i * lineHeight);
        }
    }
}