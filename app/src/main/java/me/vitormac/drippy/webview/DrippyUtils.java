package me.vitormac.drippy.webview;

import android.net.Uri;

import com.google.gson.JsonObject;

import java.io.File;
import java.util.List;

import me.vitormac.drippy.providers.ProviderBase;
import me.vitormac.drippy.providers.impl.Deezer;
import me.vitormac.drippy.providers.impl.SoundCloud;

class DrippyUtils {

    public static File getCache(String id) {
        File file = new File(System.getProperty("cache.dir"), "audio");
        if (file.exists() || file.mkdir()) {
            return new File(file, id);
        }

        throw new RuntimeException("Cache directory not found");
    }

    protected static String getRootPath(Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments.isEmpty()) return "/";

        return "/" + segments.get(0);
    }

    protected static ProviderBase<?> getProvider(JsonObject object, String id) {
        switch (object.get("id").getAsInt()) {
            case 0:
                return new SoundCloud(object, id);
            case 1:
                return new Deezer(object, id);
            default:
                throw new IllegalArgumentException("Invalid provider");
        }
    }

}
