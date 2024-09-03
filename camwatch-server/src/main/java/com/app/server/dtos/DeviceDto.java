package com.app.server.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class DeviceDto {

    @NotNull(message = "Model cannot be null")
    @Size(max = 100)
    private String model;
    @Size(max = 20)
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "Firmware version must be in the format x.x.x, where x is a number.")
    private String firmwareVersion;
    @Size(max = 100)
    private String ip;
    @Size(max = 100)
    private String serialNumber;
    @Size(max = 100)
    private String deviceLocal;
    private String macAddress;
    private UUID userId;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    

}

