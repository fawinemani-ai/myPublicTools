import base64
import json
import logging
import os
import queue
import shutil
import sys
import threading
import time
from datetime import datetime
from pathlib import Path
from typing import Callable, Set, Iterator

import customtkinter as ctk
from dotenv import load_dotenv
from PIL import Image
from watchdog.events import FileSystemEvent, FileSystemEventHandler
from watchdog.observers import Observer

# APIs
from google import genai
from openai import OpenAI

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    datefmt="%H:%M:%S"
)
logger = logging.getLogger("ScreenWatcher")

load_dotenv("config.env")


class Config:
    GEMINI_KEY: str = os.getenv("GEMINI_API_KEY", "")
    GEMINI_MODEL: str = os.getenv("GEMINI_MODEL", "gemini-2.5-flash")
    
    OPENROUTER_KEY: str = os.getenv("OPENROUTER_API_KEY", "")
    OPENROUTER_MODEL: str = os.getenv("OPENROUTER_MODEL", "anthropic/claude-3.5-sonnet")

    WATCH_FOLDER: Path = Path(os.getenv("WATCH_FOLDER", "./screenshots"))
    DONE_FOLDER: Path = WATCH_FOLDER / "erledigt"
    CACHE_FILE: Path = Path(os.getenv("CACHE_FILE", "processed_files.json"))
    PROMPT_TEMPLATE_FILE: Path = Path("prompt_template.txt")
    SUPPORTED_EXTENSIONS: Set[str] = {".png", ".jpg", ".jpeg", ".webp"}
    
    # NEU: Konfigurierbare Werte für bessere Netzwerk-Stabilität & Performance
    BATCH_DELAY: float = 1.0  # Pause nur WENN Bilder zeitgleich eintreffen
    FILE_CHECK_RETRIES: int = int(os.getenv("FILE_CHECK_RETRIES", "15")) # 15 Versuche
    FILE_CHECK_DELAY: float = float(os.getenv("FILE_CHECK_DELAY", "0.4")) # a 0.4 Sekunden = max 6 Sek warten


class HistoryTracker:
    def __init__(self, cache_file: Path):
        self.cache_file = cache_file
        self._lock = threading.Lock()
        self.processed: Set[str] = self._load()

    def _load(self) -> Set[str]:
        if self.cache_file.exists():
            try:
                with open(self.cache_file, "r", encoding="utf-8") as f:
                    data = json.load(f)
                    return set(data) if isinstance(data, list) else set()
            except Exception as e:
                logger.error(f"Fehler beim Laden des Caches: {e}")
        return set()

    def is_processed(self, filename: str) -> bool:
        with self._lock:
            return filename in self.processed

    def mark_processed(self, filename: str):
        with self._lock:
            self.processed.add(filename)
            try:
                temp_file = self.cache_file.with_suffix(".tmp")
                with open(temp_file, "w", encoding="utf-8") as f:
                    json.dump(sorted(list(self.processed)), f, indent=2)
                temp_file.replace(self.cache_file)
            except Exception as e:
                logger.error(f"Fehler beim Speichern des Caches: {e}")


