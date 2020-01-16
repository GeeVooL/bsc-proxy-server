package com.mdevv.handlers;

import com.mdevv.components.CacheManager;
import com.mdevv.components.WordFilter;
import com.mdevv.http.HttpRequest;
import com.mdevv.http.HttpResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class ResponseHandler extends Handler {

  public ResponseHandler(Socket clientSocket, Socket targetSocket, HttpRequest request,
      WordFilter wordFilter,
      CacheManager cacheManager) {
    this.clientSocket = clientSocket;
    this.targetSocket = targetSocket;
    this.request = request;
    this.wordFilter = wordFilter;
    this.cacheManager = cacheManager;
  }

  @Override
  public void handle() {
    try (DataInputStream targetInputStream = new DataInputStream(
        new BufferedInputStream(targetSocket.getInputStream()))) {
      List<String> lines = ReadRawMessage(targetInputStream);
      HttpResponse response = new HttpResponse(lines);
      System.out.println("Response:\n" + response.getFormatted());

      try (DataOutputStream clientOutputStream = new DataOutputStream(
          new BufferedOutputStream(clientSocket.getOutputStream()))) {

        String contentLengthString = response.getHeader("Content-Length");
        String contentTypeString = response.getHeader("Content-Type");

        if (contentLengthString != null) {
          long contentLength = Long.parseLong(contentLengthString);
          FileOutputStream cacheStream = cacheManager.createFile(request.getResourceId());

          if (contentLength != 0) {
            if (contentTypeString != null && contentTypeString.toLowerCase()
                .startsWith("text/html")) {
              transferTextData(targetInputStream, clientOutputStream, cacheStream, contentLength,
                  response);
            } else {
              transferRawData(targetInputStream, clientOutputStream, cacheStream, contentLength);
            }
          }
        } else {
          clientOutputStream.writeBytes(response.getFormatted());
          clientOutputStream.flush();
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

  private void transferTextData(DataInputStream inputStream, DataOutputStream outputStream,
      FileOutputStream cacheStream, long contentLength, HttpResponse response) throws IOException {
    int count;
    int total_count = 0;
    byte[] buffer = new byte[8192];
    boolean caching = (cacheStream != null);

    while (total_count < contentLength && (count = inputStream.read(buffer)) > 0) {
      total_count += count;
      wordFilter.write(buffer);
    }

    wordFilter.filter();
    byte[] filteredBuffer = wordFilter.read();

    if (caching) {
      cacheStream.write(filteredBuffer);
      cacheStream.flush();
    }

    response.modifyHeader("Content-Length", String.valueOf(filteredBuffer.length));
    outputStream.writeBytes(response.getFormatted());
    outputStream.flush();
    outputStream.write(filteredBuffer);
    outputStream.flush();
  }

  private void transferRawData(DataInputStream inputStream, DataOutputStream outputStream,
      FileOutputStream cacheStream, long contentLength) throws IOException {
    int count;
    int total_count = 0;
    byte[] buffer = new byte[8192];
    boolean caching = (cacheStream != null);
    while (total_count < contentLength && (count = inputStream.read(buffer)) > 0) {
      total_count += count;

      // Write response to cache
      if (caching) {
        cacheStream.write(buffer, 0, count);
        cacheStream.flush();
      }

      // Write response to client
      outputStream.write(buffer, 0, count);
      outputStream.flush();
    }
  }

  Socket clientSocket;
  Socket targetSocket;
  HttpRequest request;
  WordFilter wordFilter;
  CacheManager cacheManager;
}
