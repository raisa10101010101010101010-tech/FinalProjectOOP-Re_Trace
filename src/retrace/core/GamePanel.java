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
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel {
    private final GameContext ctx;
    private final SceneManager scenes;
    private final Timer timer;
    private final BufferedImage frame = new BufferedImage(Config.WIDTH, Config.HEIGHT, BufferedImage.TYPE_INT_RGB);
    private long last = System.nanoTime();

    public GamePanel(GameContext ctx) {
        this.ctx = ctx;
        this.scenes = ctx.getScenes();
        setPreferredSize(new Dimension(Config.WIDTH, Config.HEIGHT));
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_F1) { ctx.getVhs().toggle(); return; }
                if (code == KeyEvent.VK_F2) { ctx.getMusic().toggleMute(); return; }
                if (ctx.getInput().press(code)) scenes.keyPressed(code);
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
        ctx.getVhs().update(dt);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D fg = frame.createGraphics();
        fg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        scenes.render(fg);
        fg.dispose();
        graphics.drawImage(ctx.getVhs().isEnabled() ? ctx.getVhs().apply(frame) : frame, 0, 0, null);
        Toolkit.getDefaultToolkit().sync();
    }
}