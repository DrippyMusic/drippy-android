package me.vitormac.drippy;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.webkit.JavascriptInterface;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.exoplayer2.Player;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import me.vitormac.drippy.model.LoaderTask;
import me.vitormac.drippy.model.MediaManager;
import me.vitormac.drippy.model.Track;

public class JavascriptBridge {

    private final Activity activity;
    private final NotificationManager manager;
    private final NotificationCompat.Builder builder;

    JavascriptBridge(AppCompatActivity activity) {
        this.activity = activity;
        this.manager = activity.getSystemService(NotificationManager.class);
        this.builder = new NotificationCompat.Builder(activity, "drippy").setOngoing(true)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("drippy", "player", NotificationManager.IMPORTANCE_DEFAULT);
            this.manager.createNotificationChannel(channel);
        }

        ImageButton button = this.activity.findViewById(R.id.play_button);
        MediaManager.getInstance().getPlayer().addListener(new Player.EventListener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                activity.runOnUiThread(() -> {
                    button.setImageDrawable(activity.getDrawable(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play_arrow));
                });
            }
        });
    }

    @JavascriptInterface
    public void play(String idToken, String refreshToken, int index, String trackList) {
        List<Track> tracks = new ArrayList<>();
        for (JsonElement element : JsonParser.parseString(trackList).getAsJsonArray()) {
            tracks.add(new Track(element.getAsJsonObject()));
        }

        this.activity.runOnUiThread(() -> {
            Track track = tracks.get(index);
            MediaManager.getInstance().play(this.activity, idToken, tracks, index);
            ((TextView) this.activity.findViewById(R.id.title)).setText(track.getTitle());
            ((TextView) this.activity.findViewById(R.id.artists)).setText(track.getArtists());
            new LoaderTask(this.activity.findViewById(R.id.artwork)).execute(track.getArtwork(2));
        });
    }

}
