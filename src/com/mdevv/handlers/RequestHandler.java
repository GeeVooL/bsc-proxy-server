package com.mdevv.handlers;

import com.mdevv.components.CacheManager;
import com.mdevv.http.HttpRequest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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
      List<String> lines = ReadHttpMessage(clientInputStream);
      HttpRequest request = new HttpRequest(lines);
      System.out.println("Request:\n" + request.getFormatted());

//            if (request.getMethod() == "GET" && cacheManager.contains(request)) {
//                returnFromCache(request);
//            } else {
      connectToTarget(request, clientInputStream);
//            }
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

  private void connectToTarget(HttpRequest request, DataInputStream clientInputStream)
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
        int contentLength = Integer.parseInt(contentLengthString);
        if (contentLength != 0) {
          int count;
          int total_count = 0;
          byte[] buffer = new byte[8192];
          while (total_count < contentLength && (count = clientInputStream.read(buffer)) > 0) {
            total_count += count;
            targetOutputStream.write(buffer, 0, count);
            targetOutputStream.flush();
          }
        }
      }

      ResponseHandler responseHandler = new ResponseHandler(clientSocket, targetSocket,
          filteredWords,
          cacheManager);
      responseHandler.handle();
    }
  }

  private void returnFromCache(HttpRequest request) {
  }

  Socket clientSocket;
  String[] filteredWords;
  CacheManager cacheManager;
}
