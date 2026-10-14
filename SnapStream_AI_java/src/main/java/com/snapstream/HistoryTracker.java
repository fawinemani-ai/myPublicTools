package com.snapstream;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Properties;

public class HistoryTracker {
    private final Path cacheFile;
    private final Path questionCacheFile;
    private final Set<String> processedFiles = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, String> answeredQuestions = Collections.synchronizedMap(new HashMap<>());

    public HistoryTracker(Path cacheFile) {
        this.cacheFile = cacheFile;
        this.questionCacheFile = cacheFile.resolveSibling("question_cache.properties");
        load();
    }

    public synchronized boolean isProcessed(String filename) {
        return processedFiles.contains(filename);
    }

    public synchronized void markProcessed(String filename) {
        processedFiles.add(filename);
        save();
    }

    public synchronized boolean isQuestionAlreadyAnswered(String questionText) {
        if (questionText == null || questionText.isBlank()) return false;
        String normalized = questionText.replaceAll("\\s+", " ").trim().toLowerCase();
        return answeredQuestions.containsKey(normalized);
    }

    public synchronized void cacheQuestion(String questionText, String answer) {
        if (questionText == null || questionText.isBlank()) return;
        String normalized = questionText.replaceAll("\\s+", " ").trim().toLowerCase();
        answeredQuestions.put(normalized, answer);
        saveQuestions();
    }

    private void load() {
        if (Files.exists(cacheFile)) {
            try {
                String content = Files.readString(cacheFile, StandardCharsets.UTF_8);
                content = content.replace("[", "").replace("]", "").replace("\"", "");
                if (!content.isBlank()) {
                    for (String s : content.split(",")) {
                        processedFiles.add(s.trim());
                    }
                }
            } catch (Exception e) {}
        }

        if (Files.exists(questionCacheFile)) {
            try (var reader = Files.newBufferedReader(questionCacheFile, StandardCharsets.UTF_8)) {
                Properties props = new Properties();
                props.load(reader);
                for (String key : props.stringPropertyNames()) {
                    answeredQuestions.put(key, props.getProperty(key));
                }
            } catch (Exception e) {}
        }
    }

    private void save() {
        try {
            StringBuilder sb = new StringBuilder("[\n");
            synchronized (processedFiles) {
                int i = 0;
                for (String f : processedFiles) {
                    sb.append("  \"").append(f).append("\"");
                    if (++i < processedFiles.size()) sb.append(",");
                    sb.append("\n");
                }
            }
            sb.append("]");
            
            Path tempFile = cacheFile.resolveSibling("processed_files.tmp");
            Files.writeString(tempFile, sb.toString(), StandardCharsets.UTF_8);
            Files.move(tempFile, cacheFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {}
    }

    private void saveQuestions() {
        try {
            Properties props = new Properties();
            synchronized (answeredQuestions) {
                props.putAll(answeredQuestions);
            }
            Path tempFile = questionCacheFile.resolveSibling("question_cache.tmp");
            try (var writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
                props.store(writer, "SnapStream AI Question Cache");
            }
            Files.move(tempFile, questionCacheFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {}
    }
}
