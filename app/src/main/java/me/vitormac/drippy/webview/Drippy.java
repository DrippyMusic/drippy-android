package me.vitormac.drippy.webview;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

final class Drippy {

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

    private Drippy() {
    }

    protected static WebResourceResponse request(boolean connected, WebResourceRequest request) {
        if (request.getMethod().equals("OPTIONS")) {
            return new WebResourceResponse(null, null,
                    204, "No Content", OPTIONS_HEADERS, null);
        }

        if (connected) {
            Request.Builder builder = new Request.Builder()
                    .url(request.getUrl().toString());
            for (Map.Entry<String, String> entry : request.getRequestHeaders().entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }

            try {
                Response response = CLIENT.newCall(builder.build()).execute();

                Map<String, String> headers = new HashMap<>();
                for (String header : response.headers().names()) {
                    headers.put(header, response.header(header));
                }

                ResponseBody body = Objects.requireNonNull(response.body());
                return new WebResourceResponse(String.valueOf(body.contentType()), null,
                        response.code(), response.message(), headers, body.byteStream());
            } catch (IOException ex) {
                return new WebResourceResponse(null, null, null);
            }
        }

        String path = request.getUrl().getPath();
        if (!StringUtils.isEmpty(path)) {
            switch (path) {
                case "/validate":
                    return new WebResourceResponse(null, null, 200,
                            "OK", DEFAULT_HEADERS, null);
            }
        }

        return new WebResourceResponse(null, null, null);
    }

}
