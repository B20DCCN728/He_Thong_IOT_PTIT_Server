package com.example.he_thong_iot_ptit.repository;

import com.example.he_thong_iot_ptit.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRespository extends JpaRepository<Device, Integer> {
}
