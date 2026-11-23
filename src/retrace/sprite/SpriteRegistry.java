package retrace.sprite;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

public class SpriteRegistry {
    private final Map<SpriteId, Sprite> sprites = new EnumMap<>(SpriteId.class);

    public SpriteRegistry() {
        register(SpriteId.NONOGRAM_EMPTY, new RectSprite(new Color(250, 235, 210), ""));
        register(SpriteId.NONOGRAM_FILLED, new RectSprite(new Color(50, 35, 25), ""));
        register(SpriteId.NONOGRAM_MARK, new RectSprite(new Color(235, 205, 175), "X"));
        register(SpriteId.CURSOR, new RectSprite(new Color(255, 230, 0, 90), new Color(255, 200, 0), ""));

        register(SpriteId.SNAKE_FIELD, new RectSprite(new Color(0, 0, 0, 80), new Color(0, 0, 0, 160), ""));
        register(SpriteId.SNAKE_HEAD, new RectSprite(new Color(70, 200, 90), "H"));
        register(SpriteId.SNAKE_BODY, new RectSprite(new Color(50, 160, 70), ""));
        register(SpriteId.FOOD, new RectSprite(new Color(230, 70, 70), "F"));

        register(SpriteId.MS_HIDDEN, new RectSprite(new Color(170, 170, 170), ""));
        register(SpriteId.MS_REVEALED, new RectSprite(new Color(225, 225, 225), ""));
        register(SpriteId.MS_FLAG, new RectSprite(new Color(240, 200, 60), "F"));
        register(SpriteId.MS_MINE, new RectSprite(new Color(220, 60, 60), "*"));

        register(SpriteId.MC_PORTRAIT, new RectSprite(new Color(200, 160, 255), "MC"));
    }

    public void register(SpriteId id, Sprite sprite) {
        sprites.put(id, sprite);
    }

    public void draw(Graphics2D g, SpriteId id, int x, int y, int w, int h) {
        sprites.get(id).draw(g, x, y, w, h);
    }
}