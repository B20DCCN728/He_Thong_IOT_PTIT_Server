package com.example.he_thong_iot_ptit.controller;

import com.example.he_thong_iot_ptit.model.Sensor;
import com.example.he_thong_iot_ptit.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SensorController {
    @Autowired
    private SensorRepository sensorRepository;

    @GetMapping("/getAllSensorData")
    public List<Sensor> getAllSensorData() {
        return sensorRepository.findAll();
    }


}
