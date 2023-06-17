package de.uni_passau.fim.se2.rdh.util;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Represents a path that is used for processing. It can be a console, a directory or a file.
 * @author Benedikt Fein - preprocessing toolbox. No tests exist.
 */
public final class ProcessingPath {

    private final OutputPathType pathType;
    private final Path path;

    private ProcessingPath(OutputPathType pathType, Path path) {
        this.pathType = pathType;
        this.path = path;
    }

    /**
     * Creates a new console path.
     *
     * @return the new console path
     */
    public static ProcessingPath console() {
        return new ProcessingPath(OutputPathType.CONSOLE, null);
    }

    /**
     * Creates a new directory path.
     *
     * @param path the path to the directory
     * @return the new directory path
     */
    public static ProcessingPath directory(final Path path) {
        return new ProcessingPath(OutputPathType.DIRECTORY, path);
    }

    /**
     * Creates a new file path.
     *
     * @param path the path to the file
     * @return the new file path
     */
    public static ProcessingPath file(final Path path) {
        return new ProcessingPath(OutputPathType.FILE, path);
    }

    public Path getPath() {
        return path;
    }

    OutputPathType getPathType() {
        return pathType;
    }

    public boolean isDirectory() {
        return OutputPathType.DIRECTORY.equals(pathType);
    }

    public boolean isConsole() {
        return OutputPathType.CONSOLE.equals(pathType);
    }

    public String getAbsolutePath() {
        return path.toAbsolutePath().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ProcessingPath that) {
            return pathType == that.pathType && Objects.equals(path, that.path);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathType, path);
    }

    @Override
    public String toString() {
        return "OutputPath{pathType=" + pathType + ", path=" + path + '}';
    }

    /**
     * Represents the type of the output path.
     */
    enum OutputPathType {
        CONSOLE,
        DIRECTORY,
        FILE,
    }
}
