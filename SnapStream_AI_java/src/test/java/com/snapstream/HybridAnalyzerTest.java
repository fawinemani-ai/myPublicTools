package com.snapstream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class HybridAnalyzerTest {
    @TempDir
    Path tempDir;

    @Test
    void missingImageFailsBeforeAnyApiRequest() {
        HybridAnalyzer analyzer = new HybridAnalyzer(tempDir.resolve("missing-prompt.txt"));

        assertThrows(NoSuchFileException.class,
                () -> analyzer.analyzeImage(tempDir.resolve("missing.png"), ignored -> {}));
    }

    @Test
    void missingApiKeysAreReportedWithoutNetworkAccess() throws Exception {
        assumeTrue(Config.GEMINI_KEY.isEmpty() && Config.OPENROUTER_KEY.isEmpty());
        Path image = tempDir.resolve("image.png");
        Files.write(image, new byte[] { 1, 2, 3 });
        HybridAnalyzer analyzer = new HybridAnalyzer(tempDir.resolve("missing-prompt.txt"));

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> analyzer.analyzeImage(image, ignored -> {}));

        assertEquals("Keine gültigen API-Keys konfiguriert!", error.getMessage());
    }
}
