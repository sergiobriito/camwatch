import requests
import yaml
import time
import json
from requests.exceptions import RequestException
from get_usb_device import get_usb_device

class DeviceServiceImpl:
    def __init__(self):
        try:
            with open("config.yml", 'r') as file:
                config = yaml.safe_load(file)
            self.main_server_url = config.get("main-server-url")
        except FileNotFoundError:
            raise FileNotFoundError("Configuration file not found")

    def register(self):
        devices = []
        try:
            devices = get_usb_device()
            if not devices:
                print("Devices not recognized")
                time.sleep(30)
                return
        except FileNotFoundError as e:
            print(e)
            return
        for device in devices:
            try:
                payload = json.dumps(device)
                response = requests.post(f"{self.main_server_url}/save", data=payload, headers={'Content-Type': 'application/json'})
                response.raise_for_status()
                print("Response:", response.text)
            except RequestException as e:
                print("Error during device save:", e)

    def run_ping(self):
        devices = []
        try:
            devices = get_usb_device()
            if not devices:
                print("Devices not recognized")
                time.sleep(30)
                return
        except FileNotFoundError as e:
            print(e)
            return
        while True:
            self.ping(devices)
            time.sleep(30)

    def ping(self, devices):
        for device in devices:
            try:
                mac_address = device.get("macAddress")
                user_id = device.get("userId")
                payload = json.dumps({"macAddress": mac_address, "userId": user_id})
                response = requests.post(f"{self.main_server_url}/ping", data=payload, headers={'Content-Type': 'application/json'})
                response.raise_for_status()
                print(f"Response from server: {response.text} - {time.strftime('%H:%M:%S')}")
                time.sleep(5)
            except (RequestException, json.JSONDecodeError) as e:
                print("Error during device ping:", e)


