import requests
import yaml
import time
import json
import logging
from requests.exceptions import RequestException
from get_usb_device import get_usb_device

logging.basicConfig(filename="configs/service.log", 
                    level=logging.INFO, 
                    format='%(asctime)s %(levelname)s: %(message)s')

class DeviceServiceImpl:
    def __init__(self):
        try:
            with open("configs/config.yml", 'r') as file:
                config = yaml.safe_load(file)
            self.main_server_url = config.get("main-server-url")
        except FileNotFoundError as e:
            logging.error("Configuration file not found")
            raise FileNotFoundError("Configuration file not found") from e

    def register(self):
        devices = get_usb_device()
        if not devices:
            logging.warning("Devices not recognized")
            time.sleep(30)
            return
        
        for device in devices:
            try:
                payload = json.dumps(device)
                response = requests.post(f"{self.main_server_url}/save", 
                                         data=payload, 
                                         headers={'Content-Type': 'application/json'})
                response.raise_for_status()
                print(f"Device saved successfully: {device}")
            except RequestException as e:
                logging.error(f"Error during device save for {device}: {e}")

    def run_ping(self):
        while True:
            devices = get_usb_device()
            if not devices:
                logging.warning("Devices not recognized")
                return
            self.ping(devices)
            time.sleep(10)

    def ping(self, devices):
        for device in devices:
            try:
                mac_address = device.get("macAddress")
                user_id = device.get("userId")
                payload = json.dumps({"macAddress": mac_address, "userId": user_id})
                response = requests.post(f"{self.main_server_url}/ping", 
                                         data=payload, 
                                         headers={'Content-Type': 'application/json'})
                response.raise_for_status()
                print(response.text)
            except (RequestException, json.JSONDecodeError) as e:
                logging.error(f"Error during device ping: {device}, {e}")
