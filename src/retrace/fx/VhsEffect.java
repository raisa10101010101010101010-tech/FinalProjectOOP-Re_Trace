package retrace.fx;

import retrace.core.Config;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class VhsEffect {
    private static final int W = Config.WIDTH;
    private static final int H = Config.HEIGHT;

    private final BufferedImage output = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
    private final int[] out = ((DataBufferInt) output.getRaster().getDataBuffer()).getData();
    private final float[] shade = new float[W * H]; // precomputed vignette
    private boolean enabled = true;
    private double time;
    private double glitch;
    private int seed = 987654321;

    public VhsEffect() {
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                double nx = (x + 0.5) / W * 2 - 1;
                double ny = (y + 0.5) / H * 2 - 1;
                double r2 = nx * nx + ny * ny;
                shade[y * W + x] = (float) (1.0 - 0.45 * r2 / 2.0);
            }
        }
    }

    public boolean isEnabled() { return enabled; }

    public void toggle() { enabled = !enabled; }

    /** Short glitch burst (called when the player makes a mistake). */
    public void pulse() { glitch = Math.min(1.0, glitch + 0.8); }

    public void update(double dt) {
        time += dt;
        glitch = Math.max(0, glitch - dt * 1.6);
    }

    public BufferedImage apply(BufferedImage frame) {
        int[] src = ((DataBufferInt) frame.getRaster().getDataBuffer()).getData();
        double g = Math.max(glitch, time % 8.0 < 0.18 ? 0.3 : 0.0); // small glitch every few seconds
        int shift = 2 + (int) (g * 6);
        int noiseAmp = 12 + (int) (g * 40);
        double band = (time * 60) % (H + 100) - 50;                 // rolling tracking band

        for (int y = 0; y < H; y++) {
            int off = (int) Math.round(Math.sin(y * 0.05 + time * 2.0) * 0.7);
            double d = Math.abs(y - band);
            if (d < 12) off += (int) ((12 - d) / 3) * ((int) (time * 12) % 2 == 0 ? 1 : -1);
            seed = seed * 1664525 + 1013904223;
            if ((seed >>> 24) < g * 18) off += ((seed >>> 8) & 31) - 16; // line tears while glitching
            double scan = (y & 1) == 0 ? 1.1 : 0.88;
            int base = y * W;

            for (int x = 0; x < W; x++) {
                int i = base + x;
                int r = (src[base + clampX(x + off + shift)] >> 16) & 255;
                int gr = (src[base + clampX(x + off)] >> 8) & 255;
                int b = src[base + clampX(x + off - shift)] & 255;
                seed = seed * 1664525 + 1013904223;
                int n = (((seed >>> 24) & 31) - 16) * noiseAmp / 16;
                float k = (float) (shade[i] * scan);
                out[i] = (clamp255((int) (r * k) + n) << 16)
                        | (clamp255((int) (gr * k) + n) << 8)
                        | clamp255((int) (b * k) + n);
            }
        }
        return output;
    }

    private static int clampX(int x) { return x < 0 ? 0 : Math.min(x, W - 1); }

    private static int clamp255(int v) { return v < 0 ? 0 : Math.min(v, 255); }
}