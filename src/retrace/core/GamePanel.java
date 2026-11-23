package retrace.core;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel {
    private final SceneManager scenes;
    private final Timer timer;
    private long last = System.nanoTime();

    public GamePanel(GameContext ctx) {
        this.scenes = ctx.getScenes();
        setPreferredSize(new Dimension(Config.WIDTH, Config.HEIGHT));
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (ctx.getInput().press(e.getKeyCode())) scenes.keyPressed(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                ctx.getInput().release(e.getKeyCode());
            }
        });
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                ctx.getInput().clear();
            }
        });

        timer = new Timer(1000 / Config.FPS, e -> tick());
    }

    public void start() {
        last = System.nanoTime();
        timer.start();
        requestFocusInWindow();
    }

    private void tick() {
        long now = System.nanoTime();
        double dt = Math.min((now - last) / 1e9, 0.1);
        last = now;
        scenes.update(dt);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        scenes.render(g);
        g.dispose();
        Toolkit.getDefaultToolkit().sync();
    }
}