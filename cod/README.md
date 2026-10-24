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

---

## 💻 8. Vollständiger GPC-Quellcode (v10.0)

```c
//============================================================================
// PS5 WARZONE PRO SCRIPT - "1v1 DOMINATOR" ULTIMATE OLED EDITION
// Version: 10.0 - FIXED RECOIL/AA OVERWRITE, R3 JUMP-STABILIZER & FAST TTK
//============================================================================
//---------------------------/ PS5 CONTROLS ---------------------------------
define FIRE = 4;        // R2
define JUMP_R3 = 5;     // R3 (Sprungtaste bei Stick & Move)
define ADS = 7;         // L2
define L3_BTN = 8;      // L3 (Taktik-Rutschen + Auto Sprint)
define RX = 9;          // Rechter Stick X
define RY = 10;         // Rechter Stick Y
define LX = 11;         // Linker Stick X (Strafe)
define LY = 12;         // Linker Stick Y (Laufen)
define UP = 13;         // D-Pad Hoch (Auto-Ping & Live-Tuning)
define DOWN = 14;       // D-Pad Runter (Profil-Sync & Live-Tuning)
define LEFT = 15;       // D-Pad Links (Live-Tuning Horizontal)
define RIGHT = 16;      // D-Pad Rechts (Live-Tuning Horizontal)
define SW_WEAPON = 17;  // Dreieck (Waffenwechsel & Easy Plate)
define CIRCLE = 18;     // Kreis (Taktik-Rutschen / Drop-Shot / Snaking)
define CROSS = 19;      // X (Nahkampf bei Stick & Move)
define RELOAD = 20;     // Viereck (Nachladen / Rapid-Fire Toggle mit L2)

//---------------------------/ STANDARD-PROFI-WERTE -------------------------
// --- PROFIL 1: MP (MASCHINENPISTOLE - PRIMÄR / GRÜN) ---
define DEF_SMG_V = 15;        // Echter, konstanter Vertikal-Halt für MPs
define DEF_SMG_H = 0;         // Horizontal-Ausgleich für MPs
define SMG_KICK_BOOST = 4;    // Fängt den ersten Schuss-Kick (0-220ms) sofort ab
define SMG_SUSTAIN_DAMP = 2;  // Sanfte Glättung ab 850ms Dauerfeuer
define SMG_AA_ADS = 13;       // Starker Magnetismus beim Zielen
define SMG_AA_FIRE = 15;      // Klebt im 1v1 auf der Hitbox ohne zu zittern
define SMG_STRAFE_TIME = 160; // Schneller Micro-Strafe für Rotational AA

// --- PROFIL 2: STURMGEWEHR (AR - SEKUNDÄR / BLAU) ---
define DEF_AR_V = 20;         // Stärkerer Vertikal-Halt für ARs
define DEF_AR_H = 0;          // Horizontal-Ausgleich für ARs
define AR_KICK_BOOST = 5;     // Fängt den ersten Schuss-Kick (0-250ms) ab
define AR_SUSTAIN_DAMP = 3;   // Glättung ab 950ms Dauerfeuer
define AR_AA_ADS = 10;        // Ruhiges Visier auf Distanz
define AR_AA_FIRE = 12;       // Präzises Laser-Tracking beim Schießen
define AR_STRAFE_TIME = 200;  // Ruhigerer Micro-Strafe

// --- 1v1 KAMPF- & KOMFORT-WERTE ---
define SNAP_AA_BOOST = 4;     // Extra Aim-Lock in den ersten 140ms beim Anvisieren!
define JUMP_RECOIL_FIX = 3;   // Zieht Waffe beim R3-Jump-Shot extra runter (gegen Verreißen)
define HIPFIRE_AA = 17;       // Starker Aim-Assist im Hüftfeuer
define STRAFE_STRENGTH = 32;  // 32 statt 75: Aktiviert AA ohne dein Aim zu verreißen
define AA_SPEED = 16;         // 125Hz Timing (Vielfaches von 8ms)
define PING_COOLDOWN = 2500;  // 2,5 Sek. Pause zwischen Auto-Pings
define DEADZONE_MIN = 5;      // Filtert Stick-Drift im Ruhezustand

//---------------------------/ LED FARBEN -----------------------------------
define LED_OFF = 0;
define LED_GREEN = 1;
define LED_BLUE = 2;
define LED_PINK = 3;
define LED_RED = 4;
define LED_SKYBLUE = 5;
define LED_YELLOW = 6;
define LED_WHITE = 7;

data(0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 2,
     0, 2, 0, 0, 2, 0, 2, 0, 0, 2, 2, 0, 2, 2, 2, 2);

//---------------------------/ OLED STRINGS ---------------------------------
const string TXT_TITLE   = "1v1 PRO v10.0";
const string TXT_SMG     = "MP (VOLLAUTO)";
const string TXT_AR      = "AR (VOLLAUTO)";
const string TXT_RF_ON   = "RAPID FIRE: AN";
const string TXT_PLATE   = ">> EASY PLATE <<";
const string TXT_VERT    = "Recoil V:";
const string TXT_HORIZ   = "Recoil H:";

//---------------------------/ VARIABLES ------------------------------------
int is_ar_profile = FALSE;    // FALSE = MP (Grün), TRUE = Sturmgewehr (Blau)
int rapid_fire_on = FALSE;    // Standard AUS (Vollauto ist schneller für MP/AR!)
int smg_recoil_v, smg_recoil_h;
int ar_recoil_v, ar_recoil_h;
int cur_recoil_v = 15;
int cur_recoil_h = 0;
int cur_aa_size = 13;
int cur_strafe_t = 160;
int final_ry_offset = 0;
int final_rx_offset = 0;
int aa_step = 0;
int aa_timer = 0;
int aa_x = 0;
int aa_y = 0;
int jump_stabilizer_timer = 0;
int current_led = -1;
int target_led = 0;
int rumble_cnt = 0;
int scj_lock = FALSE;
int prone_active = FALSE;
int oled_update = TRUE;
int ping_timer = 0;
int reload_timer = 0;
int plate_timer = 0;
int l3_phys = FALSE;
int circle_phys = FALSE;
int slide_pressed = FALSE;
int num_idx, num_n, num_c;

//---------------------------/ INIT (LÄDT GESPEICHERTE WERTE) ---------------
init {
	smg_recoil_v = get_pvar(SPVAR_1, 0, 60, DEF_SMG_V);
	smg_recoil_h = get_pvar(SPVAR_2, -30, 30, DEF_SMG_H);
	ar_recoil_v  = get_pvar(SPVAR_3, 0, 60, DEF_AR_V);
	ar_recoil_h  = get_pvar(SPVAR_4, -30, 30, DEF_AR_H);
	oled_update  = TRUE;
}

//---------------------------/ MAIN BLOCK -----------------------------------
main {
	//====================================================================
	// 0. 125Hz DUALSENSE OVERCLOCK (8ms Reaktionszeit statt 10ms)
	//====================================================================
	vm_tctrl(-2);

	//====================================================================
	// 1. ZERO-STICK-DRIFT & 180-GRAD TURN-ACCELERATION
	//====================================================================
	if (!get_val(ADS) && !get_val(FIRE)) {
		if (abs(get_val(RX)) < DEADZONE_MIN) set_val(RX, 0);
		if (abs(get_val(RY)) < DEADZONE_MIN) set_val(RY, 0);
		if (abs(get_val(LX)) < DEADZONE_MIN) set_val(LX, 0);
		if (abs(get_val(LY)) < DEADZONE_MIN) set_val(LY, 0);

		if (get_val(RX) > 92)  set_val(RX, 100);
		if (get_val(RX) < -92) set_val(RX, -100);
	}

	//====================================================================
	// 2. LIVE RECOIL-TUNING & RAPID-FIRE TOGGLE (L2 HALTEN)
	//====================================================================
	if (get_val(ADS) && !get_val(FIRE)) {
		if (event_press(UP)) {
			if (!is_ar_profile) smg_recoil_v = clamp_val(smg_recoil_v + 1, 0, 60);
			else ar_recoil_v = clamp_val(ar_recoil_v + 1, 0, 60);
			SAVE_VALUES();
		}
		if (event_press(DOWN)) {
			if (!is_ar_profile) smg_recoil_v = clamp_val(smg_recoil_v - 1, 0, 60);
			else ar_recoil_v = clamp_val(ar_recoil_v - 1, 0, 60);
			SAVE_VALUES();
		}
		if (event_press(RIGHT)) {
			if (!is_ar_profile) smg_recoil_h = clamp_val(smg_recoil_h + 1, -30, 30);
			else ar_recoil_h = clamp_val(ar_recoil_h + 1, -30, 30);
			SAVE_VALUES();
		}
		if (event_press(LEFT)) {
			if (!is_ar_profile) smg_recoil_h = clamp_val(smg_recoil_h - 1, -30, 30);
			else ar_recoil_h = clamp_val(ar_recoil_h - 1, -30, 30);
			SAVE_VALUES();
		}
		if (event_press(RELOAD)) {
			rapid_fire_on = !rapid_fire_on;
			rumble_cnt = 1;
			oled_update = TRUE;
		}
	}

	//====================================================================
	// 3. L3 BUTTON = KREIS BUTTON & OMNIMOVEMENT SLIDE-ONLY SCHUTZ
	//====================================================================
	l3_phys = get_val(L3_BTN) > 0;
	circle_phys = get_val(CIRCLE) > 0;
	slide_pressed = event_press(L3_BTN) || event_press(CIRCLE);

	if (l3_phys) {
		set_val(CIRCLE, 100);
		set_val(L3_BTN, 0);
	}

	if ((l3_phys || circle_phys) && (get_val(LY) < -55 || abs(get_val(LX)) > 65) && !get_val(ADS) && !get_val(FIRE)) {
		if (get_ptime(L3_BTN) > 75 || get_ptime(CIRCLE) > 75) {
			set_val(CIRCLE, 0);
		}
	}

	//====================================================================
	// 4. AUTOMATISCHER WAFFEN-WECHSEL & EASY PLATE (DREIECK)
	//====================================================================
	if (event_release(SW_WEAPON) && get_ptime(SW_WEAPON) < 250 && plate_timer <= 0) {
		is_ar_profile = !is_ar_profile;
		rapid_fire_on = FALSE;
		rumble_cnt = 1;
		oled_update = TRUE;
	}

	if (get_val(SW_WEAPON) && get_ptime(SW_WEAPON) >= 350 && plate_timer <= 0 && !get_val(ADS) && !get_val(FIRE)) {
		plate_timer = 4500;
		rumble_cnt = 1;
		oled_update = TRUE;
	}

	if (plate_timer > 0) {
		if (get_val(ADS) || get_val(FIRE) || slide_pressed || event_press(RELOAD) || event_press(JUMP_R3)) {
			plate_timer = 0;
			oled_update = TRUE;
		}
		else {
			plate_timer = plate_timer - get_rtime();
			set_val(SW_WEAPON, 100);
			if (plate_timer <= 0) oled_update = TRUE;
		}
	}

	if (event_press(DOWN) && !get_val(ADS) && !get_val(FIRE)) {
		is_ar_profile = !is_ar_profile;
		rapid_fire_on = FALSE;
		rumble_cnt = 1;
		oled_update = TRUE;
	}

	//====================================================================
	// 5. INSTANT HAIR TRIGGERS (0ms Verzögerung beim Zielen & Schießen)
	//====================================================================
	if (get_val(ADS) > 0)  set_val(ADS, 100);
	if (get_val(FIRE) > 0) {
		set_val(FIRE, 100);
		if (rapid_fire_on) {
			combo_run(RAPID_FIRE_MOD);
		}
	}
	else {
		combo_stop(RAPID_FIRE_MOD);
	}

	//====================================================================
	// 6. R3 JUMP-SHOT STABILISATOR (KEIN FEUER-AUSSETZER BEIM SPRINGEN!)
	//====================================================================
	if (event_press(JUMP_R3) && (get_val(ADS) || get_val(FIRE))) {
		combo_stop(FAST_SLIDE_JUMP);
		combo_stop(SNAKING_PEEK);
		scj_lock = FALSE;
		prone_active = FALSE;
		jump_stabilizer_timer = 350;
	}

	if (jump_stabilizer_timer > 0) {
		jump_stabilizer_timer = jump_stabilizer_timer - get_rtime();
	}

	//====================================================================
	// 7. VEREINTE PROFIL-, SNAP-AA & RECOIL-ENGINE (KEIN KONFLIKT MEHR!)
	//====================================================================
	if (!is_ar_profile) {
		cur_recoil_v = smg_recoil_v;
		cur_recoil_h = smg_recoil_h;

		if (get_val(FIRE)) {
			if (get_ptime(FIRE) < 220) {
				cur_recoil_v = smg_recoil_v + SMG_KICK_BOOST;
			}
			else if (get_ptime(FIRE) > 850) {
				cur_recoil_v = smg_recoil_v - SMG_SUSTAIN_DAMP;
			}
		}

		cur_strafe_t = SMG_STRAFE_TIME;

		if (get_val(ADS) && get_val(FIRE)) cur_aa_size = SMG_AA_FIRE;
		else if (!get_val(ADS) && get_val(FIRE)) cur_aa_size = HIPFIRE_AA;
		else cur_aa_size = SMG_AA_ADS;
	}
	else {
		cur_recoil_v = ar_recoil_v;
		cur_recoil_h = ar_recoil_h;

		if (get_val(FIRE)) {
			if (get_ptime(FIRE) < 250) {
				cur_recoil_v = ar_recoil_v + AR_KICK_BOOST;
			}
			else if (get_ptime(FIRE) > 950) {
				cur_recoil_v = ar_recoil_v - AR_SUSTAIN_DAMP;
			}
		}

		cur_strafe_t = AR_STRAFE_TIME;

		if (get_val(ADS) && get_val(FIRE)) cur_aa_size = AR_AA_FIRE;
		else if (!get_val(ADS) && get_val(FIRE)) cur_aa_size = HIPFIRE_AA;
		else cur_aa_size = AR_AA_ADS;
	}

	if (get_val(ADS) && get_ptime(ADS) < 140) {
		cur_aa_size = cur_aa_size + SNAP_AA_BOOST;
	}

	if (jump_stabilizer_timer > 0 && get_val(FIRE)) {
		cur_recoil_v = cur_recoil_v + JUMP_RECOIL_FIX;
	}

	if (get_val(ADS) || get_val(FIRE)) {
		aa_timer = aa_timer + get_rtime();
		if (aa_timer >= AA_SPEED) {
			aa_timer = 0;
			aa_step = (aa_step + 1) % 8;
		}

		if (aa_step == 0)      { aa_x = 0;                        aa_y = cur_aa_size; }
		else if (aa_step == 1) { aa_x = (cur_aa_size * 7) / 10;   aa_y = (cur_aa_size * 7) / 10; }
		else if (aa_step == 2) { aa_x = cur_aa_size;              aa_y = 0; }
		else if (aa_step == 3) { aa_x = (cur_aa_size * 7) / 10;   aa_y = ((cur_aa_size * 7) / 10) * -1; }
		else if (aa_step == 4) { aa_x = 0;                        aa_y = cur_aa_size * -1; }
		else if (aa_step == 5) { aa_x = ((cur_aa_size * 7) / 10) * -1; aa_y = ((cur_aa_size * 7) / 10) * -1; }
		else if (aa_step == 6) { aa_x = cur_aa_size * -1;         aa_y = 0; }
		else if (aa_step == 7) { aa_x = ((cur_aa_size * 7) / 10) * -1; aa_y = (cur_aa_size * 7) / 10; }

		if (abs(get_val(RX)) > 30 || abs(get_val(RY)) > 30) {
			aa_x = aa_x / 2;
			aa_y = aa_y / 2;
		}

		final_rx_offset = aa_x;
		final_ry_offset = aa_y;

		if (get_val(FIRE) && reload_timer <= 0) {
			if (get_val(ADS)) {
				final_ry_offset = final_ry_offset + cur_recoil_v;
				final_rx_offset = final_rx_offset + cur_recoil_h;
			}
			else {
				final_ry_offset = final_ry_offset + (cur_recoil_v / 2);
			}
		}

		if (abs(get_val(RX)) < 85) {
			set_val(RX, clamp_val(get_val(RX) + final_rx_offset, -100, 100));
		}
		if (abs(get_val(RY)) < 85) {
			set_val(RY, clamp_val(get_val(RY) + final_ry_offset, -100, 100));
		}
	}
	else {
		aa_step = 0;
		aa_timer = 0;
	}

	PLAY_LEDS();
	if (oled_update) {
		DRAW_OLED();
		oled_update = FALSE;
	}

	//====================================================================
	// 8. FAST RELOAD CANCEL (Notfall-Abbruch beim Nachladen)
	//====================================================================
	if (event_press(RELOAD) && !get_val(ADS)) reload_timer = 2000;

	if (reload_timer > 0) {
		reload_timer = reload_timer - get_rtime();
		if (event_press(FIRE)) {
			reload_timer = 0;
			combo_run(FAST_RELOAD_CANCEL);
		}
	}

	//====================================================================
	// 9. AUTO-PING GEGNER MARKIEREN (Beim Schießen im ADS)
	//====================================================================
	if (ping_timer > 0) ping_timer = ping_timer - get_rtime();

	if (get_val(ADS) && event_press(FIRE) && ping_timer <= 0) {
		combo_run(AUTO_ENEMY_PING);
		ping_timer = PING_COOLDOWN;
	}

	//====================================================================
	// 10. MICRO-STRAFE IM GUNFIGHT (Aktiviert Rotational AA ohne Verreißen)
	//====================================================================
	if (get_val(ADS) && get_val(FIRE) && !combo_running(SMART_DROP_SHOT) && !prone_active) {
		if (abs(get_val(LX)) < 35 && abs(get_val(LY)) < 35) {
			combo_run(STRAFE_SHOT);
		}
		else {
			combo_stop(STRAFE_SHOT);
		}
	}
	else {
		combo_stop(STRAFE_SHOT);
	}

	//====================================================================
	// 11. MOVEMENT: DROP-SHOT, SNAKING & SLIDE-CANCEL
	//====================================================================
	if (slide_pressed && get_val(FIRE)) {
		combo_stop(FAST_SLIDE_JUMP);
		combo_stop(STRAFE_SHOT);
		combo_stop(SNAKING_PEEK);
		prone_active = TRUE;
		combo_run(SMART_DROP_SHOT);
	}

	if (get_val(ADS) && !get_val(FIRE) && (l3_phys || circle_phys) && abs(get_val(LY)) < 40) {
		if (get_ptime(L3_BTN) > 220 || get_ptime(CIRCLE) > 220) {
			set_val(CIRCLE, 0);
			combo_run(SNAKING_PEEK);
		}
	}
	else {
		combo_stop(SNAKING_PEEK);
	}

	if (prone_active && !get_val(ADS) && !get_val(FIRE) && !combo_running(SMART_DROP_SHOT)) {
		if (get_val(LY) < -75) {
			prone_active = FALSE;
			combo_run(QUICK_STAND_UP);
		}
		else if (slide_pressed || event_press(JUMP_R3)) {
			prone_active = FALSE;
		}
	}

	if (!get_val(ADS) && !get_val(FIRE) && (get_val(LY) < -60 || abs(get_val(LX)) > 70)) {
		if (slide_pressed && !scj_lock) {
			scj_lock = TRUE;
			prone_active = FALSE;
			combo_stop(AUTO_TAC_SPRINT);
			combo_run(FAST_SLIDE_JUMP);
		}
	}

	if (scj_lock && !combo_running(FAST_SLIDE_JUMP)) {
		scj_lock = FALSE;
	}

	//====================================================================
	// 12. AUTOMATISCHER TAKTIK-SPRINT
	//====================================================================
	if (get_val(LY) < -75 && !get_val(ADS) && !get_val(FIRE) && !combo_running(FAST_SLIDE_JUMP) && !combo_running(QUICK_STAND_UP) && !l3_phys && !circle_phys) {
		combo_run(AUTO_TAC_SPRINT);
	}
	else {
		combo_stop(AUTO_TAC_SPRINT);
	}

	//====================================================================
	// 13. VIBRATIONS-FEEDBACK
	//====================================================================
	if (rumble_cnt > 0 && !combo_running(RUMBLE_FEEDBACK)) {
		combo_run(RUMBLE_FEEDBACK);
	}
}

//============================================================================
// COMBOS
//============================================================================
combo RAPID_FIRE_MOD {
	set_val(FIRE, 100);
	wait(24);
	set_val(FIRE, 0);
	wait(24);
}

combo FAST_RELOAD_CANCEL {
	set_val(SW_WEAPON, 100);
	wait(32);
	set_val(SW_WEAPON, 0);
	wait(32);
	set_val(SW_WEAPON, 100);
	wait(32);
	set_val(SW_WEAPON, 0);
}

combo SMART_DROP_SHOT {
	set_val(CIRCLE, 100);
	wait(650);
	set_val(CIRCLE, 0);
}

combo SNAKING_PEEK {
	set_val(CIRCLE, 100);
	wait(56);
	set_val(CIRCLE, 0);
	wait(112);
	set_val(JUMP_R3, 100);
	wait(48);
	set_val(JUMP_R3, 0);
	wait(112);
}

combo QUICK_STAND_UP {
	set_val(JUMP_R3, 100);
	wait(40);
	set_val(JUMP_R3, 0);
	wait(40);
}

combo AUTO_ENEMY_PING {
	set_val(UP, 100);
	wait(32);
	set_val(UP, 0);
	wait(32);
	set_val(UP, 100);
	wait(32);
	set_val(UP, 0);
}

combo STRAFE_SHOT {
	set_val(LX, STRAFE_STRENGTH);
	wait(cur_strafe_t);
	set_val(LX, STRAFE_STRENGTH * -1);
	wait(cur_strafe_t);
}

combo FAST_SLIDE_JUMP {
	set_val(CIRCLE, 100);
	wait(112);
	set_val(CIRCLE, 0);
	wait(56);
	set_val(JUMP_R3, 100);
	wait(48);
	set_val(JUMP_R3, 0);
	wait(24);
}

combo AUTO_TAC_SPRINT {
	set_val(L3_BTN, 100);
	wait(32);
	set_val(L3_BTN, 0);
	wait(48);
}

combo RUMBLE_FEEDBACK {
	set_rumble(RUMBLE_B, 85);
	wait(96);
	reset_rumble();
	wait(80);
	rumble_cnt--;
}

//============================================================================
// FUNCTIONS (SPEICHER, LED & OLED)
//============================================================================
function clamp_val(v, lo, hi) {
	if (v > hi) return hi;
	if (v < lo) return lo;
	return v;
}

function SAVE_VALUES() {
	set_pvar(SPVAR_1, smg_recoil_v);
	set_pvar(SPVAR_2, smg_recoil_h);
	set_pvar(SPVAR_3, ar_recoil_v);
	set_pvar(SPVAR_4, ar_recoil_h);
	rumble_cnt = 1;
	oled_update = TRUE;
}

function DRAW_OLED() {
	cls_oled(OLED_BLACK);
	rect_oled(0, 0, OLED_WIDTH, OLED_HEIGHT, OLED_BLACK, OLED_WHITE);
	line_oled(0, 16, 127, 16, 1, OLED_WHITE);

	print(12, 4, OLED_FONT_SMALL, OLED_WHITE, TXT_TITLE[0]);

	if (plate_timer > 0) {
		print(10, 22, OLED_FONT_SMALL, OLED_WHITE, TXT_PLATE[0]);
	}
	else if (rapid_fire_on) {
		print(14, 22, OLED_FONT_SMALL, OLED_WHITE, TXT_RF_ON[0]);
	}
	else if (!is_ar_profile) {
		print(18, 22, OLED_FONT_SMALL, OLED_WHITE, TXT_SMG[0]);
	}
	else {
		print(18, 22, OLED_FONT_SMALL, OLED_WHITE, TXT_AR[0]);
	}

	print(10, 36, OLED_FONT_SMALL, OLED_WHITE, TXT_VERT[0]);
	print(10, 49, OLED_FONT_SMALL, OLED_WHITE, TXT_HORIZ[0]);

	if (!is_ar_profile) {
		PRINT_NUM(85, 36, smg_recoil_v);
		PRINT_NUM(85, 49, smg_recoil_h);
	}
	else {
		PRINT_NUM(85, 36, ar_recoil_v);
		PRINT_NUM(85, 49, ar_recoil_h);
	}
}

function PRINT_NUM(x, y, val) {
	num_idx = 1;
	if (val < 0) {
		putc_oled(num_idx, 45);
		num_idx++;
		val = abs(val);
	}
	num_n = val;
	if (num_n >= 10) {
		num_c = (num_n / 10) + 48;
		putc_oled(num_idx, num_c);
		num_idx++;
		num_n = num_n % 10;
	}
	num_c = num_n + 48;
	putc_oled(num_idx, num_c);
	puts_oled(x, y, OLED_FONT_SMALL, num_idx, OLED_WHITE);
}

function PLAY_LEDS() {
	if (plate_timer > 0) {
		target_led = LED_YELLOW;
	}
	else if (get_val(ADS) && get_val(FIRE)) {
		target_led = LED_RED;
	}
	else if (!is_ar_profile) {
		target_led = LED_GREEN;
	}
	else {
		target_led = LED_BLUE;
	}

	if (target_led != current_led) {
		color_led(target_led);
		current_led = target_led;
	}
}

function color_led(colour) {
	set_led(LED_1, duint8(colour * 4));
	set_led(LED_2, duint8((colour * 4) + 1));
	set_led(LED_3, duint8((colour * 4) + 2));
	set_led(LED_4, duint8((colour * 4) + 3));
}
```