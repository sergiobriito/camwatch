package com.app.server.controllers;

import com.app.server.dtos.DeviceDto;
import com.app.server.dtos.PingDto;
import com.app.server.models.DeviceModel;
import com.app.server.models.UserModel;
import com.app.server.services.implement.DeviceServiceImpl;
import com.app.server.services.implement.UserServiceImpl;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/cam-watch/devices")
public class DeviceController {

    private final DeviceServiceImpl deviceServiceImpl;
    private final UserServiceImpl userServiceImpl;

    public DeviceController(DeviceServiceImpl deviceServiceImpl, UserServiceImpl userServiceImpl) {
        this.deviceServiceImpl = deviceServiceImpl;
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("/save")
    @Transactional
    public ResponseEntity<Object> saveCamera(@RequestBody @Valid DeviceDto deviceDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid data");
        }

        boolean isIdInUse = deviceServiceImpl.findByMacAddress(deviceDto.getMacAddress()).isPresent();
        if (isIdInUse) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Device Mac Address is already in use.");
        }

        Optional<UserModel> user = userServiceImpl.findUserById(deviceDto.getUserId());
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found with the provided ID.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(deviceServiceImpl.save(deviceDto));
    }

    @PutMapping("/update")
    @Transactional
    public ResponseEntity<Object> updateCamera(@RequestBody @Valid DeviceDto deviceDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid data");
        }

        Optional<DeviceModel> deviceModel = deviceServiceImpl.findByMacAddress(deviceDto.getMacAddress());
        if (deviceModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No device found with the provided MAC Address.");
        }

        Optional<UserModel> user = userServiceImpl.findUserById(deviceDto.getUserId());
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found with the provided ID.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(deviceServiceImpl.update(deviceDto, deviceModel.get()));
    }

    @PostMapping("/ping")
    @Transactional
    public ResponseEntity<Object> ping(@RequestBody @Valid PingDto pingDto) {
        Optional<DeviceModel> deviceModelOpt = deviceServiceImpl.findByMacAddress(pingDto.getMacAddress());
        if (!deviceModelOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No device found with the provided MAC Address.");
        }

        Optional<UserModel> user = userServiceImpl.findUserById(pingDto.getUserId());
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found with the provided ID.");
        }

        DeviceModel deviceModel = deviceModelOpt.get();
        deviceServiceImpl.ping(deviceModel);
        String response = "Ping received - " + deviceModel.getMacAddress();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<Object> getDeviceById(@PathVariable(value = "id") UUID id) {
        Optional<UserModel> user = userServiceImpl.findUserById(id);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found with the provided ID.");
        }
        List<Object> devices = deviceServiceImpl.findAllDevicesById(id);
        return ResponseEntity.status(HttpStatus.OK).body(devices);
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<Object> deleteDevice(@PathVariable(value = "id") UUID id) {
        Optional<DeviceModel> deviceModelOpt = deviceServiceImpl.findById(id);
        if (!deviceModelOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No device found with the provided ID.");
        }
        deviceServiceImpl.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("Device deleted;");
    }

}
