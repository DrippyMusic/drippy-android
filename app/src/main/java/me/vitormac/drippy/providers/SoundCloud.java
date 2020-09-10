package me.vitormac.drippy.providers;

import android.webkit.WebResourceResponse;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import me.vitormac.drippy.providers.model.SoundCloudData;

public class SoundCloud extends ProviderBase<SoundCloudData> {

    public SoundCloud(JsonObject data) {
        super(data);
    }

    @Override
    public WebResourceResponse stream(String range) throws IOException {
        URL url = new URL(this.data.getUri());
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Range", range);

        Map<String, String> headers = ProviderBase.getHeaders(connection,
                "Content-Length", "Content-Range");
        return new WebResourceResponse("audio/mpeg", null, 206,
                "Partial Content", headers, connection.getInputStream());
    }

    @Override
    protected SoundCloudData map(JsonObject object) {
        SoundCloudData data = new SoundCloudData();
        data.setUri(object.get("uri").getAsString());
        return data;
    }

}
