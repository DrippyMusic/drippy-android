package me.vitormac.drippy.wrapper;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class RemoteWrapper {

    private static final OkHttpClient CLIENT = new OkHttpClient();

    private RemoteWrapper() {
    }

    public static WebResourceResponse request(WebResourceRequest request) {
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

}
