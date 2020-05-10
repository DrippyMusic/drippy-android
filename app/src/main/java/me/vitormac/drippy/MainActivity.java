package me.vitormac.drippy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.Player;

import me.vitormac.drippy.model.LoaderTask;
import me.vitormac.drippy.model.MediaManager;
import me.vitormac.drippy.model.Track;

public class MainActivity extends AppCompatActivity implements Player.EventListener {

    private Player player;
    private WebView webView;
    private ImageButton playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.setContentView(R.layout.activity_main);
        this.player = MediaManager.create(this).getPlayer();
        this.player.addListener(this);

        this.webView = this.findViewById(R.id.webview);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.addJavascriptInterface(new JavascriptBridge(this), "native");
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.loadUrl("https://drippy.live");

        this.playButton = this.findViewById(R.id.play_button);
    }

    @Override
    public void onBackPressed() {
        if (this.webView.canGoBack()) this.webView.goBack();
        else super.onBackPressed();
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        this.playButton.setImageDrawable(Utils.getButtonIcon(this, isPlaying));
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_READY && playWhenReady) {
            this.update((Track) this.player.getCurrentTag());
        }
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        if (this.player.getPlaybackState() == Player.STATE_READY && reason < Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT) {
            this.update((Track) this.player.getCurrentTag());
        }
    }

    private void update(Track track) {
        ((TextView) this.findViewById(R.id.title)).setText(track.getTitle());
        ((TextView) this.findViewById(R.id.artists)).setText(track.getArtists());
        new LoaderTask(this.findViewById(R.id.artwork)).execute(track.getArtwork(2));
    }

    public void openPlayer(View view) {
        this.startActivity(new Intent(this, NowPlaying.class));
    }

    public void toggle(View view) {
        MediaManager.getInstance().toggle();
    }

}
