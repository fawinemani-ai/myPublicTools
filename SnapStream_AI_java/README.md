# SnapStream AI (Java)

SnapStream AI überwacht einen Ordner auf neue Screenshots, lässt unterstützte Bilder von einem konfigurierten KI-Bilddienst analysieren und zeigt die Antwort in einem Desktopfenster an. Der Antworttext kann in die Zwischenablage kopiert werden. Verarbeitete Bilder und Antworten werden gespeichert.

## Funktionen

- Überwacht einen konfigurierbaren Ordner auf `.png`-, `.jpg`-, `.jpeg`- und `.webp`-Dateien.
- Unterstützt Gemini und OpenRouter. Gemini wird zuerst verwendet; OpenRouter kann als Ersatzdienst konfiguriert werden.
- Zeigt die Analyse im Fenster an und bietet die Schaltfläche **Text kopieren**.
- Speichert Antworten als Textdateien und verschiebt freigegebene Screenshots in den Unterordner `erledigt`.
- Speichert verarbeitete Dateinamen zwischen Programmstarts.

## Voraussetzungen

- JDK 25. Prüfen mit `java -version`.
- Apache Maven 3.9 oder neuer. Prüfen mit `mvn -version`.
- Für die Bildanalyse einen Gemini- oder OpenRouter-API-Schlüssel. Das Fenster kann auch ohne Schlüssel starten; bei einer Analyse meldet die Anwendung dann, dass kein API-Schlüssel konfiguriert ist.
- Das mit diesem Projekt erzeugte Fat-JAR enthält JavaFX-Nativbibliotheken für die Build-Plattform. Für ein anderes Betriebssystem dort neu bauen.

Das Projekt enthält keinen Maven Wrapper. Maven muss daher installiert und über `PATH` erreichbar sein.

## Konfiguration

Die lokale Datei `config.properties` im Projektverzeichnis enthält die Einstellungen. Trage echte API-Schlüssel nur lokal ein und veröffentliche oder committe sie nicht.

```properties
# Mindestens einen Anbieter konfigurieren.
gemini.api.key=DEIN_GEMINI_API_SCHLUESSEL
gemini.model=gemini-3.1-flash-lite

# Optionaler Anbieter beziehungsweise Ersatzdienst.
openrouter.api.key=
openrouter.model=nvidia/nemotron-3-ultra-550b-a55b

# Zu überwachender Ordner; unter Windows sind Vorwärtsschrägstriche möglich.
watch.folder=./screenshots

# Erkennung fertiger Dateien und Mausbewegung.
file.check.retries=5
file.check.delay.ms=200
mouse.inactivity.ms=2000
```

Die Anwendung versucht zunächst das konfigurierte Gemini-Modell und anschließend fest eingebaute Gemini-Modellalternativen. Schlagen diese Anfragen fehl, wird OpenRouter verwendet, sofern dafür ein Schlüssel eingetragen ist.

**Wichtig: Konfigurationsdatei neben dem Programm ablegen.** Beim Start aus dem erzeugten JAR sucht die Anwendung `config.properties` neben dem JAR. Nach dem Build muss die Datei deshalb nach `target` kopiert werden:

```powershell
Copy-Item .\config.properties .\target\config.properties -Force
```

Die Datei `prompt_template.txt` wird dagegen relativ zum aktuellen Arbeitsverzeichnis gesucht. Starte die Befehle daher aus dem Projektverzeichnis, damit die mitgelieferte Vorlage gefunden wird. Fehlt die Datei oder ist sie leer, verwendet die Anwendung eine eingebaute deutsche Standardvorlage.

Standardmäßig wird `./screenshots` überwacht. Der Unterordner `erledigt` wird für abgeschlossene Bilder und Antwortdateien verwendet. `processed_files.json` und `question_cache.properties` liegen im aktuellen Arbeitsverzeichnis.

## Bauen und Starten

Öffne PowerShell im Projektordner `SnapStream_AI_java` und führe Folgendes aus:

```powershell
mvn clean package
Copy-Item .\config.properties .\target\config.properties -Force
java -jar .\target\snapstream-ai-1.0-SNAPSHOT.jar
```

Das ausführbare Fat-JAR liegt anschließend unter `target/snapstream-ai-1.0-SNAPSHOT.jar`. Lass PowerShell im Projektordner geöffnet, während du das JAR startest. So findet die Anwendung `prompt_template.txt` und legt ihre Verlaufsdateien an der erwarteten Stelle ab.

