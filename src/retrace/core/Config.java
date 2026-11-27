package retrace.core;

import java.awt.Color;
import java.awt.Font;

public final class Config {
    public static final int WIDTH = 800, HEIGHT = 600, FPS = 60;

    /** Set true to open Game Mode without finishing Story Mode (testing). */
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
    public static final Font FONT_LIST = new Font(Font.MONOSPACED, Font.BOLD, 17);
    public static final Font FONT_SMALL = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    private Config() { }
}