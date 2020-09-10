package me.vitormac.drippy.providers;

import android.webkit.WebResourceResponse;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import me.vitormac.drippy.providers.model.DataModel;

public class CacheProvider extends ProviderBase<DataModel> {

    private final File file;

    public CacheProvider(String id) {
        super(null, id);
        this.file = ProviderBase.getCache(this.id);
    }

    @Override
    public WebResourceResponse stream(String range) throws IOException {
        Map<String, String> headers = new HashMap<String, String>() {{
            put("Access-Control-Allow-Origin", "*");
        }};

        return new WebResourceResponse("application/octet-stream", null,
                200, "OK", headers, new FileInputStream(this.file));
    }

    @Override
    protected String getMimeType() {
        return null;
    }

    @Override
    protected DataModel map(JsonObject object) {
        return null;
    }

}