### Start aus VS Code oder einer Java-IDE

1. Öffne den Ordner `SnapStream_AI_java` und importiere ihn als Maven-Projekt.
2. Wähle JDK 25 als Projekt-JDK aus.
3. Stelle sicher, dass `target/config.properties` vorhanden ist. Falls nötig, führe den obigen `Copy-Item`-Befehl aus.
4. Setze das Arbeitsverzeichnis auf den Projektordner und starte `com.nemani.run.Main`.

`Main` startet die JavaFX-Anwendung. Der Start des Fat-JARs ist der einfachste Weg, die JavaFX-Abhängigkeiten korrekt auf den Laufzeit-Classpath zu bringen.

## Anwendung verwenden

1. Starte die Anwendung und prüfe im Fenster den angezeigten Überwachungsordner.
2. Speichere oder kopiere ein unterstütztes Bild in diesen Ordner.
3. Warte, bis die Analyse im Fenster erscheint. Über **Text kopieren** gelangt der Text in die Zwischenablage.
4. Nach anhaltender Mausbewegung wird der Screenshot freigegeben und in `erledigt` verschoben. Die Dauer wird mit `mouse.inactivity.ms` eingestellt und beträgt standardmäßig 2 Sekunden. Das Antwortprotokoll wird dort als `<Dateiname>_antwort.txt` gespeichert.

Die Leertaste löscht reguläre Dateien direkt im Überwachungsordner. Das ist eine tatsächliche Dateilöschung, nicht nur das Leeren einer Warteschlange. Verschiebe Dateien, die du behalten möchtest, vorher aus diesem Ordner.

## Tests

Alle Unit-Tests aus dem Projektverzeichnis starten:

```powershell
mvn test
```

Produktions- und Testcode kompilieren, ohne Tests auszuführen:

```powershell
mvn test-compile
```

Die Tests verwenden JUnit Jupiter und Maven Surefire. Sie prüfen Konfigurationswerte, Verlaufsspeicherung und Normalisierung von Fragen, Fehlerpfade des Analysators ohne Netzwerkzugriffe sowie die Bereitschaftserkennung des Ordner-Watchers. Der JavaFX-Programmstart und die Oberfläche werden nicht durch headless Unit-Tests gestartet.

## Fehlerbehebung

### `mvn` oder `java` wird nicht erkannt

Installiere Maven und JDK 25 und öffne danach ein neues Terminal, damit die aktualisierte `PATH`-Umgebung geladen wird. Prüfe die Installation mit `mvn -version` und `java -version`.

### Meldung, dass keine Konfiguration gefunden wurde

Das JAR sucht `config.properties` in seinem eigenen Ordner. Kopiere die Datei nach dem Build wie oben gezeigt nach `target/config.properties`. Ohne externe Datei werden Standardwerte verwendet.

### Bei der Analyse fehlt ein API-Schlüssel

Trage `gemini.api.key` oder `openrouter.api.key` in `config.properties` im Projektverzeichnis ein. Kopiere die geänderte Datei erneut nach `target/config.properties` und starte die Anwendung neu. API-Schlüssel gehören weder in Kommandozeilenargumente noch in Git.

### Die Prompt-Vorlage wird nicht gefunden

Starte das Programm mit dem Projektverzeichnis als Arbeitsverzeichnis. Der Pfad zu `prompt_template.txt` ist relativ zu diesem Verzeichnis und nicht zum Speicherort des JARs.

### JavaFX-Laufzeitkomponenten fehlen

Starte das Fat-JAR mit `java -jar` oder führe die Main-Klasse über das importierte Maven-Projekt aus. Ein einfacher `java`-Aufruf für die kompilierte Klasse enthält die JavaFX-Abhängigkeiten nicht automatisch.

### Ein Screenshot wird ignoriert oder bleibt im Ordner

Unterstützt werden nur PNG, JPG, JPEG und WebP. Die Datei muss nicht leer sein und während der Bereitschaftsprüfung stabil bleiben. Prüfe außerdem, ob der im Fenster angezeigte Überwachungsordner der gewünschte Pfad ist.

## Projektstruktur

```text
src/main/java/       Anwendungscode
src/test/java/       Unit-Tests
config.properties    Lokale Anbieter- und Ordnerkonfiguration
prompt_template.txt  Prompt für die Bildanalyse
target/              Maven-Build-Ausgabe und Fat-JAR
```
