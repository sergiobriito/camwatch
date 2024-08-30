import requests

class FirmwareServiceImpl:
    def update_firmware(self, url, download_dir):
        self.download_firmware_last_version(url, download_dir)
        self.install_firmware()

    def download_firmware_last_version(self, file_url, download_dir):
        try:
            response = requests.get(file_url, stream=True)
            response.raise_for_status()
            with open(download_dir, 'wb') as output_file:
                for chunk in response.iter_content(chunk_size=4096):
                    if chunk:
                        output_file.write(chunk)
            print(f"Firmware downloaded to {download_dir}")
        except requests.RequestException as e:
            print(f"Error downloading file: {e}")

    def install_firmware(self):
        pass

