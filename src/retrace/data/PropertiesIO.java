package retrace.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

final class PropertiesIO {
    private PropertiesIO() { }

    /** Mengembalikan null bila file tidak ada atau tidak bisa dibaca. */
    static Properties read(Path file) {
        if (!Files.exists(file)) return null;
        try (InputStream in = Files.newInputStream(file)) {
            Properties props = new Properties();
            props.load(in);
            return props;
        } catch (IOException e) {
            return null;
        }
    }

    static boolean write(Path file, Properties props, String comment) {
        try {
            Files.createDirectories(file.getParent());
            try (OutputStream out = Files.newOutputStream(file)) {
                props.store(out, comment);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}