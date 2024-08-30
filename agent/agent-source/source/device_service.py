import requests
import yaml
import time
import re
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

    def register(self, device_data):
        try:
            response = requests.post(f"{self.main_server_url}/save", data=device_data, headers={'Content-Type': 'application/json'})
            response.raise_for_status()
            print("Response:", response.text)
        except RequestException as e:
            print("Error during device save:", e)

    def ping(self):
        device = ""
        try:
            device = get_usb_device()
            if not device:
                print("Device not recognized")
                time.sleep(30)
                return
        except FileNotFoundError as e:
            print(e)
            return
        try:
            json_object = json.loads(device)
            if not json_object:
                print("Invalid data")
                return
            mac_address = json_object.get("macAddress")
            user_id = json_object.get("userId")
            payload = json.dumps({"macAddress": mac_address, "userId": user_id})
            response = requests.post(f"{self.main_server_url}/ping", data=payload, headers={'Content-Type': 'application/json'})
            response.raise_for_status()
            print(f"Response from server: {response.text} - {time.strftime('%H:%M:%S')}")
        except (RequestException, json.JSONDecodeError) as e:
            print("Error during device ping:", e)

    @staticmethod
    def contains_update_text(response_body):
        return "update required" in response_body.lower()

    @staticmethod
    def extract_url(response_body):
        url_pattern = re.compile(r'https?://\S+')
        match = url_pattern.search(response_body)
        return match.group() if match else None


