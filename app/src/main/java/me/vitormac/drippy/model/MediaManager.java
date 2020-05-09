package me.vitormac.drippy.model;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.common.base.Joiner;

import java.util.Arrays;

public final class MediaManager {

    private static final String API_URL = "https://api.drippy.live";
    private static MediaManager INSTANCE;

    public static MediaManager getInstance() {
        return MediaManager.INSTANCE;
    }

    public static void create(Context context) {
        MediaManager.INSTANCE = new MediaManager(context.getApplicationContext());
    }

    private Track current;
    private final SimpleExoPlayer player;

    private MediaManager(Context context) {
        this.player = new SimpleExoPlayer.Builder(context).build();
        this.player.setPlayWhenReady(true);
    }

    public void play(Context context, String idToken, Track track) {
        this.current = track;
        DataSource.Factory factory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "drippy"));
        DefaultExtractorsFactory extractor = new DefaultExtractorsFactory()
                .setConstantBitrateSeekingEnabled(true);
        MediaSource source = new ProgressiveMediaSource.Factory(factory, extractor).createMediaSource(
                Uri.parse(Joiner.on('/').join(Arrays.asList(API_URL, "stream", idToken, this.current.getId())))
        );

        this.player.stop(true);
        this.player.prepare(source);
    }

    public boolean toggle() {
        this.player.setPlayWhenReady(!this.player.isPlaying());
        return this.player.isPlaying();
    }

    public ExoPlayer getPlayer() {
        return player;
    }

    public Track getCurrent() {
        return current;
    }
}
