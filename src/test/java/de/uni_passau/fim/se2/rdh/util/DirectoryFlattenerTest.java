package de.uni_passau.fim.se2.rdh.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryFlattenerTest {

    @Test
    void testFlatten(@TempDir Path path) throws IOException {
        // Get dir of path
        File dir = path.toFile();

        // Create a subdirectory
        File subDir = new File(dir.toString(), "subdir");
        subDir.mkdir();

        // Create a subsubdirectory
        File subSubDir = new File(subDir, "subsubdir");
        subSubDir.mkdir();

        // Create a file in the subdirectory
        File file = new File(subSubDir, "file.txt");
        file.createNewFile();

        // Use the DirectoryFlattener to flatten the directory
        DirectoryFlattener.flatten(dir);

        // New file location
        File movedFile = new File(dir, "file.txt");

        assertAll(
                () -> assertTrue(dir.exists()),
                () -> assertTrue(movedFile.exists()),
                () -> assertFalse(subDir.exists()),
                () -> assertFalse(subSubDir.exists())
        );
    }

}