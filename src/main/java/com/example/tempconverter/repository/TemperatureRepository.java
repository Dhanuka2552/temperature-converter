package com.example.tempconverter.repository;

import com.example.tempconverter.model.TemperatureLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemperatureRepository
        extends MongoRepository<TemperatureLog, String> {

    // Filter records by input unit (case insensitive)
    List<TemperatureLog> findByInputUnitIgnoreCase(String inputUnit);

    // Most recent conversions first
    List<TemperatureLog> findAllByOrderByTimestampDesc();
}
