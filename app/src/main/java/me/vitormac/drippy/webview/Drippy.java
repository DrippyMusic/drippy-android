package me.vitormac.drippy.webview;

import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
import java.util.Objects;
import java.util.UUID;

import me.vitormac.drippy.providers.ProviderBase;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

final class Drippy {

    private static final String API_URL = "https://api.drippy.live";

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

    private ProviderBase<?> session;
    private final OkHttpClient client = new OkHttpClient();

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
        Request.Builder builder = DrippyUtils.builder(request);

        if (connected && !StringUtils.equalsAny(path, "/stream", "/audio")) {
            Response response = this.client.newCall(builder.build()).execute();

            Map<String, String> headers = new HashMap<>();
            for (String header : response.headers().names()) {
                headers.put(header, response.header(header));
            }

            ResponseBody body = Objects.requireNonNull(response.body());
            return new WebResourceResponse(String.valueOf(body.contentType()), null,
                    response.code(), response.message(), headers, body.byteStream());
        }

        if (!StringUtils.isEmpty(path)) {
            switch (path) {
                case "/validate":
                    return new WebResourceResponse(null, null,
                            200, "OK", DEFAULT_HEADERS, null);
                case "/stream":
                    return this.session(builder, request.getUrl(), connected);
                case "/audio":
                    String range = request.getRequestHeaders().get("Range");
                    return this.stream(StringUtils.defaultIfEmpty(range, "bytes=0-"));
            }
        }

        return null;
    }

    private WebResourceResponse session(Request.Builder request, Uri uri, boolean connected)
            throws IOException {
        List<String> segments = new ArrayList<>(uri.getPathSegments());
        segments.set(0, "data");

        String id = segments.get(segments.size() - 1);
        boolean cache = ProviderBase.getCache(id).exists();
        try (PipedOutputStream stream = new PipedOutputStream();
             Writer writer = new OutputStreamWriter(stream)) {
            if (cache) {
                this.session = DrippyUtils.getProvider(id);
            } else if (connected) {
                JsonObject data = this.rsession(request, segments);
                this.session = DrippyUtils.getProvider(data, id);
            } else return null;

            JsonObject object = new JsonObject();
            object.addProperty("session",
                    UUID.randomUUID().toString());
            writer.write(object.toString());

            return new WebResourceResponse("application/json", null,
                    200, "OK", DEFAULT_HEADERS, new PipedInputStream(stream));
        }
    }

    private JsonObject rsession(Request.Builder request, List<String> segments)
            throws IOException {
        request.url(API_URL + '/' + StringUtils.join(segments, '/'));
        try (Response response = this.client.newCall(request.build()).execute()) {
            ResponseBody body = Objects.requireNonNull(response.body());
            return JsonParser.parseString(body.string()).getAsJsonObject();
        }
    }

    private WebResourceResponse stream(String range) throws IOException {
        ProviderBase<?> provider = Objects.requireNonNull(this.session);
        return provider.stream(range);
    }

}
