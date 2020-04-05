package me.vitormac.drippy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavascriptBridge(this), "native");
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl("https://drippy-music.github.io/");
        setContentView(webView);
    }
}
