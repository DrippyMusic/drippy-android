package me.vitormac.drippy;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.webkit.JavascriptInterface;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

public class JavascriptBridge {

    private final Activity activity;
    private final NotificationManager manager;
    private final NotificationCompat.Builder builder;

    JavascriptBridge(Activity activity) {
        this.activity = activity;
        this.manager = activity.getSystemService(NotificationManager.class);
        this.builder = new NotificationCompat.Builder(activity, "drippy").setOngoing(true)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("drippy", "player", NotificationManager.IMPORTANCE_DEFAULT);
            this.manager.createNotificationChannel(channel);
        }
    }

    @JavascriptInterface
    public void setPlayerBar(String data) throws JSONException {
        JSONObject object = new JSONObject(data);
        String artist = String.valueOf(object.getJSONArray("artists").get(0));

        this.builder.setContentTitle(object.getString("title")).setContentText(artist);
        try (BufferedInputStream stream = new BufferedInputStream(new URL(object.getString("artwork_url")).openStream())) {
            Bitmap artwork = BitmapFactory.decodeStream(stream);
            this.builder.setLargeIcon(artwork);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        this.manager.notify(1, builder.build());
    }

}
