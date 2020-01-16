package com.mdevv.handlers;

import com.mdevv.components.CacheManager;
import com.mdevv.http.HttpRequest;
import com.mdevv.http.HttpResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RequestHandler extends Handler {

  public RequestHandler(Socket clientSocket, String[] filteredWords, CacheManager cacheManager) {
    this.clientSocket = clientSocket;
    this.filteredWords = filteredWords;
    this.cacheManager = cacheManager;
  }

  @Override
  public void handle() {
    try (DataInputStream clientInputStream =
        new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()))) {
      List<String> lines = ReadRawMessage(clientInputStream);
      HttpRequest request = new HttpRequest(lines);
      System.out.println("Request:\n" + request.getFormatted());

      if (request.getMethod().equals("GET") && cacheManager.contains(request.getResourceId())) {
        respondUsingCache(request);
      } else {
        respondUsingTarget(request, clientInputStream);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        clientSocket.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  private void respondUsingTarget(HttpRequest request, DataInputStream clientInputStream)
      throws IOException {
    try (Socket targetSocket = new Socket(request.getUrl().getHost(),
        request.getUrl().getDefaultPort());
        DataOutputStream targetOutputStream = new DataOutputStream(
            new BufferedOutputStream(targetSocket.getOutputStream()))) {

      targetOutputStream.writeBytes(request.getFormatted());
      targetOutputStream.flush();

      // Read HTTP request's body
      String contentLengthString = request.getHeader("Content-Length");
      if (contentLengthString != null) {
        long contentLength = Long.parseLong(contentLengthString);
        if (contentLength != 0) {
          transferData(clientInputStream, targetOutputStream, contentLength);
        }
      }

      ResponseHandler responseHandler = new ResponseHandler(clientSocket, targetSocket, request,
          filteredWords,
          cacheManager);
      responseHandler.handle();
    }
  }

  private void respondUsingCache(HttpRequest request) throws IOException {
    File cachedFile = cacheManager.getFile(request.getResourceId());

    long contentLength = cachedFile.length();

    List<String> lines = new ArrayList<>();
    lines.add("HTTP/1.1 200 OK");
    lines.add("User-Agent: ProxyServer 1.0");
    lines.add("Content-Length: " + contentLength);
    lines.add("X-Cache: HIT");
    lines.add("Cached-Name: " + request.getResourceId());
    HttpResponse response = new HttpResponse(lines);

    try (DataOutputStream clientOutputStream = new DataOutputStream(
        new BufferedOutputStream(clientSocket.getOutputStream()))) {
      clientOutputStream.writeBytes(response.getFormatted());
      clientOutputStream.flush();

      if (contentLength != 0) {
        try (FileInputStream fileInputStream = new FileInputStream(cachedFile)) {
          transferData(fileInputStream, clientOutputStream, contentLength);
        }
      }
    }
  }

  private static void transferData(InputStream inputStream, OutputStream outputStream, long size)
      throws IOException {
    int count;
    int total_count = 0;
    byte[] buffer = new byte[8192];
    while (total_count < size && (count = inputStream.read(buffer)) > 0) {
      total_count += count;
      outputStream.write(buffer, 0, count);
      outputStream.flush();
    }
  }

  Socket clientSocket;
  String[] filteredWords;
  CacheManager cacheManager;
}
