package me.vitormac.drippy.model;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.ResolvingDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class MediaManager {

    private static final String API_URL = "https://api.drippy.live";

    private static MediaManager INSTANCE;

    public static MediaManager getInstance() {
        return MediaManager.INSTANCE;
    }

    public static void create(Context context) {
        MediaManager.INSTANCE = new MediaManager(context.getApplicationContext());
    }

    private final SimpleExoPlayer player;
    private final OkHttpClient client = new OkHttpClient();

    private String idToken, refreshToken;

    private MediaManager(Context context) {
        this.player = new SimpleExoPlayer.Builder(context).build();
    }

    public void prepare(Context context, List<Track> tracks) {
        String userAgent = Util.getUserAgent(context, "drippy");
        ConcatenatingMediaSource sources = new ConcatenatingMediaSource();

        for (Track track : tracks) {
            DataSource.Factory factory = new ResolvingDataSource.Factory(
                    new DefaultHttpDataSourceFactory(userAgent), (DataSpec spec) -> {
                Request request = new Request.Builder().url(spec.uri.toString())
                        .header("User-Token", this.idToken).build();

                try (Response response = this.client.newCall(request).execute()) {
                    String digest = JsonParser.parseString(response.body().string())
                            .getAsJsonObject().get("digest").getAsString();
                    return spec.withUri(Uri.parse(String.format("%s/audio/%s", API_URL, digest)));
                } catch (IOException ex) {
                    return spec;
                }
            });

            MediaSource media = new ProgressiveMediaSource.Factory(factory).setTag(track)
                    .createMediaSource(Uri.parse(String.format("%s/stream/%s", API_URL, track.getId())));
            sources.addMediaSource(media);
        }

        this.player.stop(true);
        this.player.prepare(sources);
    }

    public void play(int index) {
        this.player.setPlayWhenReady(true);
        this.player.seekToDefaultPosition(index);
    }

    public void toggle() {
        this.player.setPlayWhenReady(!this.player.isPlaying());
    }

    public ExoPlayer getPlayer() {
        return player;
    }

    public void setIdToken(String token) {
        this.idToken = token;
    }

    public void setRefreshToken(String token) {
        this.refreshToken = token;
    }

}