class HybridAnalyzer:
    """Multi-Agent API Analyzer: Versucht Gemini, fällt bei Fehler auf OpenRouter zurück."""
    def __init__(self, template_path: Path):
        self.template_path = template_path
        self.gemini_client = genai.Client(api_key=Config.GEMINI_KEY) if Config.GEMINI_KEY else None
        self.or_client = OpenAI(base_url="https://openrouter.ai/api/v1", api_key=Config.OPENROUTER_KEY) if Config.OPENROUTER_KEY else None

    def _get_prompt(self) -> str:
        if self.template_path.exists():
            try:
                with open(self.template_path, "r", encoding="utf-8") as f:
                    content = f.read().strip()
                    if content:
                        return content
            except Exception as e:
                # KORREKTUR 1: Kein stilles Scheitern mehr, User wird im Log informiert
                logger.warning(f"Konnte prompt_template.txt nicht lesen: {e}. Nutze Standard-Prompt.")
        return "Analysiere dieses Bild direkt und präzise."

    def analyze_image_stream(self, file_path: Path) -> Iterator[str]:
        prompt = self._get_prompt()
        
        # 1. VERSUCH: GEMINI
        if self.gemini_client:
            try:
                logger.info(f"Sende an Gemini ({Config.GEMINI_MODEL})...")
                with Image.open(file_path) as img:
                    response_stream = self.gemini_client.models.generate_content_stream(
                        model=Config.GEMINI_MODEL,
                        contents=[img, prompt]
                    )
                    for chunk in response_stream:
                        if chunk.text:
                            yield chunk.text
                return  
            except Exception as e:
                logger.warning(f"Gemini API fehlgeschlagen: {e}. Wechsle zu Fallback (OpenRouter)...")

        # 2. VERSUCH: OPENROUTER (Fallback)
        if self.or_client:
            try:
                logger.info(f"Sende an OpenRouter ({Config.OPENROUTER_MODEL})...")
                with open(file_path, "rb") as f:
                    img_b64 = base64.b64encode(f.read()).decode('utf-8')
                
                mime_type = "image/png" if file_path.suffix.lower() == ".png" else "image/jpeg"
                
                response = self.or_client.chat.completions.create(
                    model=Config.OPENROUTER_MODEL,
                    messages=[
                        {
                            "role": "user",
                            "content": [
                                {"type": "text", "text": prompt},
                                {"type": "image_url", "image_url": {"url": f"data:{mime_type};base64,{img_b64}"}}
                            ]
                        }
                    ],
                    stream=True
                )
                for chunk in response:
                    if chunk.choices and chunk.choices[0].delta.content:
                        yield chunk.choices[0].delta.content
                return
            except Exception as e:
                raise RuntimeError(f"Beide APIs (Gemini & OpenRouter) sind fehlgeschlagen. Letzter Fehler: {e}")
        
        raise ValueError("Keine API Keys konfiguriert!")


class ScreenshotEventHandler(FileSystemEventHandler):
    def __init__(self, file_queue: queue.Queue, supported_extensions: Set[str], done_folder: Path):
        super().__init__()
        self.file_queue = file_queue
        self.supported_extensions = supported_extensions
        self.done_folder = done_folder

    def on_created(self, event: FileSystemEvent):
        if not event.is_directory:
            self._handle_event(Path(event.src_path))

    def on_modified(self, event: FileSystemEvent):
        if not event.is_directory:
            self._handle_event(Path(event.src_path))

    def _handle_event(self, path: Path):
        if self.done_folder in path.parents:
            return
        if path.suffix.lower() in self.supported_extensions:
            self.file_queue.put(path)


