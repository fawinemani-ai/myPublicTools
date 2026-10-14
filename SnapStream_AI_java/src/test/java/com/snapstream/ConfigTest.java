package com.snapstream;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;

class ConfigTest {
    @Test
    void doneFolderIsResolvedUnderWatchFolder() {
        assertEquals(Config.WATCH_FOLDER.resolve("erledigt"), Config.DONE_FOLDER);
    }

    @Test
    void cacheAndPromptFilesUseTheirConfiguredNames() {
        assertEquals(Path.of("processed_files.json"), Config.CACHE_FILE);
        assertEquals(Path.of("prompt_template.txt"), Config.PROMPT_FILE);
    }

    @Test
    void supportsTheImageExtensionsHandledByTheWatcher() {
        assertEquals(Set.of(".png", ".jpg", ".jpeg", ".webp"), Config.SUPPORTED_EXTENSIONS);
    }
}
