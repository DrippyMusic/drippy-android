package me.vitormac.drippy;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.core.app.NotificationCompat;

import com.google.android.exoplayer2.Player;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.vitormac.drippy.model.MediaManager;
import me.vitormac.drippy.model.Track;

public class JavascriptBridge implements Player.EventListener {

    private final WebView view;
    private final Activity activity;
    private final NotificationManager manager;
    private final NotificationCompat.Builder builder;

    JavascriptBridge(WebView view, Activity activity) {
        this.view = view;
        this.activity = activity;
        this.manager = activity.getSystemService(NotificationManager.class);
        this.builder = new NotificationCompat.Builder(activity, "drippy").setOngoing(true)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("drippy", "player", NotificationManager.IMPORTANCE_DEFAULT);
            this.manager.createNotificationChannel(channel);
        }

        MediaManager.getInstance().getPlayer().addListener(this);
    }

    @JavascriptInterface
    public void play(String idToken, String refreshToken, int index, String trackList) {
        List<Track> tracks = new ArrayList<>();
        for (JsonElement element : JsonParser.parseString(trackList).getAsJsonArray()) {
            tracks.add(new Track(element.getAsJsonObject()));
        }

        this.activity.runOnUiThread(() -> MediaManager.getInstance().play(this.activity, idToken, tracks, index));
    }

    @JavascriptInterface
    public void toggle() {
        this.activity.runOnUiThread(() -> MediaManager.getInstance().toggle());
    }

    @JavascriptInterface
    public void open() {
        this.activity.startActivity(new Intent(this.activity, NowPlaying.class));
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        this.emit("state", isPlaying);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_READY && playWhenReady) {
            this.emit("started", MediaManager.getInstance().getPlayer().getCurrentWindowIndex());
        }
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        if (reason < Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT) {
            this.emit("started", MediaManager.getInstance().getPlayer().getCurrentWindowIndex());
        }
    }

    private void emit(String name, Object value) {
        this.view.post(() -> {
            String args = String.valueOf(value);
            if (value instanceof String) args = String.format("'%s'", String.valueOf(value));
            String event = String.format("window.native.event.emit('%s', %s)", name, args);
            this.view.evaluateJavascript(event, null);
        });
    }

}
