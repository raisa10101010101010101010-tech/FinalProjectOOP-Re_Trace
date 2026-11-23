package retrace.data;

import retrace.core.Config;
import retrace.minigame.MiniGameType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/** 5 skor tertinggi per minigame, disimpan di saves/scoreboard.properties. */
public class ScoreBoard {
    private static final int MAX_ENTRIES = 5;

    private final Path file = Paths.get(Config.SAVE_DIR, "scoreboard.properties");
    private final Map<MiniGameType, List<Integer>> scores = new EnumMap<>(MiniGameType.class);

    public ScoreBoard() {
        Properties props = PropertiesIO.read(file);
        for (MiniGameType type : MiniGameType.values()) {
            List<Integer> list = new ArrayList<>();
            if (props != null) {
                for (String part : props.getProperty(type.getKey(), "").split(",")) {
                    try {
                        if (!part.isBlank()) list.add(Integer.parseInt(part.trim()));
                    } catch (NumberFormatException ignored) {
                        // lewati nilai rusak
                    }
                }
            }
            list.sort(Comparator.reverseOrder());
            scores.put(type, list);
        }
    }

    public List<Integer> getScores(MiniGameType type) {
        return Collections.unmodifiableList(scores.get(type));
    }

    /** Mengembalikan peringkat (1..5) atau -1 bila tidak masuk papan skor. */
    public int submit(MiniGameType type, int score) {
        List<Integer> list = scores.get(type);
        list.add(score);
        list.sort(Comparator.reverseOrder());
        int rank = list.indexOf(score) + 1;
        while (list.size() > MAX_ENTRIES) list.remove(list.size() - 1);
        save();
        return rank <= MAX_ENTRIES ? rank : -1;
    }

    private void save() {
        Properties props = new Properties();
        for (MiniGameType type : MiniGameType.values()) {
            props.setProperty(type.getKey(),
                    scores.get(type).stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
        PropertiesIO.write(file, props, "Re:Trace scoreboard");
    }
}