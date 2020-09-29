package me.vitormac.drippy.webview;

import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class Drippy {

    private static final Map<String, String> DEFAULT_HEADERS = new HashMap<String, String>() {{
        put("Access-Control-Allow-Origin", "*");
    }};

    private static final Map<String, String> OPTIONS_HEADERS = new HashMap<String, String>() {{
        put("Access-Control-Allow-Headers", "*");
        put("Access-Control-Allow-Methods", "*");
        put("Access-Control-Allow-Origin", "*");
        put("Vary", "Access-Control-Request-Headers");
        put("Content-Length", "0");
    }};

    private final static Drippy INSTANCE = new Drippy();

    protected static Drippy getInstance() {
        return INSTANCE;
    }

    private Drippy() {
    }

    protected WebResourceResponse request(boolean connected, WebResourceRequest request)
            throws IOException {
        if (request.getMethod().equals("OPTIONS")) {
            return new WebResourceResponse(null, null,
                    204, "No Content", OPTIONS_HEADERS, null);
        }

        String path = DrippyUtils.getRootPath(request.getUrl());
        if (connected && !path.equals("/stream")) {
            return null;
        }

        if (!StringUtils.isEmpty(path)) {
            switch (path) {
                case "/validate":
                    return new WebResourceResponse(null, null,
                            200, "OK", DEFAULT_HEADERS, null);
                case "/stream":
                    return this.session(request.getUrl());
            }
        }

        return null;
    }

    private WebResourceResponse session(Uri uri) throws IOException {
        List<String> segments = new ArrayList<>(uri.getPathSegments());
        String id = segments.get(segments.size() - 1);
        String url = "http://localhost:" + InternalServer.PORT + "/?id=" + id;

        try (PipedOutputStream stream = new PipedOutputStream();
             Writer writer = new OutputStreamWriter(stream)) {
            JsonObject object = new JsonObject();
            object.addProperty("uri", url);
            writer.write(object.toString());

            return new WebResourceResponse("application/json", null,
                    200, "OK", DEFAULT_HEADERS, new PipedInputStream(stream));
        }
    }

}
