import time
import os
import ctypes
from ctypes import wintypes
import mss
from datetime import datetime
from dotenv import load_dotenv
from PIL import Image, ImageChops, ImageStat

load_dotenv("config.env")

SAVE_FOLDER = os.getenv("SAVE_FOLDER", "./captures")
DIFFERENCE_THRESHOLD = float(os.getenv("DIFFERENCE_THRESHOLD", "1.5"))

def get_image_difference(img1, img2):
    """Berechnet die durchschnittliche Pixelabweichung."""
    if img1 is None or img2 is None:
        return float('inf')
    diff = ImageChops.difference(img1, img2)
    stat = ImageStat.Stat(diff)
    return stat.mean[0]

def get_active_window_bbox():
    """Ermittelt unauffällig das aktive Fenster."""
    try:
        hwnd = ctypes.windll.user32.GetForegroundWindow()
        if hwnd:
            rect = wintypes.RECT()
            ctypes.windll.user32.GetWindowRect(hwnd, ctypes.byref(rect))
            width = rect.right - rect.left
            height = rect.bottom - rect.top
            if width > 100 and height > 100:
                return {
                    'left': max(0, rect.left),
                    'top': max(0, rect.top),
                    'width': width,
                    'height': height
                }
    except Exception:
        pass
    return None

def start_smart_capture(check_interval=1, stable_delay=2):
    """
    check_interval: Prüft jede Sekunde im Hintergrund.
    stable_delay: Wartet, bis das Bild X Sekunden lang stillsteht, bevor es speichert.
    """
    os.makedirs(SAVE_FOLDER, exist_ok=True)
    last_saved_image = None
    previous_image = None
    last_change_time = time.time()
    
    with mss.mss() as sct:
        while True:
            try:
                bbox = get_active_window_bbox()
                if bbox:
                    sct_img = sct.grab(bbox)
                    current_image = Image.frombytes("RGB", sct_img.size, sct_img.bgra, "raw", "BGRX")
                    compare_image = current_image.resize((400, 300)).convert("L")
                    
                    # Prüfen, ob sich gerade etwas ändert (z.B. beim Scrollen)
                    diff_score = get_image_difference(previous_image, compare_image)
                    previous_image = compare_image
                    
                    current_time = time.time()
                    
                    if diff_score > DIFFERENCE_THRESHOLD:
                        # Es wird noch gescrollt / bewegt -> Timer zurücksetzen
                        last_change_time = current_time
                    else:
                        # Bild steht still. Prüfen, ob es lang genug stillsteht UND ob es ein neues Bild ist
                        if (current_time - last_change_time >= stable_delay):
                            
                            # Jetzt prüfen, ob sich das stationäre Bild vom letzten *gespeicherten* unterscheidet
                            save_diff = get_image_difference(last_saved_image, compare_image)
                            
                            if save_diff > DIFFERENCE_THRESHOLD:
                                timestamp = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
                                filepath = os.path.join(SAVE_FOLDER, f"capture_{timestamp}.jpg")
                                
                                current_image.save(filepath, "JPEG", quality=90)
                                os.utime(filepath, None) # Hält den Cloud-Sync frisch
                                
                                last_saved_image = compare_image
            except Exception:
                pass
            
            time.sleep(check_interval)

if __name__ == "__main__":
    start_smart_capture()