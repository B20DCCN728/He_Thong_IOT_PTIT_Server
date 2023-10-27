package com.example.he_thong_iot_ptit.controller;


import com.example.he_thong_iot_ptit.configuration.mqttconfiguration.MQTTConfiguration;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class DeviceController {

    @Autowired
    MQTTConfiguration.MyGateway myGateway;

    @PostMapping("/sendMessage")
    public ResponseEntity<?> publish(@RequestBody String message) {
        try {
            JsonObject convertObject = new Gson().fromJson(message, JsonObject.class);
            System.out.println(convertObject.get("message").toString());
            myGateway.sendToMqtt(convertObject.get("message").getAsString());
            return ResponseEntity.ok("Success");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error");
        }
    }
}
