package com.example.he_thong_iot_ptit.controller;


import com.example.he_thong_iot_ptit.configuration.mqttconfiguration.MQTTConfiguration;
import com.example.he_thong_iot_ptit.model.Device;
import com.example.he_thong_iot_ptit.model.Sensor;
import com.example.he_thong_iot_ptit.repository.DeviceRespository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class DeviceController {

    @Autowired
    MQTTConfiguration.MyFanGateway myFanGateway;

    @Autowired
    MQTTConfiguration.MyLedGateway myLedGateway;

    @Autowired
    DeviceRespository deviceRespository;

    // Control Fan
    @PostMapping("/fan-device")
    public ResponseEntity<?> controlFan(@RequestBody String message) {
        try {
            JsonObject convertObject = new Gson().fromJson(message, JsonObject.class);
            System.out.println(convertObject.get("message").toString());

            String request = convertObject.get("message").getAsString();
            myFanGateway.sendToMqtt(request);

            Device fan = new Device();
            fan.setDeviceName("FAN");
            fan.setState(request);
            fan.setTimeStamp(LocalDateTime.now());
            deviceRespository.save(fan);

            return ResponseEntity.ok("Success");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error");
        }
    }

    // Control Led
    @PostMapping("/led-device")
    public ResponseEntity<?> controlLed(@RequestBody String message) {
        try {
            JsonObject convertObject = new Gson().fromJson(message, JsonObject.class);
            System.out.println(convertObject.get("message").toString());
            myLedGateway.sendToMqtt(convertObject.get("message").getAsString());

            String request = convertObject.get("message").getAsString();
            myLedGateway.sendToMqtt(request);

            Device led = new Device();
            led.setDeviceName("LED");
            led.setState(request);
            led.setTimeStamp(LocalDateTime.now());
            deviceRespository.save(led);

            return ResponseEntity.ok("Success");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error");
        }
    }

    @GetMapping("/getAllDeviceData")
    public List<Device> getAllDeviceData() {
        List<Device> deviceData = deviceRespository.findAll();
        Collections.reverse(deviceData);
        return deviceData;
    }

}
