package me.vitormac.drippy.webview;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fi.iki.elonen.NanoHTTPD;
import me.vitormac.drippy.codec.AudioFormat;
import me.vitormac.drippy.codec.FFmpeg;
import me.vitormac.drippy.providers.CacheableInputStream;
import me.vitormac.drippy.providers.ProviderBase;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

final class InternalServer extends NanoHTTPD {

    protected static final int PORT = 5627;
    protected static final String API_HOST = "api.drippy.live";
    protected static final String API_URL = "https://" + API_HOST + '/';

    private final OkHttpClient client = new OkHttpClient();

    protected InternalServer() {
        super("127.0.0.1", InternalServer.PORT);
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            return this.request(session);
        } catch (IOException e) {
            return super.serve(session);
        }
    }

    private Response request(IHTTPSession session) throws IOException {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.containsKey("id")) {
            String id = parameters.get("id").get(0);

            File file = DrippyUtils.getCache(id);
            if (file.exists()) {
                return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "audio/opus",
                        new FileInputStream(file), file.length());
            }

            Request.Builder request = new Request.Builder().url(API_URL + "data/" + id);
            try (okhttp3.Response response = this.client.newCall(request.build()).execute()) {
                ResponseBody body = Objects.requireNonNull(response.body());
                JsonObject data = JsonParser.parseString(body.string())
                        .getAsJsonObject();

                ProviderBase<?> provider = DrippyUtils.getProvider(data, id);
                FFmpeg ffmpeg = new FFmpeg(provider.stream(), AudioFormat.MP3);

                return NanoHTTPD.newChunkedResponse(Response.Status.OK, "audio/opus",
                        new CacheableInputStream(ffmpeg.stdout(), file));
            }
        }

        return super.serve(session);
    }


}
