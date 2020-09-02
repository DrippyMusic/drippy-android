package me.vitormac.drippy.bootstrap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AutoUpdater extends AsyncTask<String, Void, Void> {

    private final File dist;
    private final SharedPreferences preferences;
    private final Runnable runnable;
    private final OkHttpClient client = new OkHttpClient();

    public AutoUpdater(Activity activity, File dist, Runnable runnable) {
        this.dist = dist;
        this.preferences = activity.getPreferences(Context.MODE_PRIVATE);
        this.runnable = runnable;
        if (!this.dist.exists())
            this.dist.mkdir();
    }

    @Override
    protected Void doInBackground(String... strings) {
        boolean update;
        String assetUrl;
        Request request = new Request.Builder()
                .url(strings[0]).build();

        try (Response response = this.client.newCall(request).execute()) {
            JsonObject object = JsonParser.parseString(response.body().string())
                    .getAsJsonObject();
            String latest = object.get("tag_name").getAsString();
            assetUrl = object.get("assets").getAsJsonArray().get(0)
                    .getAsJsonObject().get("browser_download_url").getAsString();

            if (this.preferences.contains("version")) {
                String version = AutoUpdater.this.preferences.getString("version", latest);
                update = !latest.equals(version);
            } else {
                this.preferences.edit().putString("version", latest).apply();
                update = true;
            }
        } catch (IOException ex) {
            return null;
        }

        if (update && !StringUtils.isEmpty(assetUrl)) {
            try (ZipInputStream stream = new ZipInputStream(new URL(assetUrl).openStream())) {
                byte[] buffer = new byte[2048];
                for (ZipEntry entry; (entry = stream.getNextEntry()) != null; ) {
                    File file = new File(this.dist.getAbsolutePath(), entry.getName());
                    if (entry.isDirectory() && file.mkdir())
                        continue;

                    try (FileOutputStream output = new FileOutputStream(file)) {
                        for (int i; (i = stream.read(buffer)) > 0; )
                            output.write(buffer, 0, i);
                    }
                }
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void e) {
        this.runnable.run();
    }

}