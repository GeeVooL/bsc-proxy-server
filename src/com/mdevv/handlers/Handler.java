package com.mdevv.handlers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for handling sending HTTP requests and receiving HTTP responses.
 */
public abstract class Handler implements Runnable {

  public abstract void handle();

  @Override
  public void run() {
    handle();
  }

  static List<String> ReadHttpMessage(DataInputStream inputStream) throws IOException {
    List<String> lines = new ArrayList<>();
    String line;
    byte b;
    while (true) {
      StringBuilder lineBuilder = new StringBuilder();
      while ((b = inputStream.readByte()) != 10) {
        if (b != 13) {
          lineBuilder.append((char) b);
        }
      }
      line = lineBuilder.toString();
      if (line.equals("")) {
        break;
      }
      lines.add(line);
    }

    return lines;
  }
}
