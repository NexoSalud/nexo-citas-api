package com.nexo.citas.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.citas.domain.reglas.CondicionEvaluador;
import com.nexo.citas.domain.reglas.Motor3280;
import com.nexo.citas.domain.reglas.ReglaProvider;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Alambrado de los beans del Core Domain (Motor 3280) manteniendo el dominio
 * desacoplado de Spring: se inyecta vía constructor la interfaz ReglaProvider.
 */
@Configuration
@EnableCaching
public class CitasBeans {

    @Bean
    public CacheManager cacheManager(
            @Value("${nexo.cache.ttl-seconds:15}") long ttlSeconds,
            @Value("${nexo.cache.max-size:1000}") long maxSize) {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                "appointmentsByPatient", "appointmentById", "appointmentAvailability");
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(java.time.Duration.ofSeconds(ttlSeconds))
                .maximumSize(maxSize));
        return manager;
    }

    @Bean
    public CondicionEvaluador condicionEvaluador() {
        return new CondicionEvaluador();
    }

    @Bean
    public Motor3280 motor3280(ReglaProvider reglaProvider, CondicionEvaluador condicionEvaluador,
                               ObjectMapper objectMapper) {
        return new Motor3280(reglaProvider, condicionEvaluador, objectMapper);
    }

    /**
     * Cliente HTTP para integraciones sincronas con otros microservicios
     * (ej. nexo-personal-api / Agenda Medica). No forma parte del Core Domain.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
