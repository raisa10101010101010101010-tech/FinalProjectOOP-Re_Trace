package retrace.scene;

import retrace.minigame.MiniGameType;

import java.awt.Color;
import java.util.List;

public final class StoryScript {
    public record StageDefinition(String title, MiniGameType type, Color background, String introText) { }

    private static final List<StageDefinition> STAGES = List.of(
            new StageDefinition("Ingatan 1", MiniGameType.NONOGRAM, new Color(178, 106, 68),
                    "[PLACEHOLDER PLOT] Gelap dan dingin. Sebuah suara samar memanggilmu dari kejauhan. "
                            + "Susun kembali kepingan ingatan yang tersisa."),
            new StageDefinition("Ingatan 2", MiniGameType.SNAKE, new Color(84, 122, 92),
                    "[PLACEHOLDER PLOT] Ingatan terus bergerak dan tidak mau berhenti. "
                            + "Telusuri satu per satu agar kamu ingat kenapa kamu harus bangun."),
            new StageDefinition("Ingatan 3", MiniGameType.MINESWEEPER, new Color(84, 104, 156),
                    "[PLACEHOLDER PLOT] Tidak semua ingatan perlu diingat. "
                            + "Pilah mana yang harus dipendam dan mana yang harus dibuka kembali.")
    );

    private static final String[] ERROR_LINES = {
            "[PLACEHOLDER PLOT] Ingatan ini mulai kabur... seseorang memanggil namamu.",
            "[PLACEHOLDER PLOT] Kegelapan merayap. Kamu hampir lupa kenapa kamu harus bangun.",
            "[PLACEHOLDER PLOT] Satu kesalahan lagi, dan ingatan ini akan menghilang.",
            "[PLACEHOLDER PLOT] Suara itu semakin jauh.",
            "[PLACEHOLDER PLOT] Dingin. Kamu hampir tidak bisa merasakan apa pun.",
            "[PLACEHOLDER PLOT] Terlalu gelap untuk melihat."
    };

    public static final String FAIL_TEXT =
            "[PLACEHOLDER PLOT] Ingatan ini terlalu kabur untuk diraih. Kamu harus menelusurinya sekali lagi.";

    public static final String ENDING_TEXT =
            "[PLACEHOLDER PLOT] Cahaya menembus kegelapan. Perlahan, kelopak matamu terbuka... kamu akhirnya terbangun.";

    private StoryScript() { }

    public static int stageCount() { return STAGES.size(); }

    public static StageDefinition stage(int index) {
        return STAGES.get(Math.max(0, Math.min(index, STAGES.size() - 1)));
    }

    public static String errorText(int errorNumber) {
        return ERROR_LINES[(errorNumber - 1) % ERROR_LINES.length];
    }
}