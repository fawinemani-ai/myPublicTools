package com.snapstream;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

public class Config {
    private static final Properties props = new Properties();

    static {
        try {
            String jarPath = Config.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            if (jarPath.startsWith("/") && System.getProperty("os.name").toLowerCase().contains("win")) {
                jarPath = jarPath.substring(1);
            }
            Path jarDir = Paths.get(jarPath).getParent();
            Path externalConfig = jarDir != null ? jarDir.resolve("config.properties") : Paths.get("config.properties");

            if (Files.exists(externalConfig)) {
                try (InputStream input = Files.newInputStream(externalConfig)) {
                    props.load(input);
                    System.out.println("Lade externe config.properties von: " + externalConfig.toAbsolutePath());
                }
            } else {
                try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
                    if (input != null) {
                        props.load(input);
                        System.out.println("Lade interne config.properties aus der JAR.");
                    } else {
                        System.err.println("Warnung: Keine config.properties gefunden! Nutze Standardwerte.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Konfiguration: " + e.getMessage());
        }
    }

    public static final Path WATCH_FOLDER = Paths.get(props.getProperty("watch.folder", "./screenshots"));
    public static final Path DONE_FOLDER = WATCH_FOLDER.resolve("erledigt");
    public static final Path CACHE_FILE = Paths.get("processed_files.json");
    public static final Path PROMPT_FILE = Paths.get("prompt_template.txt");

    public static final String GEMINI_KEY = props.getProperty("gemini.api.key", "");
    public static final String GEMINI_MODEL = props.getProperty("gemini.model", "gemini-3.1-flash-lite");
    
    public static final String OPENROUTER_KEY = props.getProperty("openrouter.api.key", "");
    public static final String OPENROUTER_MODEL = props.getProperty("openrouter.model", "nvidia/nemotron-3-ultra-550b-a55b");

    public static final Set<String> SUPPORTED_EXTENSIONS = Set.of(".png", ".jpg", ".jpeg", ".webp");
    public static final long SUCCESS_COOLDOWN_MS = Long.parseLong(props.getProperty("success.cooldown.ms", "15000"));
    public static final int FILE_CHECK_RETRIES = Integer.parseInt(props.getProperty("file.check.retries", "5"));
    public static final long FILE_CHECK_DELAY_MS = Long.parseLong(props.getProperty("file.check.delay.ms", "200"));
    public static final long MOUSE_INACTIVITY_MS = Long.parseLong(props.getProperty("mouse.inactivity.ms", "2000"));
}
