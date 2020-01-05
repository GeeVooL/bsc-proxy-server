package com.mdevv;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class RequestHandler implements Runnable {
    public RequestHandler(Socket clientSocket, String[] filteredWords, CacheManager cacheManager) {
        this.clientSocket = clientSocket;
        this.filteredWords = filteredWords;
        this.cacheManager = cacheManager;

        try {
            this.streamFromClientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.streamToClient = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            HttpRequest request = new HttpRequest(getRawRequest());
            System.out.println("Request: " + request.getMethod() + " " + request.getUrl());
            connect(request);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO(mdziewulski): return error to client's socket
        }
    }

    private String getRawRequest() throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        do {
            line = streamFromClientReader.readLine();
            if (line.equals("")) break;
            builder.append(line).append("\r\n");
        } while (true);
        builder.append("\r\n");
        return builder.toString();
    }

    private void connect(HttpRequest request) throws IOException {
        try (Socket targetSocket = new Socket(request.getUrl().getHost(), request.getUrl().getDefaultPort())) {
            DataOutputStream targetOutputStream = new DataOutputStream(targetSocket.getOutputStream());
            DataInputStream targetInputStream = new DataInputStream(new BufferedInputStream(targetSocket.getInputStream()));

            targetOutputStream.writeBytes(request.getRaw());
            targetOutputStream.flush();

            int count;
            byte[] buffer = new byte[8192];
            while ((count = targetInputStream.read(buffer)) > 0) {
                streamToClient.write(buffer, 0, count);
                streamToClient.flush();
            }
            streamToClient.close();
        } catch (IOException e) {
            e.printStackTrace();
            clientSocket.close();
        }
    }

    Socket clientSocket;
    BufferedReader streamFromClientReader;
    DataOutputStream streamToClient;
    String[] filteredWords;
    CacheManager cacheManager;
}
