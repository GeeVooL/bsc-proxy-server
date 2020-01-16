package com.mdevv.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;

/**
 * A class responsible for managing access to cached content and storing new content
 */
public class CacheManager {

  public CacheManager(String cacheDirPath) throws NoSuchFileException, NotDirectoryException {
    Path path = Paths.get(cacheDirPath);
    if (Files.notExists(path)) {
      throw new NoSuchFileException("Cache directory does not exist.");
    } else if (!Files.isDirectory(path)) {
      throw new NotDirectoryException("Cache path points to a file, not directory.");
    }
    rootDir = path;
  }

  public boolean contains(String path) {
    return Files.exists(rootDir.resolve(path));
  }

  public FileOutputStream createFile(String path) {
    Path filePath = rootDir.resolve(path);
    FileOutputStream fileOutputStream = null;

    try {
      if (Files.notExists(filePath)) {
        Files.createFile(filePath);
      }

      fileOutputStream = new FileOutputStream(new File(filePath.toString()));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return fileOutputStream;
  }

  public File getFile(String path) {
    Path filePath = rootDir.resolve(path);
    return new File(filePath.toString());
  }

  private Path rootDir;
}
