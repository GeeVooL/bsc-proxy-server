package com.mdevv;

import java.net.MalformedURLException;
import java.net.URL;

public class HttpRequest {
    public HttpRequest(String rawRequest) throws MalformedURLException {
        this.raw = rawRequest;

        int firstSpace = rawRequest.indexOf(" ");
        method = rawRequest.substring(0, firstSpace);
        rawUrl = rawRequest.substring(firstSpace + 1, rawRequest.indexOf(" ", firstSpace + 1));
        url = new URL(rawUrl);
    }

    public String getMethod() {
        return method;
    }

    public String getRawUrl() {
        return rawUrl;
    }

    public URL getUrl() {
        return url;
    }

    public String getRaw() {
        return raw;
    }

    private String method;
    private String rawUrl;
    private URL url;
    private String raw;
}
