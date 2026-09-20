# 🎮 WARZONE GPC PRO V12.1 – All-in-One Edition

Ein hochoptimiertes Cronus Zen GPC-Script für Call of Duty: Warzone (Rebirth Island Quads).
**Alle Features dauerhaft aktiv – ein einziges Profil, kein Umschalten, kein Ballast.**

![Version](https://img.shields.io/badge/version-12.1-blue)
![Platform](https://img.shields.io/badge/platform-Cronus%20Zen-green)
![Game](https://img.shields.io/badge/game-Warzone-orange)

---

## 📋 Inhaltsverzeichnis

- [Features](#-features)
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
- **AA-Ramp** – sanftes Einblenden über 180 ms nach ADS-Start (kein erster Schuss-Jitter)
- **Automatische Pause** bei manuellem Zielen (`RX/RY > 30`)
- **Live-Anpassung** via `L2 + R3` / `L2 + L1`
- **Visueller AA-Balken** auf dem OLED-Display

### 🔫 Anti-Recoil
- **Zeitbasierte Kurve** – Kompensation steigt progressiv mit der Feuerdauer
- **ADS-Delay** – greift erst 50 ms nach dem Einzoomen (verhindert Erstschuss-Versatz)
- **Horizontaler Recoil** – kompensiert auch seitlichen Waffen-Drift
- **Ramp-Down** – weiches Auslaufen über 200 ms nach Schussende
- **Max-Cap** verhindert Überkompensation
- **Smart-Pause** – deaktiviert sich, wenn du selbst stark zielst (`RX/RY > 40`)

### 🎯 Präzisions-Features
- **Headshot-Bias** – Stick wird bei ADS leicht nach oben gezogen
- **Snap-Aim** – kurzer Impuls bei `R3` ohne ADS, verstärkt Stick-Ausschlag um 50%
- **Strafe Aim** – Mikro-Strafe beim Feuern für treffsichere Aim-Assist-Trigger

### 🏃 Movement & Combos
- **YY-Spam** – blitzschnelles Waffenwechseln für Movement-Flow
- **Slide + 3 Jump Chain** – Kreis beim Sprinten → Slide + 3 automatische Jumps
- **Slide Cancel** – doppelt Kreis < 400 ms
- **Bunny Hop** – dynamisches Timing (128/134 ms) gegen Makro-Erkennung
- **Auto Sprint** – Sprint-Trigger bei Anlauf + L3
- **Directional L2-Release Jump** – Springt in Stick-Richtung beim Loslassen von L2

### 💥 Combat-Features
- **Drop Shot** – automatisches Hinlegen beim Feuern ohne ADS
- **Rapid Fire** – Doppelschuss-Impuls (L2 + DPAD-Down)
- **Auto-Ping** – markiert Gegner für das Team beim ersten Schuss

### 🖥️ OLED & LED Feedback
- **Vollversion-Anzeige** – WARZONE V12.1 / ALL-IN-ONE
- **AA-Balken** – 7-stufige visuelle Anzeige
- **Alle 4 LEDs aktiv** – bestätigt "All Features ON"

### 🚫 Bewusst NICHT enthalten
- ❌ Profil-Umschaltung (ein einziges, alles-aktiv Profil)
- ❌ Feature-Toggles (alles läuft dauerhaft)
- ❌ Auto-Plating Evasion
- ❌ Auto-Rotate Loot

---

## 🎮 Steuerung

### Gameplay-Inputs

| Eingabe | Wirkung |
|---------|---------|
| **KREIS beim Sprinten** | 🔥 Slide + 3 automatische Jumps |
| **KREIS doppelt < 400ms** | Slide Cancel |
| **RECHTS halten** | YY-Spam (Waffenwechsel-Flow) |
| **L3 + Stick hoch** | Bunny Hop |
| **L3 bei Anlauf** | Auto Sprint |
| **L2 + R2 halten** | Zielen + Feuern (Anti-Recoil + Strafe Aim aktiv) |
| **L2 loslassen während R2** | Directional Jump (Richtung per Stick) |
| **R2 ohne L2** | Drop Shot |
| **R3 (ohne L2, mit RX > 40)** | Snap Aim |
| **L2 + DPAD-Down** | Rapid-Fire Impuls |

### Tuning-Inputs

| Eingabe | Wirkung |
|---------|---------|
| **L2 + R3** | Aim-Assist-Stärke +2 |
| **L2 + L1** | Aim-Assist-Stärke −2 |
| **TOUCHPAD + DREIECK** | AA-Bias zurücksetzen |

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
int cur_rv       = 16;   // Recoil vertikal Startwert
int cur_rv_max   = 34;   // Recoil vertikal Max
int cur_rh       = 2;    // Recoil horizontal (0 = aus)
int cur_rcr      = 6;    // Recoil Curve Rate
int RECOIL_ADS_DELAY = 50;   // ms bevor Recoil greift
int RECOIL_DECAY_MS  = 200;  // ms Auslaufzeit nach Schussende