class WatchdogFolderWatcher:
    def __init__(
        self,
        config: type[Config],
        tracker: HistoryTracker,
        analyzer: HybridAnalyzer,
        on_start_cb: Callable[[str, str], None],
        on_chunk_cb: Callable[[str], None],
        on_finish_cb: Callable[[str, str | None], None]
    ):
        self.config = config
        self.tracker = tracker
        self.analyzer = analyzer

        self.on_start_cb = on_start_cb
        self.on_chunk_cb = on_chunk_cb
        self.on_finish_cb = on_finish_cb

        self.file_queue: queue.Queue[Path] = queue.Queue()
        self.observer = Observer()
        self._stop_event = threading.Event()
        self.worker_thread: threading.Thread | None = None
        self.last_process_time: float = 0.0  # NEU: Tracking für intelligentes Batch-Delay

    def start(self):
        self.config.WATCH_FOLDER.mkdir(parents=True, exist_ok=True)
        self.config.DONE_FOLDER.mkdir(parents=True, exist_ok=True)
        self._stop_event.clear()

        existing_files = [
            p for p in self.config.WATCH_FOLDER.iterdir()
            if p.is_file() and p.suffix.lower() in self.config.SUPPORTED_EXTENSIONS
        ]
        existing_files.sort(key=lambda p: p.stat().st_mtime)
        for f in existing_files:
            if not self.tracker.is_processed(f.name):
                self.file_queue.put(f)

        event_handler = ScreenshotEventHandler(
            self.file_queue, 
            self.config.SUPPORTED_EXTENSIONS,
            self.config.DONE_FOLDER
        )
        self.observer.schedule(event_handler, str(self.config.WATCH_FOLDER), recursive=False)
        self.observer.start()

        self.worker_thread = threading.Thread(target=self._process_queue, daemon=True, name="QueueWorker")
        self.worker_thread.start()
        logger.info(f"Watchdog Observer aktiv auf: {self.config.WATCH_FOLDER.resolve()}")

    def stop(self):
        self._stop_event.set()
        if self.observer.is_alive():
            self.observer.stop()
            self.observer.join(timeout=1.0)
        if self.worker_thread and self.worker_thread.is_alive():
            self.worker_thread.join(timeout=1.0)

    def _is_file_ready(self, file_path: Path) -> bool:
        # KORREKTUR 2: Nutzt nun die dynamischen Config-Werte für robustere SMB-Checks
        for _ in range(self.config.FILE_CHECK_RETRIES):
            try:
                with open(file_path, "rb") as f:
                    f.read(1024)
                return True
            except (IOError, PermissionError):
                if self._stop_event.wait(self.config.FILE_CHECK_DELAY):
                    return False
        return False

    def _process_queue(self):
        while not self._stop_event.is_set():
            try:
                file_path: Path = self.file_queue.get(timeout=0.5)
            except queue.Empty:
                continue

            filename = file_path.name
            if not file_path.exists() or self.tracker.is_processed(filename):
                self.file_queue.task_done()
                continue

            if not self._is_file_ready(file_path):
                logger.warning(f"Datei übersprungen (Trotz Wartezeit gelockt): {filename}")
                self.file_queue.task_done()
                continue

            # KORREKTUR 3: Intelligentes Non-Blocking Batch Delay (0 Latenz für den 1. Screenshot)
            time_since_last = time.time() - self.last_process_time
            if time_since_last < self.config.BATCH_DELAY:
                time.sleep(self.config.BATCH_DELAY - time_since_last)
            self.last_process_time = time.time()

            logger.info(f"Starte Analyse für: {filename}")
            timestamp = datetime.now().strftime("%H:%M:%S")

            self.on_start_cb(filename, timestamp)
            full_text = ""

            try:
                stream = self.analyzer.analyze_image_stream(file_path)
                for chunk in stream:
                    if self._stop_event.is_set():
                        break
                    full_text += chunk
                    self.on_chunk_cb(chunk)

                self.tracker.mark_processed(filename)
                
                try:
                    name, ext = file_path.stem, file_path.suffix
                    new_path = self.config.DONE_FOLDER / filename
                    if new_path.exists():
                        new_path = self.config.DONE_FOLDER / f"{name}_{int(time.time())}{ext}"
                    
                    shutil.move(str(file_path), str(new_path))
                    logger.info(f"Datei verschoben: {new_path.name}")
                    
                    log_file = self.config.DONE_FOLDER / f"{new_path.stem}_antwort.txt"
                    with open(log_file, "w", encoding="utf-8") as f:
                        f.write(full_text)

                except Exception as move_err:
                    logger.error(f"Fehler beim Dateihandling: {move_err}")

                self.on_finish_cb(filename, None)

            except Exception as e:
                error_msg = f"Fehler bei Bildanalyse: {e}"
                logger.error(error_msg)
                self.on_finish_cb(filename, error_msg)
            finally:
                self.file_queue.task_done()


