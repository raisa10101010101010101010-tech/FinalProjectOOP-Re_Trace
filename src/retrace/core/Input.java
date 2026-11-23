package retrace.core;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class Input {
    private final Set<Integer> down = new HashSet<>();

    /** Mengembalikan true hanya pada penekanan pertama (auto-repeat diabaikan). */
    public boolean press(int keyCode) { return down.add(keyCode); }

    public void release(int keyCode) { down.remove(keyCode); }

    public void clear() { down.clear(); }

    public boolean isDown(int keyCode) { return down.contains(keyCode); }

    public static boolean isConfirm(int keyCode) {
        return keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_ENTER;
    }

    public static boolean isBack(int keyCode) {
        return keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_ESCAPE;
    }
}