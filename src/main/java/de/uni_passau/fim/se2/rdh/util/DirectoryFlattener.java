package de.uni_passau.fim.se2.rdh.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Flattens a directory by moving all files in subdirectories to the parent directory and deleting the subdirectories.
 */
public class DirectoryFlattener {

    private static final Logger log = LoggerFactory.getLogger(DirectoryFlattener.class);

    /**
     * Flattens the given directory.
     *
     * @param directory the directory to flatten
     */
    public static void flatten(File directory) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        // Move files in subdirectories to parent directory
        for (File file : files) {
            if (file.isDirectory()) {
                moveFiles(file);

                // Delete empty directory
                boolean success = file.delete();
                if (!success) {
                    log.error("Could not delete directory " + file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Moves all files in the given directory to the parent directory.
     *
     * @param directory the directory to move the files from
     */
    private static void moveFiles(File directory) {
        flatten(directory);

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        // Move files to parent directory
        for (File file : files) {
            if (file.isFile()) {
                try {
                    File destFile = new File(directory.getParent(), file.getName());
                    Files.move(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
