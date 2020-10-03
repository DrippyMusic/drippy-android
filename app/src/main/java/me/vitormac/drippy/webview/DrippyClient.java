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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;

public final class DrippyClient extends WebViewClient {

    private final File dist;
    private final WebViewAssetLoader loader;

    private final Context context;
    private final Drippy drippy = Drippy.getInstance();

    public DrippyClient(Context context) {
        super();
        this.context = context;
        this.dist = new File(context.getFilesDir().getAbsolutePath() + "/dist");
        this.loader = new WebViewAssetLoader.Builder().setDomain("drippy.live")
                .addPathHandler("/", new RouteHandler(dist)).build();
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (StringUtils.equals(request.getUrl().getHost(), InternalServer.API_HOST)
                && StringUtils.equalsAny(request.getMethod(), "GET", "OPTIONS")) {
            try {
                return this.drippy.request(this.isConnected(), request);
            } catch (IOException ex) {
                return super.shouldInterceptRequest(view, request);
            }
        }

        return loader.shouldInterceptRequest(request.getUrl());
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public File getDist() {
        return dist;
    }

    static class RouteHandler implements WebViewAssetLoader.PathHandler {

        private final File root;
        private final File index;

        public RouteHandler(File root) {
            this.root = root;
            this.index = new File(root, "index.html");
        }

        @Override
        public WebResourceResponse handle(String path) {
            File object = new File(this.root, path);
            try {
                if (StringUtils.isEmpty(path) || !object.exists()) {
                    return new WebResourceResponse("text/html", null,
                            new BufferedInputStream(new FileInputStream(this.index)));
                }

                return new WebResourceResponse(this.guessMimeType(path), null,
                        new BufferedInputStream(new FileInputStream(object)));
            } catch (IOException e) {
                return new WebResourceResponse(null, null, null);
            }
        }

        private String guessMimeType(String file) {
            String mimeType = URLConnection.guessContentTypeFromName(file);
            return StringUtils.defaultIfEmpty(mimeType, "text/plain");
        }

    }

}
