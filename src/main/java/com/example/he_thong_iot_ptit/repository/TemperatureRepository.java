package com.example.he_thong_iot_ptit.repository;

import com.example.he_thong_iot_ptit.model.Sensor;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemperatureRepository extends JpaRepository<Sensor, Integer> {
}
