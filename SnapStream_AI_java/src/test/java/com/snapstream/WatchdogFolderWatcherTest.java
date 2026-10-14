package com.snapstream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WatchdogFolderWatcherTest {
    @TempDir
    Path tempDir;

    @Test
    void considersAStableNonEmptyFileReady() throws Exception {
        Path image = tempDir.resolve("capture.png");
        Files.write(image, new byte[] { 1, 2, 3 });
        WatchdogFolderWatcher watcher = newWatcher();

        assertTrue(watcher.isFileReady(image));
    }

    @Test
    void considersAMissingFileNotReady() {
        WatchdogFolderWatcher watcher = newWatcher();

        assertFalse(watcher.isFileReady(tempDir.resolve("missing.png")));
    }

    private WatchdogFolderWatcher newWatcher() {
        return new WatchdogFolderWatcher(
                new HistoryTracker(tempDir.resolve("processed_files.json")),
                new HybridAnalyzer(tempDir.resolve("prompt.txt")),
                ignored -> {});
    }
}
