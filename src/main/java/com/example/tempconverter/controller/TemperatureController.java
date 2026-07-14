package com.example.tempconverter.controller;

import com.example.tempconverter.model.TemperatureLog;
import com.example.tempconverter.service.TemperatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/temperatures")
@RequiredArgsConstructor
public class TemperatureController {

    private final TemperatureService temperatureService;

    // POST /api/temperatures/convert?value=100&from=C&to=F
    @PostMapping("/convert")
    public TemperatureLog convert(
            @RequestHeader("X-API-KEY") String apiKey,
            @RequestParam double value,
            @RequestParam String from,
            @RequestParam String to) {

        temperatureService.validateApiKey(apiKey);
        return temperatureService.convertAndSave(value, from, to);
    }

    // GET /api/temperatures/history
    @GetMapping("/history")
    public List<TemperatureLog> getHistory(
            @RequestHeader("X-API-KEY") String apiKey) {

        temperatureService.validateApiKey(apiKey);
        return temperatureService.getAllLogs();
    }

    // DELETE /api/temperatures/history
    @DeleteMapping("/history")
    public Map<String, Boolean> clearHistory(
            @RequestHeader("X-API-KEY") String apiKey) {

        temperatureService.validateApiKey(apiKey);
        temperatureService.clearHistory();
        return Map.of("success", true);
    }

    // GET /api/temperatures/history/filter?unit=C
    @GetMapping("/history/filter")
    public List<TemperatureLog> filterHistory(
            @RequestHeader("X-API-KEY") String apiKey,
            @RequestParam String unit) {

        temperatureService.validateApiKey(apiKey);
        return temperatureService.getByUnit(unit);
    }

    // GET /api/temperatures/warning-check?value=40&unit=C
    @GetMapping("/warning-check")
    public String checkSafety(
            @RequestHeader("X-API-KEY") String apiKey,
            @RequestParam double value,
            @RequestParam String unit) {

        temperatureService.validateApiKey(apiKey);
        return temperatureService.safetyCheck(value, unit);
    }

    // GET /api/temperatures/stats
    @GetMapping("/stats")
    public Map<String, Object> stats(
            @RequestHeader("X-API-KEY") String apiKey) {

        temperatureService.validateApiKey(apiKey);
        return temperatureService.getStats();
    }
}
