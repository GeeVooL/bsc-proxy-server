package com.mdevv.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class HttpRequest extends HttpMessage {

  public HttpRequest(List<String> lines) throws MalformedURLException {
    super(lines);

    String statusLine = this.lines.get(0);
    String[] parts = statusLine.split(" ");
    method = parts[0];
    String rawUrl = parts[1];
    url = new URL(rawUrl);
    calculateResourceId();
  }

  public String getMethod() {
    return method;
  }

  public URL getUrl() {
    return url;
  }

  public String getResourceId() {
    return resourceId;
  }

  private void calculateResourceId() {
    StringBuilder stringBuilder = new StringBuilder();
    String filePath = url.getPath().length() > 1 ? url.getPath() : "root";
    stringBuilder.append(url.getHost());
    stringBuilder.append(url.getPath());
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] hash = md.digest(stringBuilder.toString().getBytes());
      resourceId = bytesToHex(hash) + Paths.get(filePath).getFileName().toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }

  // Source: https://stackoverflow.com/a/9855338
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  private static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  private String method;
  private URL url;
  private String resourceId;
}
