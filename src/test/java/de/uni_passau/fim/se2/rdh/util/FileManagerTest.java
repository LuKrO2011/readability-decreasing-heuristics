package de.uni_passau.fim.se2.rdh.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class FileManagerTest extends LoggerTest {

    @BeforeEach
    void setUp() {
        attachAppender(FileManager.class);
    }

    @Test
    void testCheckFiles(@TempDir File tempDir) {
        File file1 = new File(tempDir, "file1");
        File file2 = new File(tempDir, "file2");
        FileManager.checkFiles(file1, file2);
    }

    @Test
    void testCheckFilesNull() {
        File[] files = null;
        FileManager.checkFiles(files);
        assertLogContainsExactly("Files are null");
    }

    @Test
    void testCheckFile(@TempDir File tempDir) {
        File file = new File(tempDir, "test");
        FileManager.checkFile(file);
    }

    @Test
    void testCheckFileNull() {
        File file = null;
        FileManager.checkFile(file);
        assertLogContainsExactly("File does not exist: null");
    }

    @Test
    void testCheckFileNotExists(@TempDir File tempDir) {
        File file = new File(tempDir, "test");
        file.delete();
        FileManager.checkFile(file);
        assertLogContainsExactly("File does not exist: " + file);
    }

    @Test
    void testCreateFolder(@TempDir File tempDir) {
        File folder = new File(tempDir, "folder");
        File result = FileManager.createFolder(folder.getAbsolutePath());
        assertThat(result).isEqualTo(folder);
    }

    @Test
    void testCreateFolderExists(@TempDir File tempDir) {
        File folder = new File(tempDir, "folder");
        folder.mkdirs();
        File result = FileManager.createFolder(folder.getAbsolutePath());
        assertThat(result).isEqualTo(folder);
    }


}