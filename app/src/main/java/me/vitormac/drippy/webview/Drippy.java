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
    private static final OkHttpClient CLIENT = new OkHttpClient();

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

    private static final Map<String, ProviderBase> SESSIONS = new HashMap<>();

    private Drippy() {
    }

    protected static WebResourceResponse request(boolean connected, WebResourceRequest request)
            throws IOException {
        if (request.getMethod().equals("OPTIONS")) {
            return new WebResourceResponse(null, null,
                    204, "No Content", OPTIONS_HEADERS, null);
        }

        String path = DrippyUtils.getRootPath(request.getUrl());
        Request.Builder builder = DrippyUtils.builder(request);

        if (connected && !StringUtils.equalsAny(path, "/stream", "/audio")) {
            Response response = CLIENT.newCall(builder.build()).execute();

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
                    return Drippy.session(builder, request.getUrl());
                case "/audio":
                    String range = request.getRequestHeaders().get("Range");
                    return Drippy.stream(StringUtils.defaultIfEmpty(range, "bytes=0-"),
                            request.getUrl());
            }
        }

        return new WebResourceResponse(null, null, null);
    }

    private static WebResourceResponse session(Request.Builder request, Uri uri)
            throws IOException {
        List<String> segments = new ArrayList<>(uri.getPathSegments());
        segments.set(0, "data");

        request.url(API_URL + '/' + StringUtils.join(segments, '/'));
        try (Response response = CLIENT.newCall(request.build()).execute();
             PipedOutputStream stream = new PipedOutputStream();
             Writer writer = new OutputStreamWriter(stream)) {
            ResponseBody body = Objects.requireNonNull(response.body());
            JsonObject data = JsonParser.parseString(body.string()).getAsJsonObject();

            String session = UUID.randomUUID().toString();
            SESSIONS.put(session, DrippyUtils.getProvider(data));

            JsonObject object = new JsonObject();
            object.addProperty("session", session);
            writer.write(object.toString());

            return new WebResourceResponse("application/json", null,
                    200, "OK", DEFAULT_HEADERS, new PipedInputStream(stream));
        }
    }

    private static WebResourceResponse stream(String range, Uri uri)
            throws IOException {
        List<String> segments = new ArrayList<>(uri.getPathSegments());
        String session = segments.get(segments.size() - 1);

        ProviderBase provider = Objects.requireNonNull(SESSIONS.remove(session));
        return provider.stream(range);
    }

}
