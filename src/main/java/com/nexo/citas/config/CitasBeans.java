package com.nexo.citas.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.citas.domain.reglas.CondicionEvaluador;
import com.nexo.citas.domain.reglas.Motor3280;
import com.nexo.citas.domain.reglas.ReglaProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Alambrado de los beans del Core Domain (Motor 3280) manteniendo el dominio
 * desacoplado de Spring: se inyecta vía constructor la interfaz ReglaProvider.
 */
@Configuration
public class CitasBeans {

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
