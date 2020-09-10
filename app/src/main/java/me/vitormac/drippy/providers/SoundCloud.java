package me.vitormac.drippy.providers;

import android.webkit.WebResourceResponse;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SoundCloud extends ProviderBase {

    public SoundCloud(JsonObject data) {
        super(data);
    }

    @Override
    public WebResourceResponse stream(String range) throws IOException {
        String uri = this.data.get("uri").getAsString();
        HttpsURLConnection connection = (HttpsURLConnection) new URL(uri).openConnection();

        connection.setRequestProperty("Range", range);
        if (connection.getResponseCode() == HttpsURLConnection.HTTP_PARTIAL) {
            Map<String, String> headers = ProviderBase.getHeaders(connection,
                    "Content-Length", "Content-Range");

            return new WebResourceResponse("audio/mpeg", null, 206,
                    "Partial Content", headers, connection.getInputStream());
        }

        return new WebResourceResponse(null, null, null);
    }

}
