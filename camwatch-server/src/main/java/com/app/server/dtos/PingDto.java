package com.app.server.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public class PingDto {

    @NotBlank(message = "Device MAC Address is required.")
    private String macAddress;
    private UUID userId;

    public @NotBlank(message = "Device MAC Address is required.") String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(@NotBlank(message = "Device MAC Address is required.") String macAddress) {
        this.macAddress = macAddress;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    
}

