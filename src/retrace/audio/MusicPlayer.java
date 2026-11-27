package retrace.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/** Plays one looping placeholder track at a time. Fails silently if no audio device exists. */
public class MusicPlayer {
    private static final AudioFormat FORMAT = new AudioFormat(22050f, 16, 1, true, false);

    private Clip clip;
    private Track current;
    private boolean muted;
    private boolean available = true;

    public void play(Track track) {
        if (track == current && clip != null) return; // already playing
        stopClip();
        current = track;
        if (!muted) start(track);
    }

    public void stop() {
        stopClip();
        current = null;
    }

    public void toggleMute() {
        muted = !muted;
        if (muted) stopClip();
        else if (current != null) start(current);
    }

    private void start(Track track) {
        if (!available) return;
        try {
            clip = AudioSystem.getClip();
            clip.open(FORMAT, track.getPcm(), 0, track.getPcm().length);
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(Math.max(gain.getMinimum(), -12f));
            }
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            available = false;
            clip = null;
        }
    }

    private void stopClip() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }
}