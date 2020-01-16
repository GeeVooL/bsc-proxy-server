package com.mdevv.http;

import java.util.List;

public class HttpResponse extends HttpMessage {

  public HttpResponse(List<String> lines) {
    super(lines);

    String statusLine = this.lines.get(0);
    String[] parts = statusLine.split(" ");
    statusCode = Integer.parseInt(parts[1]);
    String resourcePath = parts[2];
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void modifyHeader(String header, String newValue) {
    String lowerHeader = header.toLowerCase();
    lines.removeIf(line -> line.toLowerCase().startsWith(lowerHeader));
    lines.add(header + ": " + newValue);
    headers.replace(lowerHeader, newValue);
  }

  private int statusCode;
}
