package com.mdevv;

import com.mdevv.components.CacheManager;
import com.mdevv.components.Configuration;
import com.mdevv.handlers.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ProxyServer is a main class that contains the core logic of the server. It also contains static
 * Main method which is an entry point to the program.
 */
public class ProxyServer {

  /**
   * Start a program by reding configuration file and creating ProxyServer class based on it. If
   * arguments or configuration are invalid program closes with exit code 1.
   *
   * @param args one expected argument - path to configuration file
   * @see Configuration
   */
  public static void main(String[] args) {
    String configurationPath = "";

    if (args.length > 0) {
      configurationPath = args[0];
    } else {
      System.err.println("This program requires one parameter - a path to configuration file.");
      System.exit(1);
    }

    Configuration configuration = new Configuration(configurationPath);
    ProxyServer proxy = new ProxyServer(configuration);
    proxy.listen();
  }

  /**
   * Create proxy server class using provided configuration.
   * <p>
   * Server socket is opened in the constructor.
   *
   * @param configuration Configuration object with server settings
   */
  public ProxyServer(Configuration configuration) {
    try {
      this.configuration = configuration;
      this.cacheManager = new CacheManager(configuration.getCacheDir());
      this.serverSocket = new ServerSocket(configuration.getPort());
      System.out.println("Server listening on " + configuration.getPort() + " port.");
    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    } catch (IllegalArgumentException e) {
      System.err.println("Error: Invalid port number.");
      System.exit(1);
    }
  }

  /**
   * Listen for new connection and spawn a separate thread to handle it.
   */
  public void listen() {
    try {
      ExecutorService executor = Executors.newFixedThreadPool(20);
      while (true) {
        executor.submit(new RequestHandler(serverSocket.accept(), configuration.getFilteredWords(),
            cacheManager));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Configuration configuration;
  private CacheManager cacheManager;
  private ServerSocket serverSocket;
}
