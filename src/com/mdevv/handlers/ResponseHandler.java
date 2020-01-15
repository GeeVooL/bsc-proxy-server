package com.mdevv.handlers;

import com.mdevv.components.CacheManager;

import com.mdevv.http.HttpResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ResponseHandler extends Handler {

  public ResponseHandler(Socket clientSocket, Socket targetSocket, String[] filteredWords,
      CacheManager cacheManager) {
    this.clientSocket = clientSocket;
    this.targetSocket = targetSocket;
    this.filteredWords = filteredWords;
    this.cacheManager = cacheManager;
  }

  @Override
  public void handle() {
    try (DataInputStream targetInputStream = new DataInputStream(
        new BufferedInputStream(targetSocket.getInputStream()))) {
      List<String> lines = ReadHttpMessage(targetInputStream);
      HttpResponse response = new HttpResponse(lines);
      System.out.println("Response:\n" + response.getFormatted());

      try (DataOutputStream clientOutputStream = new DataOutputStream(
          new BufferedOutputStream(clientSocket.getOutputStream()))) {
        clientOutputStream.writeBytes(response.getFormatted());
        clientOutputStream.flush();

        String contentLengthString = response.getHeader("Content-Length");
        if (contentLengthString != null) {
          int contentLength = Integer.parseInt(contentLengthString);
          if (contentLength != 0) {
            int count;
            int total_count = 0;
            byte[] buffer = new byte[8192];
            while (total_count < contentLength && (count = targetInputStream.read(buffer)) > 0) {
              total_count += count;
              clientOutputStream.write(buffer, 0, count);
              clientOutputStream.flush();
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        targetSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  Socket clientSocket;
  Socket targetSocket;
  String[] filteredWords;
  CacheManager cacheManager;
}
