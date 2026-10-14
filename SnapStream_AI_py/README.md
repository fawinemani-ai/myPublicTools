# 🚀 SnapStream AI

Ein rasant schnelles, ereignisgesteuertes Desktop-Tool, das lokale Ordner (oder Netzwerkfreigaben) auf neue Screenshots überwacht und diese in Echtzeit über Künstliche Intelligenz (Google Gemini / OpenRouter) analysiert. 
Speziell optimiert für **Dual-PC-Setups**, bei denen Latenz und Ausfallsicherheit an erster Stelle stehen.

## ✨ Features

* **Echtzeit-Ordnerüberwachung (Watchdog):** Keine CPU-fressenden Dauerschleifen. Die App reagiert in derselben Millisekunde, in der Windows eine neue Datei meldet.
* **Hybrid AI-Fallback System:** Versucht primär, das blitzschnelle `gemini-2.5-flash` Modell zu nutzen. Fällt Google aus, wechselt das System vollautomatisch zu OpenRouter (z.B. Claude 3.5 Sonnet).
* **Live-Streaming ("Schreibmaschinen-Effekt"):** Die Antwort wird Wort für Wort in die UI gestreamt, sobald die KI anfängt zu denken. Keine Wartezeiten auf den finalen Text.
* **Dual-PC & Netzwerk-Sicherheit:** Überprüft Dateisperren (File-Locks) auf Byte-Ebene. Stürzt niemals ab, wenn ein Bild über das Netzwerk (SMB) gerade erst zur Hälfte geschrieben wurde.
* **Auto-Cleanup & Archivierung:** Nach der Analyse wird das Originalbild in einen `erledigt`-Ordner verschoben. Die Antwort der KI wird zusätzlich als `.txt`-Datei mit dem gleichen Namen neben dem Bild gespeichert.
* **Crash-sicheres Gedächtnis:** Verarbeitete Bilder werden in einer `processed_files.json` mit sicherem atomarem Schreibverfahren gespeichert.
* **1-Klick Zwischenablage:** Ein eingebauter Kopieren-Button befördert die finale KI-Antwort sofort in die Windows-Zwischenablage.

---

## Konfiguration (config.env)
Erstelle eine Datei namens config.env im Hauptordner und trage deine Keys ein. Hier ist das komplette Template inklusive der neuen Feinjustierungen für das Netzwerk:

## KI-Verhalten anpassen (prompt_template.txt)
Erstelle eine Datei prompt_template.txt. Der Text darin bestimmt, was die KI mit dem Screenshot machen soll. Beispiel:

## Nutzung im Dual-PC Setup (Empfohlen)

Starte die App auf PC B (python main.py).

Gib den konfigurierten WATCH_FOLDER (z.B. C:\Screenshots_KI) auf PC B im Windows-Netzwerk frei (Rechtsklick -> Eigenschaften -> Freigabe -> Erweitert -> Jeder: Vollzugriff).

Stelle dein Screenshot-Tool auf PC A so ein, dass Bilder direkt in diesen Netzwerkordner (z.B. \\IP-VON-PC-B\Screenshots_KI) gespeichert werden.

Ergebnis: Jeder Screenshot von PC A erscheint in Sekundenbruchteilen analysiert auf dem Bildschirm von PC B. Das Originalbild und die Textantwort findest du danach sauber archiviert im erledigt-Unterordner.


## ⚙️ Installation & Setup

### 1. Voraussetzungen
* Python 3.10 oder neuer.
* Einen API Key für [Google AI Studio](https://aistudio.google.com/) und/oder [OpenRouter](https://openrouter.ai/).

### 2. Abhängigkeiten installieren
Öffne das Terminal im Projektordner und führe aus:
```bash
pip install -r requirements.txt