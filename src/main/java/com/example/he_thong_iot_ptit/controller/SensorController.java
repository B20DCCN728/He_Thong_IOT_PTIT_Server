package com.example.he_thong_iot_ptit.controller;

import com.example.he_thong_iot_ptit.model.Sensor;
import com.example.he_thong_iot_ptit.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Collections;

@RestController
public class SensorController {
    @Autowired
    private SensorRepository sensorRepository;

    @GetMapping("/getAllSensorData")
    public List<Sensor> getAllSensorData() {
        List<Sensor> sensorData = sensorRepository.findAll();
        Collections.reverse(sensorData);
        return sensorData;
    }


}
