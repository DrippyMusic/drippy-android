package me.vitormac.drippy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import me.vitormac.drippy.model.MediaManager;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.setContentView(R.layout.activity_main);
        MediaManager.create(this);

        this.webView = this.findViewById(R.id.webview);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.addJavascriptInterface(new JavascriptBridge(this), "native");
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.loadUrl("https://drippy.live");
    }

    @Override
    public void onBackPressed() {
        if (this.webView.canGoBack()) this.webView.goBack();
        else super.onBackPressed();
    }

    public void openPlayer(View view) {
        this.startActivity(new Intent(this, NowPlaying.class));
    }

    public void toggle(View view) {
        MediaManager.getInstance().toggle();
    }

}
