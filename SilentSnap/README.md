# Stealth Capture Utility

Ein leichtgewichtiger, im Hintergrund laufender Screenshot-Capturer für Windows, der in regelmäßigen Intervallen den Desktop-Framebuffer abgreift, ohne den Fokus des Betriebssystems zu verändern.

## Voraussetzungen

* Python 3.x (empfohlen: Installation mit aktivierten Pfad-Optionen)

## Installation

1. Klone das Repository oder lade die Dateien herunter.
2. Installiere die erforderlichen Python-Abhängigkeiten über das Terminal:
   ```bash
   pip install -r requirements.txt


## Verwendung
Standard-Start (mit Konsole für Tests):
   ```bash
  python main.py
```
Unsichtbarer Start im Hintergrund (Headless):
Starte die Anwendung über den Python-W-Interpreter (oder benenne die Datei in main.pyw um und führe sie per Doppelklick aus):
   ```bash
  pythonw main.py
```
Hinweis: Das Suffix .pyw bzw. pythonw.exe sorgt dafür, dass das Skript als reiner Hintergrundprozess läuft. Es werden keinerlei Konsolenfenster oder Taskleisten-Icons geöffnet.

## Beenden
Da das Programm im Headless-Modus komplett unsichtbar im Hintergrund arbeitet, muss es über das Betriebssystem beendet werden:

1. Öffne den Task-Manager (Strg + Shift + Esc).
2. Suche unter Hintergrundprozesse nach Python (32-bit) oder Pythonw.exe.
3. Klicke mit der rechten Maustaste darauf und wähle Task beenden.