package com.app.server.services.implement;

import com.app.server.dtos.DeviceDto;
import com.app.server.models.DeviceModel;
import com.app.server.repositories.DeviceRepository;
import com.app.server.services.DeviceService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public DeviceModel save(DeviceDto deviceDto) {
        DeviceModel deviceModel = new DeviceModel();
        BeanUtils.copyProperties(deviceDto, deviceModel);
        ZonedDateTime nowInBrazil = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        LocalDateTime now = nowInBrazil.toLocalDateTime();
        deviceModel.setCreatedAt(now);
        deviceModel.setUpdatedAt(now);
        DeviceModel savedDevice = deviceRepository.save(deviceModel);
        saveStatus(savedDevice);
        return savedDevice;
    }

    public DeviceModel update(DeviceDto deviceDto, DeviceModel deviceModel) {
        BeanUtils.copyProperties(deviceDto, deviceModel,  "id");
        deviceModel.setUpdatedAt(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        DeviceModel savedDevice = deviceRepository.save(deviceModel);
        saveStatus(savedDevice);
        return savedDevice;
    }

    public void delete(UUID id){
        deviceRepository.deleteById(id);
    };

    public void saveStatus(DeviceModel deviceModel) {
        deviceRepository.saveStatus(deviceModel.getId());
    }

    public void ping(DeviceModel deviceModel) {
        deviceRepository.ping(deviceModel.getId());
    }

    public Optional<DeviceModel> findByMacAddress(String macAddress) {
        return deviceRepository.findByMacAddress(macAddress);
    }

    public Optional<DeviceModel> findById(UUID id) {
        return deviceRepository.findById(id);
    }

    public List<Object> findAllDevicesById(UUID id) {
        return deviceRepository.findAllDevicesById(id);
    }

}
