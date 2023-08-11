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
public final class DirectoryFlattener {

    /**
     * The (logger) of this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DirectoryFlattener.class);

    /**
     * This constructor is hidden, because this class is not meant to be instantiated.
     */
    private DirectoryFlattener() {
    }

    /**
     * Flattens the given directory.
     *
     * @param directory the directory to flatten
     */
    public static void flatten(final File directory) {
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
                    if (LOG.isErrorEnabled()) {
                        LOG.error("Could not delete directory " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

    /**
     * Moves all files in the given directory to the parent directory.
     *
     * @param directory the directory to move the files from
     */
    private static void moveFiles(final File directory) {
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
                    if (LOG.isErrorEnabled()) {
                        LOG.error("Could not move file " + file.getAbsolutePath(), e);
                    }
                }
            }
        }
    }
}
