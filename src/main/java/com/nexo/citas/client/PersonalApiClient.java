package com.nexo.citas.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Cliente REST hacia nexo-personal-api (Agenda Medica). Se usa desde
 * CitaService para validar disponibilidad real de agenda antes de crear
 * una cita y para exponer el endpoint de disponibilidad.
 *
 * No propaga excepciones crudas de RestTemplate: un 404 en getAgenda se
 * traduce en Optional.empty(); cualquier otro fallo de comunicacion
 * (timeout, conexion rechazada, 5xx) se deja propagar como
 * RestClientException para que la capa de servicio decida como
 * traducirlo a una respuesta HTTP clara.
 */
@Component
public class PersonalApiClient {

    private final RestTemplate restTemplate;
    private final String personalServiceUrl;

    public PersonalApiClient(RestTemplate restTemplate,
                              @Value("${nexo.personal-service.url}") String personalServiceUrl) {
        this.restTemplate = restTemplate;
        this.personalServiceUrl = personalServiceUrl;
    }

    public Optional<MedicalAgendaDto> getAgenda(Long agendaId) {
        try {
            String url = personalServiceUrl + "/api/v1/medical-agenda/" + agendaId;
            MedicalAgendaDto dto = restTemplate.getForObject(url, MedicalAgendaDto.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw ex;
        }
    }

    public List<SlotDto> getAvailableSlots(Long agendaId, LocalDate date) {
        String url = UriComponentsBuilder
                .fromHttpUrl(personalServiceUrl + "/api/v1/medical-agenda/" + agendaId + "/available-slots")
                .queryParam("date", date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .toUriString();
        SlotDto[] slots = restTemplate.getForObject(url, SlotDto[].class);
        return slots != null ? Arrays.asList(slots) : Collections.emptyList();
    }
}
