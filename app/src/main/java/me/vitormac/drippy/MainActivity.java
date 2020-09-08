package me.vitormac.drippy;

import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewAssetLoader;

import java.io.File;

import me.vitormac.drippy.bootstrap.AutoUpdater;
import me.vitormac.drippy.model.MediaManager;
import me.vitormac.drippy.webview.RouteHandler;

public class MainActivity extends AppCompatActivity {

    private static final String REPO_URL = "https://api.github.com/repos/DrippyMusic/drippy.live/releases/latest";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MediaManager.create(this);

        File dist = new File(this.getFilesDir().getAbsolutePath() + "/dist");
        WebViewAssetLoader loader = new WebViewAssetLoader.Builder().setDomain("drippy.live")
                .addPathHandler("/", new RouteHandler(dist)).build();

        new AutoUpdater(this, dist, () -> {
            this.webView = new WebView(this);
            this.webView.setWebViewClient(new WebViewClient() {
                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    return loader.shouldInterceptRequest(request.getUrl());
                }
            });

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
