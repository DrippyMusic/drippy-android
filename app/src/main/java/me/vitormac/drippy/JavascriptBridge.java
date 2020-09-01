package me.vitormac.drippy;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.android.exoplayer2.Player;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import me.vitormac.drippy.model.MediaManager;
import me.vitormac.drippy.model.Track;

public class JavascriptBridge implements Player.EventListener {

    private final WebView view;
    private final Activity activity;

    JavascriptBridge(WebView view, Activity activity) {
        this.view = view;
        this.activity = activity;
        MediaManager.getInstance().getPlayer().addListener(this);
    }

    @JavascriptInterface
    public void setData(String idToken, String refreshToken) {
        MediaManager.getInstance().setIdToken(idToken);
        MediaManager.getInstance().setRefreshToken(refreshToken);
    }

    @JavascriptInterface
    public void load(String trackList) {
        List<Track> tracks = new ArrayList<>();
        for (JsonElement element : JsonParser.parseString(trackList).getAsJsonArray()) {
            tracks.add(new Track(element.getAsJsonObject()));
        }

        this.activity.runOnUiThread(() -> MediaManager.getInstance().prepare(this.activity, tracks));
    }

    @JavascriptInterface
    public void play(int index) {
        this.activity.runOnUiThread(() -> MediaManager.getInstance().play(index));
    }

    @JavascriptInterface
    public void toggle() {
        this.activity.runOnUiThread(() -> MediaManager.getInstance().toggle());
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
