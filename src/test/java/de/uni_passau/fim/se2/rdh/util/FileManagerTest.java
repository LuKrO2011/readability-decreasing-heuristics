package de.uni_passau.fim.se2.rdh.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

// TODO: The disabled tests only work when executed one by one. When executed all at once, the tests fail.
@ExtendWith(MockitoExtension.class)
class FileManagerTest {

    @Mock
    private static Logger mockLogger;

    @Test
    void testCheckFiles(@TempDir File tempDir) {
        File file1 = new File(tempDir, "file1");
        File file2 = new File(tempDir, "file2");
        FileManager.checkFiles(file1, file2);
    }

    @Disabled
    @Test
    void testCheckFilesNull() {
        File[] files = null;
        FileManager.checkFiles(files);
        verify(mockLogger).error("Files are null");
    }

    @Test
    void testCheckFile(@TempDir File tempDir) {
        File file = new File(tempDir, "test");
        FileManager.checkFile(file);
    }

    @Disabled
    @Test
    void testCheckFileNull() {
        File file = null;
        FileManager.checkFile(file);
        verify(mockLogger).error("Directory does not exist: null");
    }

    @Disabled
    @Test
    void testCheckFileNotExists(@TempDir File tempDir) {
        File file = new File(tempDir, "test");
        file.delete();
        FileManager.checkFile(file);
        verify(mockLogger).error("Directory does not exist: " + file);
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