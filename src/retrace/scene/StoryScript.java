package retrace.scene;

import retrace.minigame.MiniGameType;
import retrace.puzzle.PuzzleFactory;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class StoryScript {
    public record StageDefinition(int arc, int memory, MiniGameType type, int tier,
                                  Color background, String title, String introText) { }

    private static final MiniGameType N = MiniGameType.NONOGRAM;
    private static final MiniGameType S = MiniGameType.SNAKE;
    private static final MiniGameType M = MiniGameType.MINESWEEPER;

    private static final MiniGameType[][] ARCS = {
            {N, M, S},
            {N, S, N},
            {S, M, M},
            {S, N, N}
    };

    private static final String[] ARC_TEXT = {
            "[PLACEHOLDER PLOT] Arc 1. Darkness, cold, and a faint voice calling from far away.",
            "[PLACEHOLDER PLOT] Arc 2. The memories start to form a path back.",
            "[PLACEHOLDER PLOT] Arc 3. Some memories hurt. Choose which ones to keep.",
            "[PLACEHOLDER PLOT] Arc 4. The light is close. One last push to wake up."
    };

    private static final String[] ERROR_LINES = {
            "[PLACEHOLDER PLOT] This memory is blurring... someone is calling your name.",
            "[PLACEHOLDER PLOT] Darkness creeps in. You almost forgot why you must wake up.",
            "[PLACEHOLDER PLOT] One more mistake and this memory will fade away.",
            "[PLACEHOLDER PLOT] The voice is getting farther away.",
            "[PLACEHOLDER PLOT] Cold. You can barely feel anything.",
            "[PLACEHOLDER PLOT] Too dark to see."
    };

    public static final String FAIL_TEXT =
            "[PLACEHOLDER PLOT] This memory is too faint to hold on to. You must trace it once more.";

    public static final String ENDING_TEXT =
            "[PLACEHOLDER PLOT] Light pierces the dark. Slowly, your eyes open... you finally wake up.";

    private static final List<StageDefinition> STAGES = new ArrayList<>();

    static {
        Map<MiniGameType, Integer> totals = new EnumMap<>(MiniGameType.class);
        for (MiniGameType[] arc : ARCS) {
            for (MiniGameType t : arc) totals.merge(t, 1, Integer::sum);
        }
        Map<MiniGameType, Integer> seen = new EnumMap<>(MiniGameType.class);
        float[] hues = {0.07f, 0.35f, 0.60f, 0.80f};
        for (int a = 0; a < ARCS.length; a++) {
            for (int m = 0; m < ARCS[a].length; m++) {
                MiniGameType type = ARCS[a][m];
                int k = seen.merge(type, 1, Integer::sum) - 1;
                int tier = tierFor(k, totals.get(type));
                Color background = Color.getHSBColor(hues[a], 0.45f, 0.55f + 0.08f * m);
                String intro = m == 0 ? ARC_TEXT[a]
                        : "[PLACEHOLDER PLOT] Arc " + (a + 1) + ", memory " + (m + 1) + ". The path continues.";
                STAGES.add(new StageDefinition(a, m, type, tier, background,
                        "Arc " + (a + 1) + " - Memory " + (m + 1), intro));
            }
        }
    }

    private StoryScript() { }

    private static int tierFor(int occurrence, int total) {
        return total <= 1 ? 0 : Math.round(occurrence * (float) (PuzzleFactory.TIERS - 1) / (total - 1));
    }

    public static int stageCount() { return STAGES.size(); }

    public static StageDefinition stage(int index) {
        return STAGES.get(Math.max(0, Math.min(index, STAGES.size() - 1)));
    }

    /** How many stages of this minigame exist in the story (also the Game Mode stage count). */
    public static int stageCountOf(MiniGameType type) {
        int count = 0;
        for (MiniGameType[] arc : ARCS) {
            for (MiniGameType t : arc) if (t == type) count++;
        }
        return count;
    }

    /** Puzzle tier of the n-th (0-based) stage of a minigame. */
    public static int tierOf(MiniGameType type, int occurrence) {
        return tierFor(occurrence, stageCountOf(type));
    }

    public static String errorText(int errorNumber) {
        return ERROR_LINES[(errorNumber - 1) % ERROR_LINES.length];
    }
}