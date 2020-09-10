package me.vitormac.drippy.webview;

import android.net.Uri;
import android.webkit.WebResourceRequest;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import me.vitormac.drippy.providers.Deezer;
import me.vitormac.drippy.providers.ProviderBase;
import me.vitormac.drippy.providers.SoundCloud;
import okhttp3.Request;

class DrippyUtils {

    protected static String getRootPath(Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments.isEmpty()) return "/";

        return "/" + segments.get(0);
    }

    protected static Request.Builder builder(WebResourceRequest request) {
        Request.Builder builder = new Request.Builder()
                .url(request.getUrl().toString());
        for (Map.Entry<String, String> entry : request.getRequestHeaders().entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        return builder;
    }

    protected static ProviderBase<?> getProvider(JsonObject object) {
        switch (object.get("id").getAsInt()) {
            case 0:
                return new SoundCloud(object);
            case 1:
                return new Deezer(object);
            default:
                throw new IllegalArgumentException("Invalid provider");
        }
    }

}
