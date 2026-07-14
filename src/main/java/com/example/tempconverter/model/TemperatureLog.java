package com.example.tempconverter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "conversions")
public class TemperatureLog {

    @Id
    private String id;

    private double inputTemperature;
    private String inputUnit;

    private double outputTemperature;
    private String outputUnit;

    private String timestamp;
}
