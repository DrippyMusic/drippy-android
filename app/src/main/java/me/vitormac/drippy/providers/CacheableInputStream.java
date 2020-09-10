package me.vitormac.drippy.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CacheableInputStream extends InputStream {

    protected final InputStream stream;
    protected final OutputStream output;

    public CacheableInputStream(InputStream stream, OutputStream output) {
        this.stream = stream;
        this.output = output;
    }

    @Override
    public final int read() {
        return -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int i = this.stream.read(b, off, len);
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
