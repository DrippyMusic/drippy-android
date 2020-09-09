package me.vitormac.drippy.wrapper;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public final class LocalWrapper {

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

    private LocalWrapper() {
    }

    public static WebResourceResponse request(WebResourceRequest request) {
        if (request.getMethod().equals("OPTIONS")) {
            return new WebResourceResponse(null, null,
                    204, "No Content", OPTIONS_HEADERS, null);
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
