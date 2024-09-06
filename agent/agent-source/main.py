import tkinter as tk
from tkinter import simpledialog, messagebox
from threading import Thread
import sys
import yaml
from device_service import DeviceServiceImpl

def read_config(file_path):
    with open(file_path, 'r') as file:
        return yaml.safe_load(file)

def write_config(file_path, data):
    with open(file_path, 'w') as file:
        yaml.dump(data, file)

def update_user_id():
    config_file = "configs/config.yml"
    config_data = read_config(config_file)
    current_user_id = config_data['user-id']
    new_user_id = simpledialog.askstring("Alterar User ID", f"User ID Atual: {current_user_id}\nNovo User ID:")
    if new_user_id:
        config_data['user-id'] = new_user_id
        write_config(config_file, config_data)
        messagebox.showinfo("Sucesso", f"User ID atualizado: {new_user_id}")

def start_service():
    try:
        device_service = DeviceServiceImpl()
        device_service.register()
        device_service.run_ping()
    except FileNotFoundError as e:
        print(f"Error: {e}")
        sys.exit(1)

def restart_service():
    global service_thread
    if service_thread.is_alive():
        print("Restarting the service...")
        service_thread.join(timeout=1)
    service_thread = Thread(target=start_service, daemon=True)
    service_thread.start()

def on_button_hover(button, event, enter=True):
    if enter:
        button.config(bg="#4CAF50", fg="white")
    else:
        button.config(bg="lightgray", fg="black")

def main():
    global service_thread
    
    root = tk.Tk()
    root.title("GoPresence - Agente de monitoramento de câmeras")
    root.geometry("500x250")
    root.configure(bg="#F0F0F0")

    label = tk.Label(root, text="Agente de monitoramento em execução...", font=("Arial", 16, "bold"), bg="#F0F0F0", fg="#333")
    label.pack(pady=20)

    update_button = tk.Button(root, text="Alterar User ID", font=("Arial", 12), width=20, height=2, command=update_user_id)
    update_button.pack(pady=10)
    update_button.bind("<Enter>", lambda e: on_button_hover(update_button, e, True))
    update_button.bind("<Leave>", lambda e: on_button_hover(update_button, e, False))

    restart_button = tk.Button(root, text="Reiniciar Serviço", font=("Arial", 12), width=20, height=2, command=restart_service)
    restart_button.pack(pady=10)
    restart_button.bind("<Enter>", lambda e: on_button_hover(restart_button, e, True))
    restart_button.bind("<Leave>", lambda e: on_button_hover(restart_button, e, False))

    service_thread = Thread(target=start_service, daemon=True)
    service_thread.start()

    def on_closing():
        if service_thread.is_alive():
            print("Shutting down the service thread...")
        root.destroy()
        sys.exit(0)

    root.protocol("WM_DELETE_WINDOW", on_closing)

    root.mainloop()

if __name__ == "__main__":
    main()
