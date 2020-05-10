package me.vitormac.drippy.model;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class MediaManager {


    private static MediaManager INSTANCE;

    public static MediaManager getInstance() {
        return MediaManager.INSTANCE;
    }

    public static MediaManager create(Context context) {
        return MediaManager.INSTANCE = new MediaManager(context.getApplicationContext());
    }

    private final SimpleExoPlayer player;

    private MediaManager(Context context) {
        this.player = new SimpleExoPlayer.Builder(context).build();
    }

    public void play(Context context, String idToken, List<Track> tracks, int index) {
        ConcatenatingMediaSource sources = new ConcatenatingMediaSource();
        DataSource.Factory factory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "drippy"));
        for (Track track : tracks) {
            MediaSource source = new ProgressiveMediaSource.Factory(factory)
                    .setTag(track).createMediaSource(Uri.parse(track.getStreamURL(idToken)));
            sources.addMediaSource(source);
        }

        this.player.stop(true);
        this.player.prepare(sources);
        this.player.setPlayWhenReady(true);
        this.player.seekToDefaultPosition(index);
    }

    public boolean toggle() {
        this.player.setPlayWhenReady(!this.player.isPlaying());
        return this.player.isPlaying();
    }

    public ExoPlayer getPlayer() {
        return player;
    }

}
