import time
import os
import ctypes
from ctypes import wintypes
import mss
from datetime import datetime
from dotenv import load_dotenv
from PIL import Image, ImageChops, ImageStat

# Umgebungsvariablen laden
load_dotenv("config.env")

SAVE_FOLDER = os.getenv("SAVE_FOLDER", "./captures")
DIFFERENCE_THRESHOLD = float(os.getenv("DIFFERENCE_THRESHOLD", "1.5"))

def get_image_difference(img1, img2):
    """Berechnet die durchschnittliche Pixelabweichung zur Erkennung von Inhaltsänderungen."""
    if img1 is None or img2 is None:
        return float('inf')
    diff = ImageChops.difference(img1, img2)
    stat = ImageStat.Stat(diff)
    return stat.mean[0]

def get_active_chrome_bbox():
    """Prüft über Windows-APIs, ob Google Chrome das aktive Vordergrundfenster ist."""
    try:
        hwnd = ctypes.windll.user32.GetForegroundWindow()
        if not hwnd:
            return None
            
        pid = wintypes.DWORD()
        ctypes.windll.user32.GetWindowThreadProcessId(hwnd, ctypes.byref(pid))
        
        PROCESS_QUERY_INFORMATION = 0x0400
        PROCESS_VM_READ = 0x0010
        h_process = ctypes.windll.kernel32.OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, False, pid.value)
        
        if h_process:
            buf_size = 260
            filename = ctypes.create_unicode_buffer(buf_size)
            if ctypes.windll.psapi.GetModuleFileNameExW(h_process, None, filename, buf_size):
                ctypes.windll.kernel32.CloseHandle(h_process)
                exe_path = filename.value
                
                if "chrome.exe" in exe_path.lower():
                    rect = wintypes.RECT()
                    ctypes.windll.user32.GetWindowRect(hwnd, ctypes.byref(rect))
                    
                    width = rect.right - rect.left
                    height = rect.bottom - rect.top
                    
                    if width > 300 and height > 300:
                        return {
                            'left': max(0, rect.left),
                            'top': max(0, rect.top),
                            'width': width,
                            'height': height
                        }
            ctypes.windll.kernel32.CloseHandle(h_process)
    except Exception:
        pass
        
    return None

def start_capture(interval=2):
    os.makedirs(SAVE_FOLDER, exist_ok=True)
    previous_compare_image = None
    
    with mss.mss() as sct:
        while True:
            bbox = get_active_chrome_bbox()
            
            if bbox:
                try:
                    sct_img = sct.grab(bbox)
                    
                    current_image = Image.frombytes("RGB", sct_img.size, sct_img.bgra, "raw", "BGRX")
                    compare_image = current_image.resize((400, 300)).convert("L")
                    
                    diff_score = get_image_difference(previous_compare_image, compare_image)
                    
                    if diff_score > DIFFERENCE_THRESHOLD:
                        timestamp = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
                        filepath = os.path.join(SAVE_FOLDER, f"capture_{timestamp}.jpg")
                        
                        # Als JPEG (.jpg) speichern
                        current_image.save(filepath, "JPEG", quality=90)
                        
                        # Google Drive Sync manuell anstupfen (File-Touch / Metadaten-Update)
                        os.utime(filepath, None)
                        
                        previous_compare_image = compare_image
                except Exception:
                    pass
            
            time.sleep(interval)

if __name__ == "__main__":
    start_capture(interval=2)