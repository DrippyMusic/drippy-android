package me.vitormac.drippy.providers;

import android.webkit.WebResourceResponse;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import me.vitormac.drippy.providers.model.DataModel;
import me.vitormac.drippy.webview.DrippyClient;

public abstract class ProviderBase<T extends DataModel> {

    protected final T data;
    private final String id;

    public ProviderBase(JsonObject data, String id) {
        this.data = this.map(data);
        this.id = id;
    }

    public final WebResourceResponse stream(String range) throws IOException {
        File file = this.getCache();
        if (file.exists()) {
            Map<String, String> headers = new HashMap<String, String>() {{
                put("Access-Control-Allow-Origin", "*");
            }};

            return new WebResourceResponse(this.getMimeType(), null,
                    200, "OK", headers, new FileInputStream(file));
        }

        URL url = new URL(this.data.getUri());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Range", range);

        Map<String, String> headers = this.getHeaders(connection,
                "Content-Length", "Content-Range");
        return new WebResourceResponse(this.getMimeType(), null, 206,
                "Partial Content", headers, this.getInputStream(connection));
    }

    protected InputStream getInputStream(HttpURLConnection connection) throws IOException {
        return new CacheableInputStream(connection.getInputStream(), this.getOutputStream());
    }

    protected final OutputStream getOutputStream() throws IOException {
        return new FileOutputStream(this.getCache());
    }

    protected abstract String getMimeType();

    protected abstract T map(JsonObject object);

    private Map<String, String> getHeaders(HttpURLConnection connection, String... headers) {
        Map<String, String> responseHeaders = new HashMap<String, String>() {{
            put("Access-Control-Allow-Origin", "*");
        }};

        for (String header : headers) {
            responseHeaders.put(header, connection.getHeaderField(header));
        }

        return responseHeaders;
    }

    private File getCache() {
        File file = new File(DrippyClient.getData(), "data");
        if (file.exists() || file.mkdir()) {
            return new File(file, this.id);
        }

        throw new RuntimeException("No data dir");
    }

}
