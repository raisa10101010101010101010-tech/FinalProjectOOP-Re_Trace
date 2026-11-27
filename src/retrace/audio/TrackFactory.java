package retrace.audio;

import retrace.minigame.MiniGameType;

import java.util.Random;

public final class TrackFactory {
    private static final float RATE = 22050f;
    private static final int[] SCALE = {0, 3, 5, 7, 10}; // minor pentatonic

    private enum Wave {
        SQUARE, PULSE, TRIANGLE;

        double sample(double phase) {
            return switch (this) {
                case SQUARE -> phase < 0.5 ? 1.0 : -1.0;
                case PULSE -> phase < 0.25 ? 1.0 : -1.0;
                case TRIANGLE -> 4 * Math.abs(phase - 0.5) - 1;
            };
        }
    }

    private TrackFactory() { }

    public static Track menu() { return build("menu", 45, 76, Wave.TRIANGLE, 11); }

    public static Track ending() { return build("ending", 52, 66, Wave.TRIANGLE, 23); }

    public static Track minigame(MiniGameType type, int arc) {
        return switch (type) {
            case NONOGRAM -> build("nonogram" + arc, 48 + arc * 2, 88 + arc * 8, Wave.PULSE, 100 + arc);
            case SNAKE -> build("snake" + arc, 52 + arc * 2, 120 + arc * 10, Wave.SQUARE, 200 + arc);
            case MINESWEEPER -> build("minesweeper" + arc, 45 + arc * 2, 80 + arc * 6, Wave.TRIANGLE, 300 + arc);
        };
    }

    private static Track build(String name, int rootMidi, double bpm, Wave lead, long seed) {
        Random rnd = new Random(seed); // same seed = same melody every run
        int steps = 16;
        int stepSamples = (int) (60.0 / bpm / 2 * RATE);
        double[] mix = new double[steps * stepSamples];
        for (int s = 0; s < steps; s++) {
            if (rnd.nextInt(100) < 75) {
                int note = rootMidi + 12 + SCALE[rnd.nextInt(SCALE.length)] + (rnd.nextBoolean() ? 12 : 0);
                addNote(mix, s * stepSamples, stepSamples, note, lead, 0.5);
            }
            if (s % 4 == 0) {
                int bass = rootMidi - 12 + (rnd.nextBoolean() ? 0 : 7);
                addNote(mix, s * stepSamples, stepSamples * 3, bass, Wave.TRIANGLE, 0.7);
            }
        }
        byte[] pcm = new byte[mix.length * 2];
        for (int i = 0; i < mix.length; i++) {
            short v = (short) (Math.max(-1, Math.min(1, mix[i])) * 11000);
            pcm[2 * i] = (byte) v;
            pcm[2 * i + 1] = (byte) (v >> 8);
        }
        return new Track(name, pcm);
    }

    private static void addNote(double[] mix, int start, int length, int midi, Wave wave, double volume) {
        double freq = 440.0 * Math.pow(2, (midi - 69) / 12.0);
        for (int i = 0; i < length && start + i < mix.length; i++) {
            double phase = (i / RATE * freq) % 1.0;
            double env = Math.min(1.0, i / 120.0) * (1.0 - (double) i / length);
            mix[start + i] += wave.sample(phase) * env * volume;
        }
    }
}