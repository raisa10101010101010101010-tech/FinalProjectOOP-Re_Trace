package retrace.audio;

/** Immutable PCM data (16-bit, mono, 22050 Hz): a flyweight shared through AssetManager. */
public final class Track {
    private final String name;
    private final byte[] pcm;

    public Track(String name, byte[] pcm) {
        this.name = name;
        this.pcm = pcm;
    }

    public String getName() { return name; }

    public byte[] getPcm() { return pcm; }
}