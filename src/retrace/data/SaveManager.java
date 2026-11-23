package retrace.data;

import retrace.core.Config;
import retrace.core.Difficulty;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

/** New file, save state, dan load state untuk Story Mode (file di folder saves/). */
public class SaveManager {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Path dir = Paths.get(Config.SAVE_DIR);

    private Path slotFile(int slot) { return dir.resolve("slot" + slot + ".properties"); }

    private Path profileFile() { return dir.resolve("profile.properties"); }

    public boolean exists(int slot) { return PropertiesIO.read(slotFile(slot)) != null; }

    /** New file: membuat save baru (menimpa bila slot sudah terisi). Null bila gagal. */
    public SaveData createNew(int slot, Difficulty difficulty) {
        SaveData data = new SaveData(slot, difficulty);
        return save(data) ? data : null;
    }

    /** Save state. */
    public boolean save(SaveData data) {
        data.setSavedAt(LocalDateTime.now().format(FORMAT));
        Properties props = new Properties();
        props.setProperty("difficulty", data.getDifficulty().name());
        props.setProperty("stage", String.valueOf(data.getStageIndex()));
        props.setProperty("errors", String.valueOf(data.getTotalErrors()));
        props.setProperty("playSeconds", String.valueOf((long) data.getPlaySeconds()));
        props.setProperty("savedAt", data.getSavedAt());
        return PropertiesIO.write(slotFile(data.getSlot()), props, "Re:Trace save");
    }

    /** Load state. Null bila file tidak ada atau rusak. */
    public SaveData load(int slot) {
        Properties props = PropertiesIO.read(slotFile(slot));
        if (props == null) return null;
        try {
            SaveData data = new SaveData(slot, Difficulty.valueOf(props.getProperty("difficulty", "NORMAL")));
            data.setStageIndex(Integer.parseInt(props.getProperty("stage", "0")));
            data.setTotalErrors(Integer.parseInt(props.getProperty("errors", "0")));
            data.setPlaySeconds(Long.parseLong(props.getProperty("playSeconds", "0")));
            data.setSavedAt(props.getProperty("savedAt", "-"));
            return data;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isStoryCompleted() {
        Properties props = PropertiesIO.read(profileFile());
        return props != null && Boolean.parseBoolean(props.getProperty("storyCompleted", "false"));
    }

    public void setStoryCompleted(boolean completed) {
        Properties props = new Properties();
        props.setProperty("storyCompleted", String.valueOf(completed));
        PropertiesIO.write(profileFile(), props, "Re:Trace profile");
    }
}