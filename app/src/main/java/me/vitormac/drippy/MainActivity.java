package me.vitormac.drippy;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.setContentView(R.layout.activity_main);
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

}
