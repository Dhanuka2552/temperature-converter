package com.example.tempconverter.config;

import com.example.tempconverter.model.ApiKey;
import com.example.tempconverter.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApiKeyBootstrapConfig {

    private final ApiKeyRepository apiKeyRepository;

    @Value("${app.security.default-api-key:SUPER-SECRET-DEV-KEY-2552}")
    private String defaultApiKey;

    @Value("${app.security.default-client-name:ui-dev-client}")
    private String defaultClientName;

    @Bean
    public CommandLineRunner ensureDefaultApiKey() {
        return args -> {
            String normalizedKey = defaultApiKey == null ? "" : defaultApiKey.trim();

            if (normalizedKey.isEmpty()) {
                return;
            }

            apiKeyRepository.findByKeyValue(normalizedKey)
                    .map(existing -> {
                        if (!existing.isActive()) {
                            existing.setActive(true);
                            return apiKeyRepository.save(existing);
                        }
                        return existing;
                    })
                    .orElseGet(() -> apiKeyRepository.save(new ApiKey(
                            null,
                            normalizedKey,
                            defaultClientName,
                            true
                    )));
        };
    }
}
