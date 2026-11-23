package retrace.core;

import java.awt.Graphics2D;

public abstract class Scene {
    protected final GameContext ctx;

    protected Scene(GameContext ctx) {
        this.ctx = ctx;
    }

    public void onEnter() { }

    public void onExit() { }

    public abstract void update(double dt);

    public abstract void render(Graphics2D g);

    public abstract void keyPressed(int keyCode);
}