package me.vitormac.drippy.codec;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public final class FFmpeg {

    private final Process process;
    private final InputStream stream;

    public FFmpeg(InputStream stream, AudioFormat format) throws IOException {
        this(stream, format, Bitrate.B96K);
    }

    public FFmpeg(InputStream stream, AudioFormat format, Bitrate bitrate) throws IOException {
        this.stream = stream;
        String path = Objects.requireNonNull(System.getProperty("native.dir"));

        this.process = new ProcessBuilder(path + "/ffmpeg", "-loglevel", "quiet",
                "-f", format.getName(), "-i", "pipe:0",
                "-c:a", "libopus", "-b:a", bitrate.getName(),
                "-f", "opus", "pipe:1").start();
        new Thread(new PipeWriter()).start();
    }

    public InputStream stdout() {
        return this.process.getInputStream();
    }

    private class PipeWriter implements Runnable {

        @Override
        public void run() {
            try {
                byte[] data = new byte[4096];
                OutputStream stdin = FFmpeg.this.process.getOutputStream();

                try (BufferedInputStream stream = IOUtils.buffer(FFmpeg.this.stream)) {
                    for (int i; (i = stream.read(data)) != -1; )
                        stdin.write(data, 0, i);
                    stdin.close();
                }
            } catch (IOException e) {
                FFmpeg.this.process.destroy();
            }
        }

    }

}
