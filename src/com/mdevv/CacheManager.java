package com.mdevv;

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

    private Path rootDir;
}
