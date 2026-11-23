package retrace;

import retrace.core.GameContext;
import retrace.core.GamePanel;
import retrace.scene.MenuScene;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameContext ctx = new GameContext();
            ctx.getScenes().setImmediate(new MenuScene(ctx));

            JFrame frame = new JFrame("Re:Trace");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            GamePanel panel = new GamePanel(ctx);
            frame.setContentPane(panel);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            panel.start();
        });
    }
}