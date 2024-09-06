# Get the non-loopback IP address of the PC
$ipAddress = (Get-NetIPAddress -AddressFamily IPv4 | Where-Object { $_.AddressState -eq 'Preferred' -and $_.IPAddress -ne '127.0.0.1' }).IPAddress

# Retrieve USB devices and display desired properties
$devices = Get-PnpDevice -PresentOnly |
    Where-Object { $_.InstanceId -match '^USB' -and $_.Class -eq 'MEDIA' } |
    ForEach-Object {
        $device = $_

        # Extract the serial number from the InstanceId and remove special characters
        $serialNumberRaw = ($device.InstanceId -split '\\')[-1]  # The serial number is usually the last segment
        $serialNumber = $serialNumberRaw -replace '[^\w]', ''  # Remove all non-alphanumeric characters

        # Attempt to identify the model and friendly name
        $model = $device.FriendlyName -or $device.Description
        $friendlyName = $device.FriendlyName

        # Retrieve firmware version using
        $deviceDetails = Get-CimInstance -ClassName Win32_PnPEntity | Where-Object { $_.PNPDeviceID -eq $device.InstanceId }
        $firmwareVersion = $deviceDetails.DriverVersion

        # Set default firmware version if not available
        if (-not $firmwareVersion) {
            $firmwareVersion = '1.0.0'
        }

        # Create a custom object to display the desired properties including IP address
        [PSCustomObject]@{
            model            = $friendlyName
            ip               = $ipAddress
            serialNumber     = $serialNumber
            macAddress       = $serialNumber
            firmwareVersion  = $firmwareVersion
        }
    }

# Convert the collection of devices to JSON format and output it
$devices | ConvertTo-Json -Depth 3