class AppUI(ctk.CTk):
    def __init__(self):
        super().__init__()
        self.watcher: WatchdogFolderWatcher | None = None

        self.title("SnapStream AI (Gemini & OpenRouter)")
        self.geometry("950x720")
        self.minsize(640, 450)
        ctk.set_appearance_mode("Dark")
        ctk.set_default_color_theme("blue")

        self.top_frame = ctk.CTkFrame(self, corner_radius=0)
        self.top_frame.pack(fill="x", padx=15, pady=(12, 6))

        self.status_label = ctk.CTkLabel(
            self.top_frame,
            text=f"● Überwache: {Config.WATCH_FOLDER.resolve()}",
            font=ctk.CTkFont(size=13, weight="bold"),
            text_color="#4CAF50"
        )
        self.status_label.pack(side="left", padx=12, pady=10)

        self.copy_btn = ctk.CTkButton(
            self.top_frame,
            text="Text kopieren",
            command=self._copy_to_clipboard,
            state="disabled",
            width=120
        )
        self.copy_btn.pack(side="right", padx=12)

        self.content_frame = ctk.CTkFrame(self, corner_radius=8)
        self.content_frame.pack(fill="both", expand=True, padx=15, pady=(5, 15))

        self.active_textbox: ctk.CTkTextbox | None = None
        self.active_card: ctk.CTkFrame | None = None
        self.current_text: str = ""

        self._show_placeholder()
        self.protocol("WM_DELETE_WINDOW", self.on_close)

    def set_watcher(self, watcher: WatchdogFolderWatcher):
        self.watcher = watcher

    def _copy_to_clipboard(self):
        if self.current_text:
            self.clipboard_clear()
            self.clipboard_append(self.current_text)
            self.update() 
            
            self.copy_btn.configure(text="Kopiert! ✔", fg_color="#2E7D32")
            self.after(2000, lambda: self.copy_btn.configure(text="Text kopieren", fg_color=["#3a7ebf", "#1f538d"]))

    def _show_placeholder(self):
        for widget in self.content_frame.winfo_children():
            widget.destroy()
        self.placeholder = ctk.CTkLabel(
            self.content_frame,
            text="Warte auf neue Screenshots...",
            font=ctk.CTkFont(size=14),
            text_color="gray"
        )
        self.placeholder.pack(expand=True)

    def on_stream_start(self, filename: str, timestamp: str):
        self.current_text = ""
        self.copy_btn.configure(state="disabled")
        
        for widget in self.content_frame.winfo_children():
            widget.destroy()

        self.active_card = ctk.CTkFrame(self.content_frame, corner_radius=8, border_width=1.5, border_color="#1E88E5")
        self.active_card.pack(fill="both", expand=True, padx=8, pady=8)

        header = ctk.CTkFrame(self.active_card, fg_color="transparent")
        header.pack(fill="x", padx=15, pady=(10, 5))

        title = ctk.CTkLabel(header, text=f"📄 {filename}", font=ctk.CTkFont(size=15, weight="bold"))
        title.pack(side="left")

        time_lbl = ctk.CTkLabel(header, text=timestamp, font=ctk.CTkFont(size=13), text_color="gray")
        time_lbl.pack(side="right")

        self.active_textbox = ctk.CTkTextbox(
            self.active_card, font=ctk.CTkFont(size=14, family="Segoe UI"), wrap="word", corner_radius=6, fg_color="#1E1E1E"
        )
        self.active_textbox.pack(fill="both", expand=True, padx=12, pady=(5, 12))
        self.active_textbox.configure(state="disabled")

    def on_stream_chunk(self, chunk: str):
        self.current_text += chunk
        if self.active_textbox and self.active_textbox.winfo_exists():
            self.active_textbox.configure(state="normal")
            self.active_textbox.insert("end", chunk)
            self.active_textbox.see("end")
            self.active_textbox.configure(state="disabled")

    def on_stream_finish(self, filename: str, error_msg: str | None):
        if error_msg and self.active_textbox and self.active_card:
            self.active_card.configure(border_color="#E53935")
            self.active_textbox.configure(state="normal", fg_color="#2B1B1B", font=ctk.CTkFont(size=14, family="Consolas"))
            self.active_textbox.insert("end", f"\n\n[FEHLER]\n{error_msg}")
            self.active_textbox.see("end")
            self.active_textbox.configure(state="disabled")
        else:
            self.copy_btn.configure(state="normal")

    def on_close(self):
        logger.info("Beende Watcher & UI...")
        if self.watcher:
            self.watcher.stop()
        self.destroy()


def main():
    if not Config.GEMINI_KEY and not Config.OPENROUTER_KEY:
        logger.critical("FEHLER: Es muss mindestens ein API-Key (Gemini oder OpenRouter) in der config.env stehen.")
        sys.exit(1)

    tracker = HistoryTracker(Config.CACHE_FILE)
    analyzer = HybridAnalyzer(Config.PROMPT_TEMPLATE_FILE)
    app = AppUI()

    def handle_start(filename: str, timestamp: str):
        app.after(0, app.on_stream_start, filename, timestamp)

    def handle_chunk(chunk: str):
        app.after(0, app.on_stream_chunk, chunk)

    def handle_finish(filename: str, error_msg: str | None):
        app.after(0, app.on_stream_finish, filename, error_msg)

    watcher = WatchdogFolderWatcher(Config, tracker, analyzer, on_start_cb=handle_start, on_chunk_cb=handle_chunk, on_finish_cb=handle_finish)
    
    app.set_watcher(watcher)
    watcher.start()

    try:
        app.mainloop()
    except KeyboardInterrupt:
        logger.info("KeyboardInterrupt empfangen, beende...")
        watcher.stop()
        sys.exit(0)

if __name__ == "__main__":
    main()