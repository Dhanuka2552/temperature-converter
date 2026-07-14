package com.example.tempconverter.service;

import com.example.tempconverter.exception.BadRequestException;
import com.example.tempconverter.exception.UnauthorizedException;
import com.example.tempconverter.model.TemperatureLog;
import com.example.tempconverter.repository.ApiKeyRepository;
import com.example.tempconverter.repository.TemperatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemperatureService {

    private static final double ABSOLUTE_ZERO_C = -273.15;

    private final TemperatureRepository temperatureRepository;
    private final ApiKeyRepository apiKeyRepository;

    // ---- Security -------------------------------------------------------

    public void validateApiKey(String requestKey) {
        if (requestKey == null || requestKey.trim().isEmpty()) {
            throw new UnauthorizedException("API Key missing from HTTP Headers!");
        }

        apiKeyRepository.findByKeyValueAndActiveTrue(requestKey.trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid, inactive, or revoked API Key provided!"));
    }

    // ---- Unit helpers -----------------------------------------------------

    private String normalizeUnit(String unit) {
        if (unit == null) {
            throw new BadRequestException("Unit must be one of C, F, K");
        }
        String u = unit.trim().toUpperCase();
        switch (u) {
            case "C": case "CELSIUS": return "C";
            case "F": case "FAHRENHEIT": return "F";
            case "K": case "KELVIN": return "K";
            default: throw new BadRequestException("Unit must be one of C, F, K");
        }
    }

    private double toCelsius(double value, String unit) {
        switch (unit) {
            case "C": return value;
            case "F": return (value - 32) * (5.0 / 9.0);
            case "K": return value - 273.15;
            default: throw new BadRequestException("Unit must be one of C, F, K");
        }
    }

    private double fromCelsius(double celsius, String unit) {
        switch (unit) {
            case "C": return celsius;
            case "F": return celsius * (9.0 / 5.0) + 32;
            case "K": return celsius + 273.15;
            default: throw new BadRequestException("Unit must be one of C, F, K");
        }
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // ---- Conversion ---------------------------------------------------

    public TemperatureLog convertAndSave(double value, String fromUnitRaw, String toUnitRaw) {
        String fromUnit = normalizeUnit(fromUnitRaw);
        String toUnit = normalizeUnit(toUnitRaw);

        double celsius = toCelsius(value, fromUnit);
        if (celsius < ABSOLUTE_ZERO_C) {
            throw new BadRequestException("Temperature is below absolute zero");
        }

        double result = round2(fromCelsius(celsius, toUnit));

        TemperatureLog log = new TemperatureLog();
        log.setInputTemperature(value);
        log.setInputUnit(fromUnit);
        log.setOutputTemperature(result);
        log.setOutputUnit(toUnit);
        log.setTimestamp(LocalDateTime.now().toString());

        return temperatureRepository.save(log);
    }

    // ---- History / stats --------------------------------------------------

    public List<TemperatureLog> getAllLogs() {
        return temperatureRepository.findAllByOrderByTimestampDesc();
    }

    public List<TemperatureLog> getByUnit(String unit) {
        return temperatureRepository.findByInputUnitIgnoreCase(normalizeUnit(unit));
    }

    public void clearHistory() {
        temperatureRepository.deleteAll();
    }

    public Map<String, Object> getStats() {
        List<TemperatureLog> all = temperatureRepository.findAll();

        String mostCommonPair = all.stream()
                .collect(Collectors.groupingBy(l -> l.getInputUnit() + " -> " + l.getOutputUnit(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return Map.of(
                "total", all.size(),
                "mostCommonPair", mostCommonPair == null ? "" : mostCommonPair
        );
    }

    // ---- Safety check -------------------------------------------------

    public String safetyCheck(double value, String unitRaw) {
        String unit = normalizeUnit(unitRaw);
        double celsius = toCelsius(value, unit);

        if (celsius > 38) {
            return "Warning: " + value + unit + " is dangerously HOT! Stay hydrated.";
        }
        if (celsius < -273.15) {
            throw new BadRequestException("Temperature is below absolute zero");
        }

        return "The temperature is comfortable and safe.";
    }
}
