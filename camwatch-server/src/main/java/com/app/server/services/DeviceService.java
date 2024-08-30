package com.app.server.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.app.server.dtos.DeviceDto;
import com.app.server.models.DeviceModel;

public interface DeviceService {
    public DeviceModel save(DeviceDto deviceDto);

    public DeviceModel update(DeviceDto deviceDto, DeviceModel deviceModel);

    public void saveStatus(DeviceModel deviceModel);

    public void ping(DeviceModel deviceModel);

    public Optional<DeviceModel> findByMacAddress(String macAddress);

    public Optional<DeviceModel> findById(UUID id);

    public List<Object> findAllDevicesById(UUID id);
}
