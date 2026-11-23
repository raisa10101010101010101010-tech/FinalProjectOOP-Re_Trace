package retrace.minigame;

import retrace.core.Config;
import retrace.core.Difficulty;
import retrace.core.GameContext;
import retrace.core.GFX;
import retrace.sprite.SpriteId;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NonogramGame extends MiniGame {
    private static final int EMPTY = 0;
    private static final int FILLED = 1;
    private static final int MARKED = 2;

    /** Gambar placeholder ("ingatan"). Hanya puzzle dengan solusi unik. */
    private static final String[][] PUZZLES = {
            {".#.#.", "#####", "#####", ".###.", "..#.."},
            {".#.#.", ".#.#.", ".....", "#...#", ".###."},
            {"..#..", ".###.", "#####", "..#..", "..#.."}
    };

    private final int rows;
    private final int cols;
    private final boolean[][] solution;
    private final int[][] cells;
    private final List<List<Integer>> rowClues = new ArrayList<>();
    private final List<List<Integer>> colClues = new ArrayList<>();
    private int cursorRow;
    private int cursorCol;
    private int remaining;

    public NonogramGame(GameContext ctx, Difficulty difficulty) {
        super(ctx, MiniGameType.NONOGRAM, difficulty);
        String[] picture = PUZZLES[new Random().nextInt(PUZZLES.length)];
        rows = picture.length;
        cols = picture[0].length();
        solution = new boolean[rows][cols];
        cells = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                solution[r][c] = picture[r].charAt(c) == '#';
                if (solution[r][c]) remaining++;
            }
        }
        for (int r = 0; r < rows; r++) rowClues.add(clueOf(solution[r]));
        for (int c = 0; c < cols; c++) {
            boolean[] column = new boolean[rows];
            for (int r = 0; r < rows; r++) column[r] = solution[r][c];
            colClues.add(clueOf(column));
        }
    }

    private static List<Integer> clueOf(boolean[] line) {
        List<Integer> clue = new ArrayList<>();
        int run = 0;
        for (boolean filled : line) {
            if (filled) {
                run++;
            } else if (run > 0) {
                clue.add(run);
                run = 0;
            }
        }
        if (run > 0) clue.add(run);
        if (clue.isEmpty()) clue.add(0);
        return clue;
    }

    @Override
    protected void onKeyPressed(int key) {
        switch (key) {
            case KeyEvent.VK_UP -> cursorRow = Math.max(0, cursorRow - 1);
            case KeyEvent.VK_DOWN -> cursorRow = Math.min(rows - 1, cursorRow + 1);
            case KeyEvent.VK_LEFT -> cursorCol = Math.max(0, cursorCol - 1);
            case KeyEvent.VK_RIGHT -> cursorCol = Math.min(cols - 1, cursorCol + 1);
            case KeyEvent.VK_SPACE -> fill();
            case KeyEvent.VK_SHIFT -> mark();
            default -> { }
        }
    }

    private void fill() {
        if (cells[cursorRow][cursorCol] == FILLED) return;
        if (solution[cursorRow][cursorCol]) {
            cells[cursorRow][cursorCol] = FILLED;
            remaining--;
            if (remaining == 0) win();
        } else {
            cells[cursorRow][cursorCol] = MARKED;
            registerError();
        }
    }

    private void mark() {
        int value = cells[cursorRow][cursorCol];
        if (value == FILLED) return;
        if (value == MARKED) {
            cells[cursorRow][cursorCol] = EMPTY;
        } else if (solution[cursorRow][cursorCol]) {
            registerError();
        } else {
            cells[cursorRow][cursorCol] = MARKED;
        }
    }

    @Override
    protected int computeScore() {
        return Math.max(100, 1000 - getErrors() * 150 - (int) getElapsed() * 3);
    }

    @Override
    public String getStatus() { return "Sisa kotak: " + remaining; }

    @Override
    public String getControlsHint() { return "Arrow: gerak | SPACE: isi | SHIFT: tandai X"; }

    private static int maxSize(List<List<Integer>> clues) {
        int max = 1;
        for (List<Integer> clue : clues) max = Math.max(max, clue.size());
        return max;
    }

    @Override
    public void render(Graphics2D g, int x, int y, int w, int h) {
        int rowSlots = maxSize(rowClues);
        int colSlots = maxSize(colClues);
        int cell = Math.min(56, Math.min(w / (cols + rowSlots), h / (rows + colSlots)));
        int gx = x + (w - (cols + rowSlots) * cell) / 2 + rowSlots * cell;
        int gy = y + (h - (rows + colSlots) * cell) / 2 + colSlots * cell;

        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, cell / 2));
        g.setColor(Config.TEXT);
        for (int r = 0; r < rows; r++) {
            List<Integer> clue = rowClues.get(r);
            for (int i = 0; i < clue.size(); i++) {
                GFX.centerText(g, String.valueOf(clue.get(i)),
                        gx - (clue.size() - i) * cell, gy + r * cell, cell, cell);
            }
        }
        for (int c = 0; c < cols; c++) {
            List<Integer> clue = colClues.get(c);
            for (int i = 0; i < clue.size(); i++) {
                GFX.centerText(g, String.valueOf(clue.get(i)),
                        gx + c * cell, gy - (clue.size() - i) * cell, cell, cell);
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                SpriteId id = switch (cells[r][c]) {
                    case FILLED -> SpriteId.NONOGRAM_FILLED;
                    case MARKED -> SpriteId.NONOGRAM_MARK;
                    default -> SpriteId.NONOGRAM_EMPTY;
                };
                ctx.getSprites().draw(g, id, gx + c * cell + 1, gy + r * cell + 1, cell - 2, cell - 2);
            }
        }
        ctx.getSprites().draw(g, SpriteId.CURSOR, gx + cursorCol * cell, gy + cursorRow * cell, cell, cell);
    }
}