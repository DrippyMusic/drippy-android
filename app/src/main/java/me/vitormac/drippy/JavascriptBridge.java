package me.vitormac.drippy;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.webkit.JavascriptInterface;

import androidx.core.app.NotificationCompat;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import me.vitormac.drippy.model.MediaManager;
import me.vitormac.drippy.model.Track;

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
    public void play(String idToken, String refreshToken, int index, String trackList) {
        List<Track> tracks = new ArrayList<>();
        for (JsonElement element : JsonParser.parseString(trackList).getAsJsonArray()) {
            tracks.add(new Track(element.getAsJsonObject()));
        }

        this.activity.runOnUiThread(() -> MediaManager.getInstance().play(this.activity, idToken, tracks, index));
    }

}
