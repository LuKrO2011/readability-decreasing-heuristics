package de.uni_passau.fim.se2.rdh.util;

import java.io.File;

public class FileManager {

    /**
     * Checks if the given files exist.
     * @param files the files to check
     */
    public static void checkFiles(File... files) {
        for (File directory : files) {
            checkFile(directory);
        }
    }

    public static void checkFile(File directory) {
        if (directory == null || !directory.exists()) {
            throw new IllegalArgumentException("Directory does not exist: " + directory);
        }
    }

    public static File createFolder(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            boolean success = folder.mkdirs();

            if (!success) {
                // log.error("Could not create folder: {}", path);
            }
        }
        return folder;
    }

}
