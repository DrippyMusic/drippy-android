package me.vitormac.drippy;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

import me.vitormac.drippy.model.LoaderTask;
import me.vitormac.drippy.model.MediaManager;
import me.vitormac.drippy.model.Track;

public class NowPlaying extends AppCompatActivity implements Runnable, Player.EventListener {

    private Player player;
    private ImageButton playButton;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        this.playButton = this.findViewById(R.id.player_play);
        this.player = MediaManager.getInstance().getPlayer();

        this.player.addListener(this);
        this.playButton.setImageDrawable(this.getButtonIcon(this.player.isPlaying()));
        if (this.player.isPlaying()) {
            this.update((Track) this.player.getCurrentTag());
            this.handler.post(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.player.removeListener(this);
        this.handler.removeCallbacks(this);
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        this.playButton.setImageDrawable(this.getButtonIcon(isPlaying));
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        if (reason < Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT) {
            this.update((Track) this.player.getCurrentTag());
        }
    }

    @Override
    public void run() {
        ((SeekBar) this.findViewById(R.id.player_progress))
                .setProgress((int) (this.player.getCurrentPosition() / 1000L));
        this.handler.postDelayed(this, 1000);
    }

    private void update(Track track) {
        ((SeekBar) this.findViewById(R.id.player_progress)).setMax(track.getDuration());
        ((TextView) this.findViewById(R.id.player_title)).setText(track.getTitle());
        ((TextView) this.findViewById(R.id.player_album)).setText(track.getAlbum());
        new LoaderTask(this.findViewById(R.id.player_artwork)).execute(track.getArtwork(0));
    }

    private Drawable getButtonIcon(boolean playing) {
        if (playing) {
            return this.getDrawable(R.drawable.ic_pause);
        }
        return this.getDrawable(R.drawable.ic_play_arrow);
    }

    public void previous(View view) {
        this.player.previous();
    }

    public void toggle(View view) {
        MediaManager.getInstance().toggle();
    }

    public void next(View view) {
        this.player.next();
    }

}