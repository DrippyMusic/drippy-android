package me.vitormac.drippy.providers;

import android.webkit.WebResourceResponse;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public abstract class ProviderBase {

    protected final JsonObject data;

    public ProviderBase(JsonObject data) {
        this.data = data;
    }

    public abstract WebResourceResponse stream(String range) throws IOException;

    protected static Map<String, String> getHeaders(HttpsURLConnection connection, String... headers) {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Access-Control-Allow-Origin", "*");

        for (String header : headers) {
            responseHeaders.put(header, connection.getHeaderField(header));
        }

        return responseHeaders;
    }

}
