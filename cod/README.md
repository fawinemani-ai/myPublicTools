# 🎮 WARZONE GPC PRO V10 – Rebirth Quads Edition

Ein hochoptimiertes Cronus Zen GPC-Script für Call of Duty: Warzone (Rebirth Island Quads). 
Fokus auf **Geschwindigkeit**, **Präzision** und **Bewegungsfluss** – ohne Ballast.

![Version](https://img.shields.io/badge/version-10.0-blue)
![Platform](https://img.shields.io/badge/platform-Cronus%20Zen-green)
![Game](https://img.shields.io/badge/game-Warzone-orange)

---

## 📋 Inhaltsverzeichnis

- [Features](#-features)
- [Installation](#-installation)
- [Steuerung](#-steuerung)
- [Konfiguration](#-konfiguration)
- [Tuning-Tipps](#-tuning-tipps)
- [Voraussetzungen](#-voraussetzungen)
- [Hinweise](#-hinweise)

---

## ⚡ Features

### 🎯 Aim Assist
- **Smooth Rotational Aim Assist** – kreisförmige Mikro-Bewegung des rechten Sticks
- **Left Stick Micro-Movement** – triggert In-Game Rotational AA
- **Automatische Pause** bei manuellem Zielen (`RX/RY > 30`)
- **Waffenspezifische Stärke** – MP (stark) vs AR (schwächer, weniger Jitter)
- **Live-Anpassung** via `L2 + R3` / `L2 + L1`
- **Visueller AA-Balken** auf dem OLED-Display

### 🔫 Anti-Recoil
- **Zeitbasierte Kurve** – Kompensation steigt progressiv mit der Feuerdauer
- **Pro-Waffe konfigurierbar** – getrennte Werte für MP und AR
- **Max-Cap** verhindert Überkompensation
- **Smart-Pause** – deaktiviert sich, wenn du selbst stark zielst (`RX/RY > 40`)
- **Sofortiger Reset** nach dem Feuern

### 🏃 Movement & Combos
- **YY-Spam** – blitzschnelles Waffenwechseln (60ms Zyklus) für Movement-Flow
- **Slide + 3 Jump Chain** – Kreis beim Sprinten → Slide + 3 automatische Jumps
- **Directional L2-Release Jump** – Springt in Stick-Richtung beim Loslassen von L2
- **Kein versehentlicher Jump** mehr beim Feuern

### 🖥️ OLED & LED Feedback
- **Profil-Anzeige** – MP / AR wird live angezeigt
- **LED-1** = MP-Profil aktiv
- **LED-2** = AR-Profil aktiv
- **AA-Stärke-Balken** – visuell als Punkte dargestellt
- **Versions-Info** auf dem Display

### 🎛️ Weitere Features
- **Instant Hair Triggers** mit Deadzone (verhindert Ghost-Inputs)
- **Auto-Ping** beim ersten Schuss (markiert Gegner für das Team)
- **Smart Weapon Tracking** – Waffenwechsel durch kurzes Dreieck-Drücken
- **Manueller Profil-Reset** – Touchpad + Dreieck
- **Crouch on ADS** (optional, Code-Level)

### 🚫 Bewusst NICHT enthalten
- ❌ Drop-Shot (kein Hinlegen)
- ❌ Auto-Plating Evasion
- ❌ Auto-Rotate Loot
- ❌ Feature-Toggles (alles läuft dauerhaft)

---

## 🎮 Steuerung

### Gameplay-Inputs

| Eingabe | Wirkung |
|---------|---------|
| **KREIS beim Sprinten** | 🔥 Slide + 3 automatische Jumps |
| **RECHTS halten** | YY-Spam (Waffenwechsel-Flow) |
| **TRIANGLE kurz (<300ms)** | Waffenprofil wechseln (MP ↔ AR) |
| **TOUCHPAD + DREIECK** | Profil manuell auf MP zurücksetzen |
| **L2 + R2 halten** | Zielen + Feuern (Anti-Recoil aktiv) |
| **L2 loslassen während R2** | Directional Jump (Richtung per Stick) |
| **L2 + R3** | Aim-Assist-Stärke +2 |
| **L2 + L1** | Aim-Assist-Stärke −2 |

### Jump-Richtung (L2-Release)

| Stick-Position beim Loslassen | Jump-Richtung |
|-------------------------------|---------------|
| Stick nach links (< −30) | ⬅️ Jump nach links |
| Stick nach rechts (> 30) | ➡️ Jump nach rechts |
| Stick neutral | 🔄 Abwechselnd links/rechts |

---

## ⚙️ Konfiguration

Alle Werte können am Anfang des Skripts angepasst werden:

### Anti-Recoil

```gpc
int recoil_MP         = 15;    // Startwert MP
int recoil_AR         = 20;    // Startwert AR
int max_recoil_MP     = 30;    // Maximale Kompensation MP
int max_recoil_AR     = 40;    // Maximale Kompensation AR
int recoil_curve_rate = 8;     // Anstieg pro 100ms Feuerzeit