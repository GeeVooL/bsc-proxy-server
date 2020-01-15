package com.mdevv.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HttpRequest extends HttpMessage {

  public HttpRequest(List<String> lines) throws MalformedURLException {
    super(lines);

    String statusLine = this.lines.get(0);
    String[] parts = statusLine.split(" ");
    method = parts[0];
    String rawUrl = parts[1];
    url = new URL(rawUrl);
  }

  public String getMethod() {
    return method;
  }

  public URL getUrl() {
    return url;
  }

  private String method;
  private URL url;
}
