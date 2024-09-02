import tkinter as tk
from threading import Thread
import time
import sys
from device_service import DeviceServiceImpl

def start_service():
    try:
        device_service = DeviceServiceImpl()
        device_service.register()
        device_service.run_ping()
    except FileNotFoundError as e:
        print(f"Error: {e}")
        sys.exit(1)

def main():
    root = tk.Tk()
    root.title("GoPresence - Agente de monitoramento de câmeras")
    root.geometry("500x100")
    label = tk.Label(root, text="Agente de monitoramento em execução...", font=("Arial", 14))
    label.pack(pady=20)
    service_thread = Thread(target=start_service, daemon=True)
    service_thread.start()

    def on_closing():
        root.destroy()
        sys.exit(0)

    root.protocol("WM_DELETE_WINDOW", on_closing)
    root.mainloop()

if __name__ == "__main__":
    main()
