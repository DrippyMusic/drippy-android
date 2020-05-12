package me.vitormac.drippy;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import me.vitormac.drippy.model.MediaManager;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MediaManager.create(this);

        this.webView = new WebView(this);
        this.webView.setWebViewClient(new WebViewClient());
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.addJavascriptInterface(new JavascriptBridge(this.webView, this), "native");
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.loadUrl("https://drippy.live");
        this.setContentView(this.webView);
    }

    @Override
    public void onBackPressed() {
        if (this.webView.canGoBack()) this.webView.goBack();
        else super.onBackPressed();
    }

}
