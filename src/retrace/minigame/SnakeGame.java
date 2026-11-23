package retrace.minigame;

import retrace.core.Config;
import retrace.core.Difficulty;
import retrace.core.GameContext;
import retrace.core.GFX;
import retrace.sprite.SpriteId;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class SnakeGame extends MiniGame {
    private static final int COLS = 20;
    private static final int ROWS = 14;
    private static final int TARGET = 8;
    private static final double STEP = 0.16;
    private static final double STEP_FAST = 0.07;

    private final Deque<int[]> snake = new ArrayDeque<>();
    private final Random random = new Random();
    private int dx, dy, nextDx, nextDy;
    private int[] food;
    private int eaten;
    private double timer;
    private double ready;
    private boolean paused;

    public SnakeGame(GameContext ctx, Difficulty difficulty) {
        super(ctx, MiniGameType.SNAKE, difficulty);
        respawn();
        spawnFood();
    }

    private void respawn() {
        snake.clear();
        int cx = COLS / 2;
        int cy = ROWS / 2;
        for (int i = 0; i < 3; i++) snake.addLast(new int[]{cx - i, cy});
        dx = nextDx = 1;
        dy = nextDy = 0;
        timer = 0;
        ready = 1.0;
        if (food != null && isOnSnake(food[0], food[1])) spawnFood();
    }

    private void spawnFood() {
        int fx;
        int fy;
        do {
            fx = random.nextInt(COLS);
            fy = random.nextInt(ROWS);
        } while (isOnSnake(fx, fy));
        food = new int[]{fx, fy};
    }

    private boolean isOnSnake(int x, int y) {
        for (int[] s : snake) {
            if (s[0] == x && s[1] == y) return true;
        }
        return false;
    }

    @Override
    protected boolean isClockRunning() { return !paused; }

    @Override
    protected void onUpdate(double dt) {
        if (paused) return;
        if (ready > 0) {
            ready -= dt;
            return;
        }
        timer += dt;
        double interval = ctx.getInput().isDown(KeyEvent.VK_SHIFT) ? STEP_FAST : STEP;
        while (timer >= interval && ready <= 0 && getState() == State.PLAYING) {
            timer -= interval;
            step();
        }
    }

    private void step() {
        dx = nextDx;
        dy = nextDy;
        int[] head = snake.peekFirst();
        int nx = head[0] + dx;
        int ny = head[1] + dy;
        boolean eat = nx == food[0] && ny == food[1];
        boolean crash = nx < 0 || ny < 0 || nx >= COLS || ny >= ROWS;
        if (!crash) {
            int index = 0;
            for (int[] s : snake) {
                index++;
                boolean tail = index == snake.size();
                if (tail && !eat) continue;
                if (s[0] == nx && s[1] == ny) {
                    crash = true;
                    break;
                }
            }
        }
        if (crash) {
            respawn();
            registerError();
            return;
        }
        snake.addFirst(new int[]{nx, ny});
        if (eat) {
            eaten++;
            if (eaten >= TARGET) {
                win();
                return;
            }
            spawnFood();
        } else {
            snake.removeLast();
        }
    }

    @Override
    protected void onKeyPressed(int key) {
        switch (key) {
            case KeyEvent.VK_UP -> { if (dy == 0) { nextDx = 0; nextDy = -1; } }
            case KeyEvent.VK_DOWN -> { if (dy == 0) { nextDx = 0; nextDy = 1; } }
            case KeyEvent.VK_LEFT -> { if (dx == 0) { nextDx = -1; nextDy = 0; } }
            case KeyEvent.VK_RIGHT -> { if (dx == 0) { nextDx = 1; nextDy = 0; } }
            case KeyEvent.VK_SPACE -> paused = !paused;
            default -> { }
        }
    }

    @Override
    protected int computeScore() {
        return Math.max(100, eaten * 100 + Math.max(0, 300 - (int) getElapsed()) - getErrors() * 50);
    }

    @Override
    public String getStatus() { return "Makanan: " + eaten + "/" + TARGET; }

    @Override
    public String getControlsHint() { return "Arrow: arah | SHIFT (tahan): ngebut | SPACE: jeda"; }

    @Override
    public void render(Graphics2D g, int x, int y, int w, int h) {
        int cell = Math.min(w / COLS, h / ROWS);
        int fx = x + (w - cell * COLS) / 2;
        int fy = y + (h - cell * ROWS) / 2;
        ctx.getSprites().draw(g, SpriteId.SNAKE_FIELD, fx, fy, cell * COLS, cell * ROWS);
        ctx.getSprites().draw(g, SpriteId.FOOD, fx + food[0] * cell + 2, fy + food[1] * cell + 2, cell - 4, cell - 4);

        boolean head = true;
        for (int[] s : snake) {
            ctx.getSprites().draw(g, head ? SpriteId.SNAKE_HEAD : SpriteId.SNAKE_BODY,
                    fx + s[0] * cell + 1, fy + s[1] * cell + 1, cell - 2, cell - 2);
            head = false;
        }
        if (paused || ready > 0) {
            g.setFont(Config.FONT_BIG);
            g.setColor(Config.TEXT);
            GFX.drawCentered(g, paused ? "PAUSE" : "SIAP...", x + w / 2, y + h / 2);
        }
    }
}