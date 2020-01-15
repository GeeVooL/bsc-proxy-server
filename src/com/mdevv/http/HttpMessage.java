package com.mdevv.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HttpMessage {

  public HttpMessage(List<String> lines) {
    this.lines = lines;
    this.lines.removeIf(l -> l.startsWith("Accept-Encoding"));

    this.lines.subList(1, lines.size() - 1).forEach(l -> {
      String[] parts = l.toLowerCase().split(": ");
      this.headers.put(parts[0], parts[1]);
    });
  }

  public String getHeader(String header) {
    return headers.get(header.toLowerCase());
  }

  public String getFormatted() {
    StringBuilder builder = new StringBuilder();
    lines.forEach(line -> builder.append(line).append("\r\n"));
    builder.append("\r\n");
    return builder.toString();
  }

  ;

  protected List<String> lines;
  protected Map<String, String> headers = new HashMap<>();
}
