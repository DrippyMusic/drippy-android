package me.vitormac.drippy.providers;

import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CacheableInputStream extends InputStream {

    protected final InputStream stream;
    protected final OutputStream output;

    public CacheableInputStream(InputStream stream, File file) throws FileNotFoundException {
        this.stream = stream;
        this.output = new BufferedOutputStream(new FileOutputStream(file));
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
        IOUtils.copy(this.stream, this.output);
        this.stream.close();
        this.output.close();
        super.close();
    }

}
