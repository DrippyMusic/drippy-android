package me.vitormac.drippy.webview;

import android.content.Context;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.webkit.WebViewAssetLoader;

import java.io.File;

public class DrippyClient extends WebViewClient {

    private final File dist;
    private final WebViewAssetLoader loader;

    public DrippyClient(Context context) {
        super();
        this.dist = new File(context.getFilesDir().getAbsolutePath() + "/dist");
        this.loader = new WebViewAssetLoader.Builder().setDomain("drippy.live")
                .addPathHandler("/", new RouteHandler(dist)).build();
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return loader.shouldInterceptRequest(request.getUrl());
    }

    public File getDist() {
        return dist;
    }

}
