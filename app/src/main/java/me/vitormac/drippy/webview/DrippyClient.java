package me.vitormac.drippy.webview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.webkit.WebViewAssetLoader;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

import me.vitormac.drippy.wrapper.LocalWrapper;
import me.vitormac.drippy.wrapper.RemoteWrapper;

public class DrippyClient extends WebViewClient {

    private final File dist;
    private final WebViewAssetLoader loader;

    private final Context context;

    public DrippyClient(Context context) {
        super();
        this.context = context;
        this.dist = new File(context.getFilesDir().getAbsolutePath() + "/dist");
        this.loader = new WebViewAssetLoader.Builder().setDomain("drippy.live")
                .addPathHandler("/", new RouteHandler(dist)).build();
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (StringUtils.equals(request.getUrl().getHost(), "api.drippy.live")
                && StringUtils.equalsAny(request.getMethod(), "GET", "OPTIONS")) {
            if (this.isConnected() && request.getMethod().equals("GET"))
                return RemoteWrapper.request(request);

            return LocalWrapper.request(request);
        }

        return loader.shouldInterceptRequest(request.getUrl());
    }

    public File getDist() {
        return dist;
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

}
