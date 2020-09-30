package me.vitormac.drippy.providers;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import me.vitormac.drippy.providers.model.DataModel;

public abstract class ProviderBase<T extends DataModel> {

    protected final T data;
    protected final String id;

    public ProviderBase(JsonObject data, String id) {
        this.data = this.map(data);
        this.id = id;
    }

    public final InputStream stream() throws IOException {
        URL url = new URL(this.data.getUri());
        return this.getInputStream(url.openConnection());
    }

    protected InputStream getInputStream(URLConnection connection) throws IOException {
        return connection.getInputStream();
    }

    protected abstract T map(JsonObject object);

    public static File getCache(String id) {
        File file = new File(System.getProperty("data.dir"), "data");
        if (file.exists() || file.mkdir()) {
            return new File(file, id);
        }

        throw new RuntimeException("No data dir");
    }

}
