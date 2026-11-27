package retrace.asset;

import retrace.audio.Track;
import retrace.audio.TrackFactory;
import retrace.minigame.MiniGameType;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class AssetManager {
    private static final AssetManager INSTANCE = new AssetManager();

    private final Map<SpriteId, Sprite> sprites = new EnumMap<>(SpriteId.class); // flyweight pool
    private final Map<String, Track> tracks = new HashMap<>();                   // flyweight pool

    private AssetManager() { }

    public static AssetManager getInstance() { return INSTANCE; }

    public Sprite getSprite(SpriteId id) {
        return sprites.computeIfAbsent(id, AssetManager::createPlaceholder);
    }

    /** Replace a placeholder with real art later: register(SpriteId.FOOD, new ImageSprite(...)). */
    public void register(SpriteId id, Sprite sprite) { sprites.put(id, sprite); }

    public void draw(Graphics2D g, SpriteId id, int x, int y, int w, int h) {
        getSprite(id).draw(g, x, y, w, h);
    }

    public Track getMenuTrack() {
        return tracks.computeIfAbsent("menu", k -> TrackFactory.menu());
    }

    public Track getEndingTrack() {
        return tracks.computeIfAbsent("ending", k -> TrackFactory.ending());
    }

    /** One short loop per minigame; the arc (0-3) shifts key and tempo as the story progresses. */
    public Track getMinigameTrack(MiniGameType type, int arc) {
        return tracks.computeIfAbsent(type.getKey() + ":" + arc, k -> TrackFactory.minigame(type, arc));
    }

    private static Sprite createPlaceholder(SpriteId id) {
        return switch (id) {
            case NONOGRAM_EMPTY -> new RectSprite(new Color(250, 235, 210), "");
            case NONOGRAM_FILLED -> new RectSprite(new Color(50, 35, 25), "");
            case NONOGRAM_MARK -> new RectSprite(new Color(235, 205, 175), "X");
            case CURSOR -> new RectSprite(new Color(255, 230, 0, 90), new Color(255, 200, 0), "");
            case SNAKE_FIELD -> new RectSprite(new Color(0, 0, 0, 80), new Color(0, 0, 0, 160), "");
            case SNAKE_HEAD -> new RectSprite(new Color(70, 200, 90), "H");
            case SNAKE_BODY -> new RectSprite(new Color(50, 160, 70), "");
            case FOOD -> new RectSprite(new Color(230, 70, 70), "F");
            case OBSTACLE -> new RectSprite(new Color(90, 80, 75), "#");
            case MS_HIDDEN -> new RectSprite(new Color(161, 161, 130), "");
            case MS_REVEALED -> new RectSprite(new Color(225, 225, 225), "");
            case MS_FLAG -> new RectSprite(new Color(240, 200, 60), "F");
            case MS_MINE -> new RectSprite(new Color(220, 60, 60), "*");
            case MC_PORTRAIT -> new RectSprite(new Color(200, 160, 255), "MC");
        };
    }
}