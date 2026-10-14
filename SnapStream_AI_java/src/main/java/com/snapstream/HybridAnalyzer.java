package com.snapstream;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

public class HybridAnalyzer {
    private final Path templatePath;

    public HybridAnalyzer(Path templatePath) {
        this.templatePath = templatePath;
    }

    private String getPrompt() {
        if (Files.exists(templatePath)) {
            try {
                String content = Files.readString(templatePath, StandardCharsets.UTF_8).trim();
                if (!content.isEmpty()) return content;
            } catch (Exception e) {}
        }
        return "Analysiere das Bild direkt und präzise.";
    }

    public void analyzeImage(Path imagePath, Consumer<String> onChunk) throws Exception {
        byte[] imageBytes = Files.readAllBytes(imagePath);
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String mimeType = imagePath.toString().toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";
        String dataUrl = "data:" + mimeType + ";base64," + base64Image;
        String prompt = getPrompt();

        List<String> geminiModels = List.of(Config.GEMINI_MODEL, "gemini-3.5-flash-lite", "gemini-3.7-flash");

        if (!Config.GEMINI_KEY.isEmpty()) {
            boolean success = false;
            Exception lastException = null;

            for (String model : geminiModels) {
                if (model == null || model.isBlank()) continue;
                String jsonPayload = String.format("{\"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": [{\"type\": \"text\", \"text\": \"%s\"}, {\"type\": \"image_url\", \"image_url\": {\"url\": \"%s\"}}]}]}", 
                    model, prompt.replace("\"", "\\\"").replace("\n", "\\n"), dataUrl);

                try {
                    sendApiRequest("https://generativelanguage.googleapis.com/v1beta/openai/chat/completions", Config.GEMINI_KEY, jsonPayload, onChunk);
                    success = true;
                    break; 
                } catch (Exception e) {
                    lastException = e;
                }
            }
            if (success) return;
        }

        if (!Config.OPENROUTER_KEY.isEmpty()) {
            String jsonPayload = String.format("{\"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": [{\"type\": \"text\", \"text\": \"%s\"}, {\"type\": \"image_url\", \"image_url\": {\"url\": \"%s\"}}]}]}", 
                Config.OPENROUTER_MODEL, prompt.replace("\"", "\\\"").replace("\n", "\\n"), dataUrl);
            sendApiRequest("https://openrouter.ai/api/v1/chat/completions", Config.OPENROUTER_KEY, jsonPayload, onChunk);
            return;
        }

        throw new RuntimeException("Keine gültigen API-Keys konfiguriert!");
    }

    private void sendApiRequest(String endpoint, String apiKey, String payload, Consumer<String> onChunk) throws Exception {
        URL url = URI.create(endpoint).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(30000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        InputStream stream = (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream();

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) response.append(line);
        }

        if (responseCode != 200) throw new RuntimeException("HTTP " + responseCode + ": " + response);

        String respStr = response.toString();
        if (respStr.contains("content_filter") || respStr.contains("RECITATION")) {
            onChunk.accept("[SICHERHEITSFILTER] Die KI hat die Antwort blockiert...");
            return;
        }

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"content\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
        java.util.regex.Matcher matcher = pattern.matcher(respStr);

        if (matcher.find()) {
            String rawContent = matcher.group(1);
            if (rawContent.contains("extra_content") || rawContent.contains("thought_signature")) return;
            String decoded = rawContent.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\").replace("\\t", "\t");
            onChunk.accept(decoded);
            return;
        }
        onChunk.accept("Antwort erhalten, konnte aber nicht extrahiert werden.");
    }
}
