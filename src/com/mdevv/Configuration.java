package com.mdevv;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Configuration parses a text file with server's initial configuration.
 * It is used to parse a file in the following format:
 * PROXY_PORT=8080
 * WORDS=bomb;atomic;uranium
 * CACHE_DIR="D:\TMP"
 */
public class Configuration {
    /**
     * Initialize Configuration using content of file pointed to by filePath.
     *
     * If file does not exist it uses following default values:
     * PROXY_PORT=8080
     * WORDS=
     * CACHE_DIR="."
     *
     * @param filePath path to a file with configuration
     */
    public Configuration(String filePath) {
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> {
                String[] parts = s.split("=");
                configurationMap.put(parts[0], parts[1]);
            });
        } catch (IOException e) {
            // Use default values
            configurationMap.put("PROXY_PORT", "8080");
            configurationMap.put("WORDS", "");
            configurationMap.put("CACHE_DIR", ".");
            System.err.println("Configuration file " + filePath + " does not exist. Using default values.");
        }
    }

    /**
     * Get configuration option by key
     *
     * @param item option's key
     * @return option's value as unformatted string or null if option does not exists
     */
    public String get(String item) {
        return configurationMap.get(item);
    }

    /**
     * Get port on which server should start listening
     *
     * @return port number
     */
    public int getPort() {
        return Integer.parseInt(configurationMap.get("PROXY_PORT"));
    }

    /**
     * Get array of filtered words
     *
     * @return filtered words array
     */
    public String[] getFilteredWords() {
        return configurationMap.get("WORDS").split(";");
    }

    /**
     * Get path to cache directory
     *
     * @return path to cache dir
     */
    public String getCacheDir() {
        return configurationMap.get("CACHE_DIR").replaceAll("\"", "");
    }

    private Map<String, String> configurationMap = new HashMap<>();
}
