package com.snapstream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class HistoryTrackerTest {
    @TempDir
    Path tempDir;

    @Test
    void processedFilesArePersistedAndLoaded() {
        Path cacheFile = tempDir.resolve("processed_files.json");
        HistoryTracker tracker = new HistoryTracker(cacheFile);

        assertFalse(tracker.isProcessed("capture.png"));
        tracker.markProcessed("capture.png");

        assertTrue(tracker.isProcessed("capture.png"));
        assertTrue(new HistoryTracker(cacheFile).isProcessed("capture.png"));
    }

    @Test
    void cachedQuestionsAreNormalizedAndPersisted() {
        Path cacheFile = tempDir.resolve("processed_files.json");
        HistoryTracker tracker = new HistoryTracker(cacheFile);

        tracker.cacheQuestion("  Where   is\tit?  ", "At home");

        assertTrue(tracker.isQuestionAlreadyAnswered("where is it?"));
        assertTrue(new HistoryTracker(cacheFile).isQuestionAlreadyAnswered("WHERE IS IT?"));
    }

    @Test
    void nullAndBlankQuestionsAreNotCached() {
        HistoryTracker tracker = new HistoryTracker(tempDir.resolve("processed_files.json"));

        tracker.cacheQuestion(null, "unused");
        tracker.cacheQuestion("  \t ", "unused");

        assertFalse(tracker.isQuestionAlreadyAnswered(null));
        assertFalse(tracker.isQuestionAlreadyAnswered("  "));
    }
}
