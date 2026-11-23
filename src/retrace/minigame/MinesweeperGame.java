package retrace.minigame;

import retrace.core.Difficulty;
import retrace.core.GameContext;
import retrace.core.GFX;
import retrace.sprite.SpriteId;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class MinesweeperGame extends MiniGame {
    private static final int SIZE = 8;
    private static final int MINES = 8;
    private static final int HIDDEN = 0;
    private static final int REVEALED = 1;
    private static final int FLAGGED = 2;
    private static final int EXPLODED = 3;
    private static final Color[] NUMBER_COLORS = {
            Color.BLACK, new Color(30, 60, 220), new Color(20, 140, 30), new Color(220, 40, 40),
            new Color(20, 20, 140), new Color(140, 40, 20), new Color(20, 140, 140), Color.BLACK, Color.GRAY
    };

    private final boolean[][] mine = new boolean[SIZE][SIZE];
    private final int[][] adjacent = new int[SIZE][SIZE];
    private final int[][] state = new int[SIZE][SIZE];
    private final Random random = new Random();
    private boolean minesPlaced;
    private int cursorRow, cursorCol;
    private int revealedSafe;
    private int flags;

    public MinesweeperGame(GameContext ctx, Difficulty difficulty) {
        super(ctx, MiniGameType.MINESWEEPER, difficulty);
    }

    private boolean inBounds(int r, int c) {
        return r >= 0 && c >= 0 && r < SIZE && c < SIZE;
    }

    private void placeMines(int safeRow, int safeCol) {
        int placed = 0;
        while (placed < MINES) {
            int r = random.nextInt(SIZE);
            int c = random.nextInt(SIZE);
            boolean nearSafe = Math.abs(r - safeRow) <= 1 && Math.abs(c - safeCol) <= 1;
            if (mine[r][c] || nearSafe) continue;
            mine[r][c] = true;
            placed++;
        }
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int count = 0;
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int rr = r + dr;
                        int cc = c + dc;
                        if ((dr != 0 || dc != 0) && inBounds(rr, cc) && mine[rr][cc]) count++;
                    }
                }
                adjacent[r][c] = count;
            }
        }
        minesPlaced = true;
    }

    @Override
    protected void onKeyPressed(int key) {
        switch (key) {
            case KeyEvent.VK_UP -> cursorRow = Math.max(0, cursorRow - 1);
            case KeyEvent.VK_DOWN -> cursorRow = Math.min(SIZE - 1, cursorRow + 1);
            case KeyEvent.VK_LEFT -> cursorCol = Math.max(0, cursorCol - 1);
            case KeyEvent.VK_RIGHT -> cursorCol = Math.min(SIZE - 1, cursorCol + 1);
            case KeyEvent.VK_SPACE -> reveal(cursorRow, cursorCol);
            case KeyEvent.VK_SHIFT -> toggleFlag(cursorRow, cursorCol);
            default -> { }
        }
    }

    private void toggleFlag(int r, int c) {
        if (state[r][c] == HIDDEN) {
            state[r][c] = FLAGGED;
            flags++;
        } else if (state[r][c] == FLAGGED) {
            state[r][c] = HIDDEN;
            flags--;
        }
    }

    private void reveal(int r, int c) {
        if (state[r][c] != HIDDEN) return;
        if (!minesPlaced) placeMines(r, c);
        if (mine[r][c]) {
            state[r][c] = EXPLODED;
            registerError();
            return;
        }
        Deque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{r, c});
        while (!queue.isEmpty()) {
            int[] p = queue.poll();
            if (state[p[0]][p[1]] != HIDDEN) continue;
            state[p[0]][p[1]] = REVEALED;
            revealedSafe++;
            if (adjacent[p[0]][p[1]] == 0) {
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int rr = p[0] + dr;
                        int cc = p[1] + dc;
                        if (inBounds(rr, cc) && state[rr][cc] == HIDDEN) queue.add(new int[]{rr, cc});
                    }
                }
            }
        }
        if (revealedSafe == SIZE * SIZE - MINES) win();
    }

    @Override
    protected int computeScore() {
        return Math.max(100, 1000 - getErrors() * 150 - (int) getElapsed() * 3);
    }

    @Override
    public String getStatus() { return "Ranjau: " + MINES + "  Bendera: " + flags; }

    @Override
    public String getControlsHint() { return "Arrow: gerak | SPACE: buka | SHIFT: bendera"; }

    @Override
    public void render(Graphics2D g, int x, int y, int w, int h) {
        int cell = Math.min(56, Math.min(w, h) / SIZE);
        int gx = x + (w - cell * SIZE) / 2;
        int gy = y + (h - cell * SIZE) / 2;
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, cell * 3 / 5));
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int px = gx + c * cell + 1;
                int py = gy + r * cell + 1;
                int size = cell - 2;
                switch (state[r][c]) {
                    case HIDDEN -> ctx.getSprites().draw(g, SpriteId.MS_HIDDEN, px, py, size, size);
                    case FLAGGED -> ctx.getSprites().draw(g, SpriteId.MS_FLAG, px, py, size, size);
                    case EXPLODED -> ctx.getSprites().draw(g, SpriteId.MS_MINE, px, py, size, size);
                    default -> {
                        ctx.getSprites().draw(g, SpriteId.MS_REVEALED, px, py, size, size);
                        if (adjacent[r][c] > 0) {
                            g.setColor(NUMBER_COLORS[adjacent[r][c]]);
                            GFX.centerText(g, String.valueOf(adjacent[r][c]), px, py, size, size);
                        }
                    }
                }
            }
        }
        ctx.getSprites().draw(g, SpriteId.CURSOR, gx + cursorCol * cell, gy + cursorRow * cell, cell, cell);
    }
}