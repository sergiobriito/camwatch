import subprocess
import platform
import socket
import hashlib
import json
import yaml


def read_yaml_file(filepath):
    with open(filepath, 'r') as file:
        return yaml.safe_load(file)
    
def get_usb_device():
    os_name = platform.system()
    if "Windows" in os_name:
        return get_usb_device_windows()
    else:
        return get_usb_device_unix()

def get_usb_device_windows():
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

        json_string = result.stdout.strip()
        if not json_string:
            return "{}"

        json_string = add_user_id(json_string, user_id)
        return json_string
    
    except FileNotFoundError as e:
        print(f"Configuration file not found: {e}")
        return "{}"
    except subprocess.CalledProcessError as e:
        print(f"Error executing script: {e}")
        return "{}"

def get_usb_device_unix():
    import pyudev
    try:
        config = read_yaml_file("config.yml")
        user_id = config.get("user-id")
        context = pyudev.Context()
        usb_devices = []
        local_ip = get_local_ip()
        for device in context.list_devices(subsystem='usb', DEVTYPE='usb_device'):
            manufacturer = device.get('ID_VENDOR', '')
            product = device.get('ID_MODEL', '')
            if 'Integrated' in manufacturer or 'Integrated' in product:
                continue
            if 'Camera' in product or 'Tenveo' in product or 'presence' in product or '360' in product or 'Camera' in manufacturer or 'Tenveo' in manufacturer or 'presence' in manufacturer or '360' in manufacturer:
                serial_number = device.get('ID_SERIAL', '')
                product_id = device.get('ID_MODEL_ID', '')
                hashed_serial_product = hash_serial_and_product(serial_number, product_id)

                device_info = {
                    'model': product,
                    'ip': local_ip,
                    'serialNumber': hashed_serial_product,
                    'macAddress': hashed_serial_product, 
                    'firmwareVersion': '1.0.0',
                }
                usb_devices.append(device_info)
        json_string = json.dumps(usb_devices[0])
        json_string = add_user_id(json_string, user_id)
        return json_string
    except FileNotFoundError as e:
            print(f"Configuration file not found: {e}")
            return "{}"

def add_user_id(json_string, user_id):
    try:
        json_object = json.loads(json_string)
        json_object["userId"] = user_id
        return json.dumps(json_object)
    except json.JSONDecodeError as e:
        print(f"Error decoding JSON: {e}")
        return json_string

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