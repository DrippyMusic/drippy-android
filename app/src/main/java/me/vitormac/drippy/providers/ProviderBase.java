package me.vitormac.drippy.providers;

import android.webkit.WebResourceResponse;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public abstract class ProviderBase<T> {

    protected final T data;

    public ProviderBase(JsonObject data) {
        this.data = this.map(data);
    }

    public abstract WebResourceResponse stream(String range) throws IOException;

    protected abstract T map(JsonObject object);

    protected static Map<String, String> getHeaders(HttpsURLConnection connection, String... headers) {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Access-Control-Allow-Origin", "*");

        for (String header : headers) {
            responseHeaders.put(header, connection.getHeaderField(header));
        }

        return responseHeaders;
    }

}
