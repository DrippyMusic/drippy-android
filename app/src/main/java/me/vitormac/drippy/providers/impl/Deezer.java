package me.vitormac.drippy.providers.impl;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import me.vitormac.drippy.providers.CacheableInputStream;
import me.vitormac.drippy.providers.ProviderBase;
import me.vitormac.drippy.providers.model.DeezerData;

public class Deezer extends ProviderBase<DeezerData> {

    public Deezer(JsonObject data, String id) {
        super(data, id);
    }

    @Override
    protected InputStream getInputStream(HttpURLConnection connection) throws IOException {
        return new DecryptStream(connection.getInputStream(),
                this.getOutputStream(), this.data.getKey());
    }

    @Override
    protected String getMimeType() {
        return "audio/mpeg";
    }

    @Override
    protected DeezerData map(JsonObject object) {
        DeezerData data = new DeezerData();
        data.setUri(object.get("uri").getAsString());
        data.setKey(object.get("key").getAsString());
        data.setSize(object.get("size").getAsInt());
        return data;
    }

    private static class DecryptStream extends CacheableInputStream {

        private int c = 0;

        private final SecretKeySpec key;
        private final IvParameterSpec spec = new IvParameterSpec(new byte[]{
                0, 1, 2, 3, 4, 5, 6, 7
        });

        DecryptStream(InputStream stream, OutputStream output, SecretKeySpec key) {
            super(stream, output);
            this.key = key;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            byte[] data = new byte[4096];
            int i = 0;

            do {
                int read = this.stream.read(data, i, len - i);
                if (read < 0) {
                    if (i == 0) {
                        return -1;
                    }

                    break;
                }

                i += read;
            } while (i < len);

            for (int pos = 0; pos < len; pos += 2048, this.c++) {
                byte[] chunk = Arrays.copyOfRange(data, pos, pos + 2048);

                if (this.c % 3 > 0 || i < 2048) {
                    System.arraycopy(chunk, 0, b, pos, chunk.length);
                    this.output.write(chunk);
                    if (i < 2048) {
                        return i;
                    }
                    continue;
                }

                try {
                    Cipher cipher = Cipher.getInstance("Blowfish/CBC/NoPadding");
                    cipher.init(Cipher.DECRYPT_MODE, this.key, this.spec);

                    byte[] buffer = cipher.doFinal(chunk);
                    System.arraycopy(buffer, 0, b, pos, buffer.length);
                    this.output.write(buffer);
                } catch (NoSuchAlgorithmException
                        | NoSuchPaddingException
                        | InvalidAlgorithmParameterException
                        | InvalidKeyException
                        | BadPaddingException
                        | IllegalBlockSizeException e) {
                    return -1;
                }
            }

            return data.length;
        }

    }

}
