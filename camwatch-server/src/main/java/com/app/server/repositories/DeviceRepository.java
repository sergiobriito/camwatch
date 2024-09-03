package com.app.server.repositories;

import com.app.server.models.DeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceModel, UUID> {

    Optional<DeviceModel> findById(UUID id);
    Optional<DeviceModel> findByMacAddress(String macAddress);
    DeviceModel save(DeviceModel deviceModel);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Status (DEVICE_ID, STATUS, LAST_PING) VALUES (:id, 'online', CURRENT_TIMESTAMP AT TIME ZONE 'America/Sao_Paulo')",
            nativeQuery = true)
    void saveStatus(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Status SET STATUS = 'online', LAST_PING = CURRENT_TIMESTAMP AT TIME ZONE 'America/Sao_Paulo' WHERE DEVICE_ID = :id",
            nativeQuery = true)
    void ping(@Param("id") UUID id);

    @Query(value = "SELECT \n" + //
                        "IP,SERIAL_NUMBER,MODEL,STATUS,LAST_PING,DEVICE_LOCAL,Devices.id \n" + //
                        "FROM Devices \n" + //
                        "LEFT JOIN Status ON (Devices.ID = Status.DEVICE_ID) \n" + //
                        "LEFT JOIN Users ON (Devices.user_id = Users.user_id) \n" + //
                        "WHERE Users.user_id = :id",
            nativeQuery = true)
    List<Object> findAllDevicesById(UUID id);
}
