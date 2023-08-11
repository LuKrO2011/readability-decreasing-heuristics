package de.uni_passau.fim.se2.rdh.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.io.FileUtils.moveFile;

/**
 * Utility class for file operations.
 */
public final class FileManager {

    /**
     * The (logger) of this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FileManager.class);

    /**
     * This constructor is hidden, because this class is not meant to be instantiated.
     */
    private FileManager() {
    }

    /**
     * Checks if the given files exist.
     *
     * @param files the files to check
     */
    public static void checkFiles(final File... files) {
        if (files == null) {
            LOG.error("Files are null");
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
    public static void checkFile(final File file) {
        if (file == null || !file.exists()) {
            if (LOG.isErrorEnabled()) {
                LOG.error("File does not exist: " + file);
            }
        }
    }

    /**
     * Check if the given file is a directory.
     *
     * @param file the file to check
     */
    public static void checkDirectory(final File file) {
        if (file == null || !file.isDirectory()) {
            if (LOG.isErrorEnabled()) {
                LOG.error("File is not a directory: " + file);
            }
        }
    }

    /**
     * Creates a folder if it does not exist.
     *
     * @param path the path to the folder
     * @return the folder
     */
    public static File createFolder(final String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            boolean success = folder.mkdirs();

            if (!success) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Could not create folder: {}", path);
                }
            }
        }
        return folder;
    }

    /**
     * Moves the given files to the given directory.
     *
     * @param inputDir  The directory to move the files from.
     * @param outPutDir The directory to move the files to.
     * @param files     The files to move.
     */
    public static void moveFiles(final Path inputDir, final Path outPutDir, final File... files) throws IOException {
        if (files == null) {
            LOG.error("Files are null");
            return;
        }

        for (File file : files) {
            File from = new File(inputDir.toString() + "/" + file.getName());
            File to = new File(outPutDir.toString() + "/" + file.getName());
            moveFile(from, to);
        }
    }
}
