package card;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.IntStream;

public final class StoryManiaInputStream extends FilterInputStream {

    /**
     * Creates a decoder input string turning an @{code .mp3} into an @{code .smp} and vise versa.
     *
     * @param in the underlying input stream to read from
     */
    public StoryManiaInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        int read = super.read();
        if (read >= 0) {
            return (byte) ((byte) read) ^ 102;
        }
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = super.read(b, off, len);
        if (read >= 0) {
            IntStream.range(off, read).forEach((i) -> {
                b[i] = (byte) ((byte) b[i] ^ 102);
            });
        }
        return read;
    }
}
