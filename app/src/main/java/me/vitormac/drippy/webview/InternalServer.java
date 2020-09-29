package me.vitormac.drippy.webview;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import okhttp3.OkHttpClient;

public final class InternalServer extends NanoHTTPD {

    public static final int PORT = 5627;
    private static final String API_URL = "https://api.drippy.live";

    private final OkHttpClient client = new OkHttpClient();

    public InternalServer() {
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
        return super.serve(session);
    }


}
