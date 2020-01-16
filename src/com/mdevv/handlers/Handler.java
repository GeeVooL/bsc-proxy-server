package com.mdevv.handlers;

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

  static List<String> ReadRawMessage(DataInputStream inputStream) throws IOException {
    final byte CR = 13;
    final byte LF = 10;

    List<String> lines = new ArrayList<>();
    String line;
    byte b;
    while (true) {
      StringBuilder lineBuilder = new StringBuilder();

      while (true) {
        b = inputStream.readByte();

        // Ignore any other combination of those bytes except CRLF
        if (b == CR) {
          byte last = b;
          if ((b = inputStream.readByte()) == LF) {
            break;
          }
          lineBuilder.append(last);
        }

        lineBuilder.append((char) b);
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
