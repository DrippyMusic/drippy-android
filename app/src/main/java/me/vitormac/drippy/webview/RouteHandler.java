package me.vitormac.drippy.webview;

import android.webkit.WebResourceResponse;

import androidx.webkit.WebViewAssetLoader;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;

public class RouteHandler implements WebViewAssetLoader.PathHandler {

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
