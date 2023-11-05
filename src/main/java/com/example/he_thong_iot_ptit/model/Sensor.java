package com.example.he_thong_iot_ptit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Sensor")
@AllArgsConstructor()
@NoArgsConstructor
@Data
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime timeStamp;
    private double temperature;
    private double humidity;
    private double lightValue;
    private double voltage;
    public Sensor(
            LocalDateTime timeStamp,
            double temperature,
            double humidity,
            double lightValue,
            double voltage
    ) {
        this.timeStamp = timeStamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.lightValue = lightValue;
        this.voltage = voltage;
    }
}