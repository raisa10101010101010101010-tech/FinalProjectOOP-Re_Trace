package retrace.sprite;

import retrace.core.GFX;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/** Placeholder: persegi berwarna dengan label singkat. */
public class RectSprite implements Sprite {
    private final Color fill;
    private final Color border;
    private final String label;

    public RectSprite(Color fill, Color border, String label) {
        this.fill = fill;
        this.border = border;
        this.label = label == null ? "" : label;
    }

    public RectSprite(Color fill, String label) {
        this(fill, fill.darker(), label);
    }

    @Override
    public void draw(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(fill);
        g.fillRect(x, y, w, h);
        g.setColor(border);
        g.drawRect(x, y, w - 1, h - 1);
        if (!label.isEmpty()) {
            Font old = g.getFont();
            double luminance = 0.299 * fill.getRed() + 0.587 * fill.getGreen() + 0.114 * fill.getBlue();
            g.setColor(luminance < 128 ? Color.WHITE : new Color(40, 25, 15));
            int size = Math.max(8, Math.min(h / 2, (int) (w / (label.length() * 0.7))));
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, size));
            GFX.centerText(g, label, x, y, w, h);
            g.setFont(old);
        }
    }
}