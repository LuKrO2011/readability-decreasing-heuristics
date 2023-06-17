package de.uni_passau.fim.se2.rdh.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for file operations.
 */
public class FileManager {

    private static final Logger log = LoggerFactory.getLogger(FileManager.class);

    /**
     * Checks if the given files exist.
     *
     * @param files the files to check
     */
    public static void checkFiles(File... files) {
        if (files == null) {
            log.error("Files are null");
            return;
        }

        for (File directory : files) {
            checkFile(directory);
        }
    }

    /**
     * Checks if the given file exists.
     *
     * @param file the file to check
     */
    public static void checkFile(File file) {
        if (file == null || !file.exists()) {
            log.error("Directory does not exist: " + file);
        }
    }

    /**
     * Creates a folder if it does not exist.
     *
     * @param path the path to the folder
     * @return the folder
     */
    public static File createFolder(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            boolean success = folder.mkdirs();

            if (!success) {
                log.error("Could not create folder: {}", path);
            }
        }
        return folder;
    }

}
