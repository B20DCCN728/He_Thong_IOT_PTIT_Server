package com.example.he_thong_iot_ptit.repository;

import com.example.he_thong_iot_ptit.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Integer> {
}
