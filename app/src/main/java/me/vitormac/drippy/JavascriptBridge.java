package me.vitormac.drippy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.webkit.JavascriptInterface;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavascriptBridge {

    private static final String API_URL = "https://api.drippy.live";

    private final Activity activity;
    private final NotificationManager manager;
    private final NotificationCompat.Builder builder;
    private final MediaPlayer player = new MediaPlayer();

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

        ImageButton button = this.activity.findViewById(R.id.play_button);
        AudioAttributes attributes = new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
        this.player.setAudioAttributes(attributes);
        this.player.setOnPreparedListener(player -> {
            button.setImageDrawable(this.activity.getDrawable(R.drawable.ic_pause));
            player.start();
        });

        this.activity.findViewById(R.id.play_button).setOnClickListener(view -> {
            if (this.player.isPlaying()) {
                button.setImageDrawable(this.activity.getDrawable(R.drawable.ic_play_arrow));
                this.player.pause();
            } else {
                button.setImageDrawable(this.activity.getDrawable(R.drawable.ic_pause));
                this.player.start();
            }
        });
    }

    @JavascriptInterface
    public void play(String idToken, String refreshToken, String track, String trackList) throws IOException {
        JsonObject object = JsonParser.parseString(track).getAsJsonObject();
        String artwork = object.get("album").getAsJsonObject().get("images").getAsJsonArray()
                .get(1).getAsJsonObject().get("url").getAsString();

        List<String> artists = new ArrayList<>();
        for (JsonElement element : object.get("artists").getAsJsonArray()) {
            JsonObject artist = element.getAsJsonObject();
            artists.add(artist.get("name").getAsString());
        }

        this.activity.runOnUiThread(() -> {
            ((TextView) this.activity.findViewById(R.id.title)).setText(object.get("name").getAsString());
            ((TextView) this.activity.findViewById(R.id.artists)).setText(Joiner.on(',').join(artists));
            new LoaderTask(this.activity.findViewById(R.id.artwork)).execute(artwork);
        });

        if (this.player.isPlaying()) this.player.reset();
        this.player.setDataSource(this.activity, Uri.parse(Joiner.on('/').join(Arrays.asList(API_URL, "stream", idToken, object.get("id").getAsString()))));
        this.player.prepareAsync();
    }

    private static class LoaderTask extends AsyncTask<String, Void, Bitmap> {

        @SuppressLint("StaticFieldLeak")
        private final ImageView view;

        private LoaderTask(ImageView view) {
            this.view = view;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            try (BufferedInputStream stream = new BufferedInputStream(new URL(strings[0]).openStream())) {
                return BitmapFactory.decodeStream(stream);
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            this.view.setImageBitmap(bitmap);
        }

    }

}
