import subprocess
import platform
import socket
import hashlib
import json
import yaml
    
def get_usb_device():
    os_name = platform.system()
    if "Windows" in os_name:
        return get_usb_devices_windows()
    else:
        return "Windows not found."

def get_usb_devices_windows():
    try:
        config = read_yaml_file("config.yml")
        script_path = config.get("get-usb-device-script")
        user_id = config.get("user-id")

        result = subprocess.run(
            ["powershell.exe", "-ExecutionPolicy", "Bypass", "-File", script_path],
            capture_output=True,
            text=True,
            check=True
        )

        devices = result.stdout.strip()
        if not devices:
            return "{}"
        if devices[0] == '[':
            devices_list = json.loads(devices)
            for device in devices_list:
                add_metadata(device, user_id)
        else:
            devices_list = []
            devices_list.append(json.loads(devices))
            for device in devices_list:
                add_metadata(device, user_id)
        return devices_list
    
    except FileNotFoundError as e:
        print(f"Configuration file not found: {e}")
        return "{}"
    except subprocess.CalledProcessError as e:
        print(f"Error executing script: {e}")
        return "{}"


def add_metadata(device, user_id,):
    add_user_id(device, user_id)
    add_device_local(device)

def add_user_id(device, user_id):
    device["userId"] = user_id

def add_device_local(device):
    device["deviceLocal"] = socket.gethostname()

def get_local_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(('8.8.8.8', 80))
        local_ip = s.getsockname()[0]
    except Exception as e:
        print(f"Error retrieving IP address: {e}")
        local_ip = 'unknown'
    finally:
        s.close()
    return local_ip

def hash_serial_and_product(serial_number, product_id):
    hash_input = f"{serial_number}-{product_id}".encode('utf-8')
    hash_object = hashlib.sha256(hash_input)
    return hash_object.hexdigest()[:10]

def read_yaml_file(filepath):
    with open(filepath, 'r') as file:
        return yaml.safe_load(file)