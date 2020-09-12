package me.vitormac.drippy.providers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CacheableInputStream extends InputStream {

    protected final InputStream stream;
    protected final OutputStream output;

    public CacheableInputStream(InputStream stream, OutputStream output) {
        this.stream = new BufferedInputStream(stream);
        this.output = new BufferedOutputStream(output);
    }

    @Override
    public final int read() {
        return -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int i = 0;

        do {
            int read = this.stream.read(b, i, len - i);
            if (read < 0) {
                if (i == 0) {
                    return -1;
                }

                break;
            }

            i += read;
        } while (i < len);
        this.output.write(b);

        return i;
    }

    @Override
    public final void close() throws IOException {
        this.stream.close();
        this.output.close();
        super.close();
    }

}
