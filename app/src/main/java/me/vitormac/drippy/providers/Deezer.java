package me.vitormac.drippy.providers;

import android.webkit.WebResourceResponse;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import me.vitormac.drippy.providers.model.DeezerData;

public class Deezer extends ProviderBase<DeezerData> {

    public Deezer(JsonObject data) {
        super(data);
    }

    @Override
    public WebResourceResponse stream(String range) throws IOException {
        URL url = new URL(this.data.getUri());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Range", range);

        Map<String, String> headers = ProviderBase.getHeaders(connection,
                "Content-Length", "Content-Range");
        DecryptStream stream = new DecryptStream(this.data.getKey(),
                connection.getInputStream());
        return new WebResourceResponse("audio/mpeg", null, 206,
                "Partial Content", headers, stream);
    }

    @Override
    protected DeezerData map(JsonObject object) {
        DeezerData data = new DeezerData();
        data.setUri(object.get("uri").getAsString());
        data.setKey(object.get("key").getAsString());
        data.setSize(object.get("size").getAsInt());
        return data;
    }

    private static class DecryptStream extends InputStream {

        private int c = 0;

        private final SecretKeySpec key;
        private final IvParameterSpec spec = new IvParameterSpec(new byte[]{
                0, 1, 2, 3, 4, 5, 6, 7
        });

        private final InputStream stream;

        DecryptStream(SecretKeySpec key, InputStream stream) {
            this.key = key;
            this.stream = stream;
        }

        @Override
        public int read() {
            return -1;
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
