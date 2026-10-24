# 🎮 PS5 Warzone Pro Script – "1v1 Dominator" Ultimate OLED Edition (v10.0)

Eine speziell auf **Call of Duty: Warzone (PS5)** und das Controller-Layout **"Stick & Move"** abgestimmte Plug-&-Play-Lösung für Cronus Zen.

Version 10.0 vereint **8-Wege-Diamant-Aim-Assist** und **3-Stufen-Anti-Recoil** in einer einzigen mathematischen Engine ohne Combo-Verzögerungen und läuft mit einer übertakteten **125Hz (8ms) Reaktionszeit**.

---

## 📋 Inhaltsverzeichnis

1. [Voraussetzungen & In-Game-Einstellungen](#-1-voraussetzungen--in-game-einstellungen)
2. [Controller-Layout & Tastenbelegung](#-2-controller-layout--tastenbelegung)
3. [OLED-Display & LED-Farbcodes](#-3-oled-display--led-farbcodes)
4. [Live-Tuning im Spiel (Ohne PC)](#-4-live-tuning-im-spiel-ohne-pc)
5. [Detaillierte Funktionsübersicht](#-5-detaillierte-funktionsübersicht)
   * [Aim Assist & Anti-Recoil Engine](#a-aim-assist--anti-recoil-engine)
   * [1v1 Gunfight-Optimierungen](#b-1v1-gunfight-optimierungen)
   * [Pro-Movement & Komfort-Features](#c-pro-movement--komfort-features)
6. [Standardwerte & Code-Anpassung](#-6-standardwerte--code-anpassung)
7. [Fehlerbehebung (FAQ)](#-7-fehlerbehebung-faq)
8. [Vollständiger GPC-Quellcode (v10.0)](#-8-vollständiger-gpc-quellcode-v100)

---

## ⚙️ 1. Voraussetzungen & In-Game-Einstellungen

Damit das Script optimal mit dem Spiel harmoniert, stelle bitte folgende Optionen in deinen **Call of Duty: Warzone Einstellungen** ein:

* **Tasten-Layout (Button Layout):** `Stick & Move` *(Wichtig: Springen liegt auf R3, Nahkampf auf X, Rutschen/Ducken auf Kreis)*.
* **Stoßfänger/Trigger umkehren (Flipped):** `Aus` *(Gezielt wird mit L2, Geschossen mit R2)*.
* **Rutsch-/Hechtsprung-Verhalten (Slide/Dive Behavior):** `Nur Rutschen (Slide Only)` oder `Tippen zum Rutschen (Tap to Slide)`.
* **Automatischer Taktiksprint:** Kann im Spiel auf `Ein` oder `Automatisch Sprinten` stehen (das Script unterstützt den Taktiksprint zusätzlich automatisch).
* **Totzone (Deadzone) im Spiel:** Linker & Rechter Stick Minimum idealerweise auf `0.03` bis `0.05` (das Script besitzt bereits einen integrierten `DEADZONE_MIN = 5` Filter gegen Stick-Drift).

---

## 🕹️ 2. Controller-Layout & Tastenbelegung

Deine gewohnte Steuerung bleibt zu 100 % erhalten, wird aber durch intelligente Doppelfunktionen erweitert:

| Taste (PS5) | Standard-Funktion | Script-Spezialfunktion |
| :--- | :--- | :--- |
| **`L2`** | Zielen (ADS) | Instant Hair-Trigger (100 % ab 1 % Druck) + **Snap-Aim-Boost** (erste 140ms) |
| **`R2`** | Schießen (Fire) | Instant Hair-Trigger + **3-Stufen Anti-Recoil** + **Auto-Enemy-Ping** |
| **`L3`** | Linker Stick Klick | **Identisch mit `Kreis`!** Löst Rutschen, Smart Drop-Shot oder Snaking aus |
| **`R3`** | Springen (Stick & Move) | Einzel-Sprung im Gunfight + **350ms Jump-Recoil-Stabilisator** |
| **`Kreis`** | Ducken / Rutschen | **Slide-Cancel** (beim Laufen), **Drop-Shot** (beim Feuern), **Snaking** (im ADS halten) |
| **`Dreieck`** | Waffenwechsel | **Kurz tippen (<250ms):** Profilwechsel (MP ↔ AR)<br>**Kurz halten (>350ms):** **Easy Plate** (legt alle 3 Platten automatisch an) |
| **`Viereck`** | Nachladen / Interaktion | **Fast Reload Cancel** (Schießen bricht Nachladen per YY sofort ab)<br>**`L2` + `Viereck`:** Rapid-Fire Ein/Aus (für Pistolen/Einzelschuss) |
| **`D-Pad Runter`** | Steuerkreuz Unten | **Außerhalb ADS:** Manuelle Profil-Synchronisation (falls Waffe am Boden getauscht)<br>**Mit `L2` gehalten:** Vertikaler Rückstoß `-1` |
| **`D-Pad Hoch`** | Steuerkreuz Oben | Automatischer Doppel-Ping bei Feuerbeginn<br>**Mit `L2` gehalten:** Vertikaler Rückstoß `+1` |

---

## 🖥️ 3. OLED-Display & LED-Farbcodes

Sowohl das OLED-Display der Cronus Zen als auch die Controller-/Zen-LEDs zeigen dir jederzeit in Echtzeit an, in welchem Modus du dich befindest:

### LED-Farbcodes
* 🟢 **GRÜN:** **MP-Profil (Primärwaffe)** aktiv – optimiert für Nahkampf, schnellen Micro-Strafe und starken Lock-On.
* 🔵 **BLAU:** **Sturmgewehr-Profil (Sekundärwaffe)** aktiv – optimiert für mittlere bis weite Distanz und ruhiges Laser-Tracking.
* 🔴 **ROT:** **Feuergefecht aktiv (`L2` + `R2`)** – Kombinierte Recoil- & Aim-Assist-Engine arbeitet auf Hochtouren.
* 🟡 **GELB:** **Easy Plate aktiv** – Charakter legt automatisch Rüstungsplatten nach.

### OLED-Anzeige
* **Zeile 1:** Script-Titel (`1v1 PRO v10.0`)
* **Zeile 2:** Aktives Waffenprofil (`MP (VOLLAUTO)` / `AR (VOLLAUTO)`), `RAPID FIRE: AN` oder `>> EASY PLATE <<`
* **Zeile 3 & 4:** Aktuelle gespeicherte Werte für `Recoil V` (Vertikal) und `Recoil H` (Horizontal) des aktiven Profils.

---

## 🔧 4. Live-Tuning im Spiel (Ohne PC)

Du kannst den Rückstoß jeder Waffe direkt im Spiel (z. B. auf der Shooting Range) anpassen. Alle Änderungen werden sofort im **EEPROM-Speicher (`SPVAR`)** der Cronus Zen gesichert und bleiben auch nach dem Ausschalten erhalten:

1. Wähle die Waffe aus, die du anpassen möchtest (LED **Grün** für MP oder **Blau** für AR).
2. Halte **`L2` (Zielen)** gedrückt *(nicht schießen!)*.
3. Drücke auf das **Steuerkreuz (D-Pad)**:
   * **`L2` + `D-Pad Hoch`:** Vertikalen Rückstoß-Ausgleich verstärken (`+1`) *(wenn die Waffe noch nach oben zieht)*.
   * **`L2` + `D-Pad Runter`:** Vertikalen Rückstoß-Ausgleich verringern (`-1`) *(wenn die Waffe nach unten zieht)*.
   * **`L2` + `D-Pad Rechts`:** Horizontalen Ausgleich nach rechts (`+1`).
   * **`L2` + `D-Pad Links`:** Horizontalen Ausgleich nach links (`-1`).
   * **`L2` + `Viereck`:** Schaltet **Rapid Fire** für das aktuelle Profil Ein/Aus *(ideal, wenn du eine Pistole oder ein halbautomatisches Gewehr aufhebst)*.

> **Hinweis:** Jeder Tastendruck wird durch eine kurze Controller-Vibration bestätigt und sofort auf dem OLED-Bildschirm aktualisiert.

---

## 🚀 5. Detaillierte Funktionsübersicht

### A. Aim Assist & Anti-Recoil Engine
* **Mathematische Fusion (Zero-Conflict Engine):** In herkömmlichen Scripts überschreiben Aim-Assist-Combos oft den Anti-Recoil-Wert. In v10.0 werden der **8-Wege-Diamant-Rotational-AA** und der **Anti-Recoil** direkt im Hauptprozessor-Loop (`main`) addiert und gemeinsam an den rechten Stick übergeben.
* **3-Stufen Progressiver Rückstoß:**
  * *Stufe 1 (0–220/250ms):* Fängt den aggressiven *First-Shot-Kick* beim Feuerbeginn mit einem Extra-Boost (`SMG_KICK_BOOST` / `AR_KICK_BOOST`) ab.
  * *Stufe 2 (Mittelphase):* Hält die Waffe konstant auf deinem eingestellten Basiswert.
  * *Stufe 3 (ab 850/950ms Dauerfeuer):* Reduziert den Zug nach unten leicht (`SUSTAIN_DAMP`), damit das Fadenkreuz bei großen Magazinen nicht nach unten wegwandert.
* **Anti-Fight Stick-Dämpfung:** Sobald du den rechten Stick selbst stärker als `30%` bewegst (z. B. beim Verfolgen eines rennenden Gegners), halbiert das Script den Aim-Assist-Radius automatisch, damit du keinen Widerstand spürst.

### B. 1v1 Gunfight-Optimierungen
* **125Hz Overclock (`vm_tctrl(-2)`):** Übertaktet die Cronus Zen von 10ms auf **8ms Zykluszeit** – exakt synchron zur Polling-Rate des PS5 DualSense Controllers.
* **Snap-Aim-Boost:** Gibt in den ersten `140ms` beim Drücken von `L2` einen kurzen Magnetismus-Schub (`+4`), damit das Visier beim schnellen Anvisieren sofort auf der Hitbox des Gegners einrastet.
* **R3 Jump-Shot Stabilisator:** Verhindert beim Drücken von `R3` im Feuergefecht störende Doppel-Sprung-Unterbrechungen und erhöht für `350ms` automatisch den vertikalen Anti-Recoil um `+3` (`JUMP_RECOIL_FIX`), um den Hochschlag beim Springen auszugleichen.
* **Micro-Strafe (`STRAFE_STRENGTH = 32`):** Bewegt deinen Charakter beim Schießen im ADS minimal nach links und rechts, um den spieleigenen *Rotational Aim Assist* dauerhaft zu aktivieren, ohne dein Fadenkreuz vom Ziel zu reißen.

### C. Pro-Movement & Komfort-Features
* **L3 = Kreis & Omnimovement Slide-Only Schutz:** Du kannst wahlweise mit `L3` oder `Kreis` rutschen. Das Script kappt das Signal beim Laufen nach `75ms` automatisch, sodass du im Gefecht niemals versehentlich einen langsamen Hechtsprung (Dive) ausführst.
* **Pro Slide-Cancel (`FAST_SLIDE_JUMP`):** Außerhalb des Zielens löst ein Antippen von `L3` oder `Kreis` während des Laufens einen perfekt getimten Slide-Cancel aus und übergibt nahtlos zurück in den **Auto Tac Sprint**.
* **Smart Drop-Shot + Auto-Stand-Up:** Tippst du während des Schießens (`R2`) kurz auf `L3` oder `Kreis`, wirft sich dein Charakter sofort flach auf den Boden, während du weiterfeuerst. Sobald du nach dem Kill `L2`/`R2` loslässt und den linken Stick nach vorne drückst, richtet dich das Script automatisch wieder in den Sprint auf.
* **Snaking (Headglitch Crouch-Peek):** Hältst du hinter einer Deckung `L2` (ohne zu schießen) und bleibst auf `L3` oder `Kreis` (`>220ms`), taucht dein Charakter im optimalen Takt auf und ab, um für Gegner kaum treffbar zu sein.
* **Easy Plate mit 0ms Combat-Cancel:** Halte `Dreieck` für `350ms`, um automatisch bis zu 3 Platten anzulegen. Sobald du `L2`, `R2`, `Kreis`/`L3`, `R3` oder `Viereck` drückst, bricht der Vorgang in **0 Millisekunden** ab, damit du sofort schießen kannst.
* **Fast Reload Cancel:** Wenn du während des Nachladens überrascht wirst, reicht ein Druck auf `R2` (Schießen) – das Script führt sofort einen ultraschnellen Doppel-Dreieck-Cancel (`YY`) aus.
* **180° Turn-Acceleration:** Drückst du den rechten Stick außerhalb des Zielens bis fast an den Anschlag (`>92%`), gibt das Script sofort `100%` Drehgeschwindigkeit frei.

---

## 🛠️ 6. Standardwerte & Code-Anpassung

Falls du feste Grundwerte im Code ändern möchtest, findest du diese ganz oben unter `STANDARD-PROFI-WERTE`:

```c
// --- PROFIL 1: MP (MASCHINENPISTOLE - PRIMÄR / GRÜN) ---
define DEF_SMG_V = 15;        // Standard Vertikal-Rückstoß MP
define SMG_AA_ADS = 13;       // Aim-Assist Stärke beim Zielen (MP)
define SMG_AA_FIRE = 15;      // Aim-Assist Stärke beim Schießen (MP)

// --- PROFIL 2: STURMGEWEHR (AR - SEKUNDÄR / BLAU) ---
define DEF_AR_V = 20;         // Standard Vertikal-Rückstoß AR
define AR_AA_ADS = 10;        // Aim-Assist Stärke beim Zielen (AR)
define AR_AA_FIRE = 12;       // Aim-Assist Stärke beim Schießen (AR)

// --- 1v1 KAMPF- & KOMFORT-WERTE ---
define SNAP_AA_BOOST = 4;     // Extra Lock-On in den ersten 140ms
define JUMP_RECOIL_FIX = 3;   // Extra Rückstoß-Halt beim R3-Jump-Shot
define STRAFE_STRENGTH = 32;  // Stärke der Links/Rechts-Bewegung im Feuergefecht
```

---

## ❓ 7. Fehlerbehebung (FAQ)

* **Frage: Meine Waffe ist im Spiel eine MP, aber die LED leuchtet Blau (AR-Profil). Wie synchronisiere ich das?**
  * **Antwort:** Drücke außerhalb des Zielens einfach einmal kurz auf **`D-Pad Runter`**. Dadurch wechselt das Profil sofort, ohne dass dein Charakter im Spiel die Waffe wechselt.
* **Frage: Warum ist Rapid Fire standardmäßig ausgeschaltet?**
  * **Antwort:** Bei vollautomatischen MPs und Sturmgewehren verlangsamt klassisches Rapid Fire die Feuerrate (TTK), weil der Schussbefehl unterbrochen wird. Schalte Rapid Fire (`L2` + `Viereck`) nur ein, wenn du mit Pistolen, DMRs oder halbautomatischen Schrotflinten spielst. Beim nächsten Waffenwechsel mit `Dreieck` schaltet das Script sicherheitshalber automatisch wieder auf Vollautomatik zurück.
* **Frage: Mein Bildschirm wackelt im Zielen leicht.**
  * **Antwort:** Das ist der *Rotational Aim Assist*, der die Zielhilfe-Blase von Warzone aktiv hält. Wenn du es auf weite Distanz noch ruhiger magst, kannst du im Code `AR_AA_ADS` von `10` auf `8` senken.