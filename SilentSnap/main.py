import time
import os
import mss
from datetime import datetime
from dotenv import load_dotenv

# config.env explizit laden
load_dotenv("config.env")

# Pfad aus Environment auslesen (Fallback auf Standard, falls nicht definiert)
SAVE_FOLDER = os.getenv("SAVE_FOLDER", "./captures")

def start_stealth_capture(interval=2):
    # Ordner erstellen, falls er nicht existiert (optimierter Einzeiler)
    os.makedirs(SAVE_FOLDER, exist_ok=True)
    
    with mss.mss() as sct:
        while True:
            timestamp = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
            filepath = os.path.join(SAVE_FOLDER, f"capture_{timestamp}.png")
            
            sct.shot(mon=1, output=filepath)
            time.sleep(interval)

if __name__ == "__main__":
    start_stealth_capture(interval=2)