package com.snapstream;

import java.io.InputStream;
import java.nio.file.*;
import java.util.Comparator;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class WatchdogFolderWatcher {
    private final HistoryTracker tracker;
    private final Consumer<Path> onFileDetected;
    private volatile boolean running = true;
    private ScheduledExecutorService scheduler;
    private WatchService watchService;

    public WatchdogFolderWatcher(HistoryTracker tracker, HybridAnalyzer analyzer, Consumer<Path> onFileDetected) {
        this.tracker = tracker;
        this.onFileDetected = onFileDetected;
    }

    public void start() {
        try {
            Files.createDirectories(Config.WATCH_FOLDER);
            Files.createDirectories(Config.DONE_FOLDER);
        } catch (Exception e) {}

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::scanFolder, 0, 5, TimeUnit.SECONDS);

        Thread watchThread = new Thread(() -> {
            try {
                watchService = FileSystems.getDefault().newWatchService();
                Config.WATCH_FOLDER.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
                while (running) {
                    WatchKey key = watchService.poll(1, TimeUnit.SECONDS);
                    if (key == null) continue;
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.OVERFLOW) continue;
                        Path fullPath = Config.WATCH_FOLDER.resolve((Path) event.context());
                        if (Files.isRegularFile(fullPath) && isSupported(fullPath)) {
                            if (!Config.DONE_FOLDER.equals(fullPath.getParent()) && !tracker.isProcessed(fullPath.getFileName().toString())) {
                                onFileDetected.accept(fullPath);
                            }
                        }
                    }
                    key.reset();
                }
            } catch (Exception e) {}
        });
        watchThread.setDaemon(true);
        watchThread.start();
    }

    private void scanFolder() {
        if (!running) return;
        try (var stream = Files.list(Config.WATCH_FOLDER)) {
            stream.filter(Files::isRegularFile).filter(this::isSupported)
                  .sorted(Comparator.comparingLong(p -> {
                      try { return Files.getLastModifiedTime(p).toMillis(); } catch (Exception e) { return 0L; }
                  }))
                  .forEach(p -> {
                      if (!tracker.isProcessed(p.getFileName().toString()) && isFileReady(p)) {
                          onFileDetected.accept(p);
                      }
                  });
        } catch (Exception e) {}
    }

    public void stop() {
        running = false;
        if (scheduler != null) scheduler.shutdownNow();
        try { if (watchService != null) watchService.close(); } catch (Exception e) {}
    }

    public boolean isFileReady(Path path) {
        for (int i = 0; i < Config.FILE_CHECK_RETRIES; i++) {
            try (InputStream in = Files.newInputStream(path)) {
                in.readNBytes(1024);
                long size1 = Files.size(path);
                Thread.sleep(300);
                long size2 = Files.size(path);
                if (size1 == size2 && size1 > 0) return true;
            } catch (Exception e) {
                try { Thread.sleep(Config.FILE_CHECK_DELAY_MS); } catch (Exception ex) {}
            }
        }
        return false;
    }

    private boolean isSupported(Path p) {
        return Config.SUPPORTED_EXTENSIONS.stream().anyMatch(p.toString().toLowerCase()::endsWith);
    }
}
