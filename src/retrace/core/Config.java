package retrace.core;

import java.awt.Color;
import java.awt.Font;

public final class Config {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int FPS = 60;

    /** Set true untuk membuka Game Mode tanpa menamatkan Story Mode (untuk testing). */
    public static final boolean DEBUG_UNLOCK_GAME_MODE = false;

    public static final String SAVE_DIR = "saves";
    public static final int SAVE_SLOTS = 3;

    public static final Color MENU_BG = new Color(255, 122, 31);
    public static final Color DARK = new Color(107, 58, 26);
    public static final Color ACCENT = new Color(253, 188, 87);
    public static final Color TEXT = new Color(255, 240, 220);

    public static final Font FONT_TITLE = new Font(Font.MONOSPACED, Font.BOLD, 56);
    public static final Font FONT_BIG = new Font(Font.MONOSPACED, Font.BOLD, 28);
    public static final Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 20);
    public static final Font FONT_SMALL = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    private Config() { }
}