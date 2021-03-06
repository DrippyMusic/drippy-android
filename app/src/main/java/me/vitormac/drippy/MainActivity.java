package me.vitormac.drippy;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import me.vitormac.drippy.bootstrap.AutoUpdater;
import me.vitormac.drippy.webview.DrippyClient;

public class MainActivity extends AppCompatActivity {

    private static final String REPO_URL = "https://api.github.com/repos/DrippyMusic/drippy.live/releases/latest";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.setProperty("cache.dir", getCacheDir().getAbsolutePath());
        System.setProperty("native.dir", getApplicationInfo().nativeLibraryDir);

        DrippyClient client = new DrippyClient(this);
        new AutoUpdater(this, client.getDist(), () -> {
            this.webView = new WebView(this);
            this.webView.setWebViewClient(client);
            this.webView.getSettings().setJavaScriptEnabled(true);
            this.webView.getSettings().setDomStorageEnabled(true);
            this.webView.loadUrl("https://drippy.live");
            this.setContentView(this.webView);
        }).execute(REPO_URL);
    }

    @Override
    public void onBackPressed() {
        if (this.webView.canGoBack()) this.webView.goBack();
        else super.onBackPressed();
    }

}
