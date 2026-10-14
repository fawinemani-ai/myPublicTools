package com.snapstream;

import java.awt.MouseInfo;
import java.awt.Point;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SnapStreamApp extends Application {
    private HistoryTracker tracker;
    private HybridAnalyzer analyzer;
    private WatchdogFolderWatcher watcher;

    private TextArea activeTextBox;
    private Button copyBtn;
    private Label queueLabel;
    private String currentText = "";
    private ExecutorService processingPool;

    private ScheduledExecutorService mousePollService;
    private ScheduledExecutorService folderPollService;
    private Point lastMousePos = null;
    private long lastMouseMovedTime = 0;
    private boolean waitingForUserMove = false;
    private Path currentFilePath = null;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SnapStream AI (Java Edition)");

        tracker = new HistoryTracker(Config.CACHE_FILE);
        analyzer = new HybridAnalyzer(Config.PROMPT_FILE);
        processingPool = Executors.newSingleThreadExecutor();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        HBox topFrame = new HBox(20);
        topFrame.setAlignment(Pos.CENTER_LEFT);
        topFrame.setPadding(new Insets(15, 20, 15, 20));
        topFrame.setStyle("-fx-background-color: #1e1e1e;");

        Label statusLabel = new Label("● Überwache: " + Config.WATCH_FOLDER.toAbsolutePath());
        statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-font-size: 15px;");

        queueLabel = new Label("Ausstehende Bilder: 0");
        queueLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold; -fx-font-size: 15px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        copyBtn = new Button("Text kopieren");
        copyBtn.setDisable(true);
        copyBtn.setPrefWidth(140);
        copyBtn.setPrefHeight(35);
        copyBtn.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        copyBtn.setOnAction(e -> copyToClipboard());

        topFrame.getChildren().addAll(statusLabel, queueLabel, spacer, copyBtn);
        root.setTop(topFrame);

        VBox contentFrame = new VBox();
        contentFrame.setPadding(new Insets(20));
        root.setCenter(contentFrame);

        activeTextBox = new TextArea("Warte auf neue Screenshots... (Drücke LEERTASTE, um den Überwachungsordner zu leeren)");
        activeTextBox.setWrapText(true);
        activeTextBox.setEditable(false);
        activeTextBox.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: white; -fx-font-size: 18px;");
        VBox.setVgrow(activeTextBox, Priority.ALWAYS);
        contentFrame.getChildren().add(activeTextBox);

        Scene scene = new Scene(root);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                clearWatchFolder();
                event.consume();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        primaryStage.setOnCloseRequest(e -> {
            if (watcher != null) watcher.stop();
            if (processingPool != null) processingPool.shutdownNow();
            if (mousePollService != null) mousePollService.shutdownNow();
            if (folderPollService != null) folderPollService.shutdownNow();
            Platform.exit();
        });
        primaryStage.show();

        startMouseWatcher();
        startFolderWatcher(); 

        watcher = new WatchdogFolderWatcher(tracker, analyzer, this::handleNewFile);
        watcher.start();
    }

    private void startFolderWatcher() {
        folderPollService = Executors.newSingleThreadScheduledExecutor();
        folderPollService.scheduleAtFixedRate(() -> {
            try {
                int count = 0;
                if (Files.exists(Config.WATCH_FOLDER)) {
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Config.WATCH_FOLDER)) {
                        for (Path entry : stream) {
                            if (Files.isRegularFile(entry) && !tracker.isProcessed(entry.getFileName().toString())) {
                                count++;
                            }
                        }
                    }
                }
                final int finalCount = count;
                Platform.runLater(() -> queueLabel.setText("Ausstehende Bilder: " + finalCount));
            } catch (Exception ignored) {}
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void clearWatchFolder() {
        processingPool.submit(() -> {
            Platform.runLater(() -> activeTextBox.appendText("\n\n[AKTION] Leere Überwachungsordner..."));
            try {
                int count = 0;
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(Config.WATCH_FOLDER)) {
                    for (Path entry : stream) {
                        if (Files.isRegularFile(entry)) {
                            Files.deleteIfExists(entry);
                            count++;
                        }
                    }
                }
                final int deletedCount = count;
                Platform.runLater(() -> activeTextBox.appendText("\n[ERFOLG] " + deletedCount + " Dateien gelöscht. Warte auf neue Screenshots..."));
                this.currentFilePath = null;
                this.waitingForUserMove = false;
            } catch (Exception e) {}
        });
    }

    private void startMouseWatcher() {
        mousePollService = Executors.newSingleThreadScheduledExecutor();
        mousePollService.scheduleAtFixedRate(() -> {
            try {
                Point currentPos = MouseInfo.getPointerInfo().getLocation();
                long now = System.currentTimeMillis();

                if (lastMousePos == null) {
                    lastMousePos = currentPos;
                    return;
                }

                boolean isMovingNow = (currentPos.x != lastMousePos.x || currentPos.y != lastMousePos.y);

                if (isMovingNow) {
                    if (lastMouseMovedTime == 0) {
                        lastMouseMovedTime = now;
                    } else if ((now - lastMouseMovedTime) >= Config.MOUSE_INACTIVITY_MS && !waitingForUserMove && currentFilePath != null) {
                        waitingForUserMove = true;
                        Platform.runLater(this::moveToNextFile);
                    }
                } else {
                    lastMouseMovedTime = 0;
                    waitingForUserMove = false;
                }
                lastMousePos = currentPos;
            } catch (Exception ignored) {}
        }, 500, 100, TimeUnit.MILLISECONDS);
    }

    private void moveToNextFile() {
        if (currentFilePath != null) {
            try {
                String filename = currentFilePath.getFileName().toString();
                tracker.markProcessed(filename);
                Path newPath = Config.DONE_FOLDER.resolve(filename);
                if (Files.exists(newPath)) {
                    String baseName = filename.replaceFirst("[.][^.]+$", "");
                    String ext = filename.substring(filename.lastIndexOf('.'));
                    newPath = Config.DONE_FOLDER.resolve(baseName + "_" + System.currentTimeMillis() + ext);
                }
                if (Files.exists(currentFilePath)) {
                    Files.move(currentFilePath, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                currentFilePath = null;
                Platform.runLater(() -> activeTextBox.appendText("\n\n[WEITER] Nächster Screenshot wird geladen..."));
            } catch (Exception ignored) {}
        }
    }

    private void handleNewFile(Path filePath) {
        if (this.currentFilePath != null && Files.exists(this.currentFilePath)) return;
        this.currentFilePath = filePath;
        this.waitingForUserMove = false;
        this.lastMouseMovedTime = 0;

        processingPool.submit(() -> {
            String filename = filePath.getFileName().toString();
            if (tracker.isProcessed(filename)) return;

            if (!watcher.isFileReady(filePath)) {
                Platform.runLater(() -> activeTextBox.appendText("\n[WARNUNG] Datei gelockt: " + filename));
                return;
            }

            String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            Platform.runLater(() -> {
                currentText = "";
                copyBtn.setDisable(true);
                activeTextBox.clear();
                activeTextBox.appendText("📄 " + filename + " (" + timestamp + ")\n-----------------------------------\n");
            });

            StringBuilder fullResponse = new StringBuilder();
            final boolean[] success = { false };

            try {
                analyzer.analyzeImage(filePath, chunk -> {
                    fullResponse.append(chunk);
                    Platform.runLater(() -> {
                        activeTextBox.appendText(chunk);
                        currentText += chunk;
                    });
                });
                success[0] = true;
                Path logFile = Config.DONE_FOLDER.resolve(filename.replaceFirst("[.][^.]+$", "") + "_antwort.txt");
                Files.writeString(logFile, fullResponse.toString(), StandardCharsets.UTF_8);

                if (fullResponse.toString().trim().equalsIgnoreCase("none")) {
                    Thread.sleep(300);
                    Platform.runLater(this::moveToNextFile);
                }
            } catch (Exception e) {
                Platform.runLater(() -> activeTextBox.appendText("\n[FEHLER] " + e.getMessage()));
            }
            Platform.runLater(() -> { if (success[0]) copyBtn.setDisable(false); });
        });
    }

    private void copyToClipboard() {
        if (!currentText.isEmpty()) {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(currentText);
            clipboard.setContent(content);
            copyBtn.setText("Kopiert! ✔");
            new Thread(() -> {
                try { Thread.sleep(2000); } catch (Exception ignored) {}
                Platform.runLater(() -> copyBtn.setText("Text kopieren"));
            }).start();
        }
    }

    public static void main(String[] args) { launch(args); }
}
