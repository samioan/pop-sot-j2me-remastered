package javax.microedition.media;

import java.io.InputStream;

public final class Manager {
    public static Player createPlayer(InputStream in, String type) throws java.io.IOException, MediaException {
        if (in == null) throw new NullPointerException();
        while (in.read() >= 0) {}
        return new Player();
    }
    public static void playTone(int n, int d, int v) {}
}
