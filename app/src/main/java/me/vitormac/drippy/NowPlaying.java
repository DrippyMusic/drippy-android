package me.vitormac.drippy;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.Player;

import me.vitormac.drippy.model.LoaderTask;
import me.vitormac.drippy.model.MediaManager;
import me.vitormac.drippy.model.Track;

public class NowPlaying extends AppCompatActivity implements Runnable, Player.EventListener {

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        MediaManager.getInstance().getPlayer().addListener(this);

        if (MediaManager.getInstance().getCurrent() != null) {
            Track track = MediaManager.getInstance().getCurrent();
            ((SeekBar) this.findViewById(R.id.player_progress)).setMax(track.getDuration());
            ((TextView) this.findViewById(R.id.player_title)).setText(track.getTitle());
            ((TextView) this.findViewById(R.id.player_album)).setText(track.getAlbum());
            new LoaderTask(this.findViewById(R.id.player_artwork)).execute(track.getArtwork(0));
            this.handler.post(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.getInstance().getPlayer().removeListener(this);
        this.handler.removeCallbacks(this);
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        ImageButton button = this.findViewById(R.id.player_play);
        button.setImageDrawable(this.getDrawable(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play_arrow));
    }

    @Override
    public void run() {
        Player player = MediaManager.getInstance().getPlayer();
        ((SeekBar) this.findViewById(R.id.player_progress)).setProgress((int) (player.getCurrentPosition() / 1000L));
        this.handler.postDelayed(this, 1000);
    }

    public void toggle(View view) {
        MediaManager.getInstance().toggle();
    }

}
