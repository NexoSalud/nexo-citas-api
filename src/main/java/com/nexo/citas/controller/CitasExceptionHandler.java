package com.nexo.citas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador de excepciones local al modulo Citas.
 *
 * El {@code GlobalExceptionHandler} compartido (nexo-core-spring) solo mapea
 * {@code EntityNotFoundException} y {@code MethodArgumentNotValidException};
 * sin este advice, las validaciones de negocio de agendamiento (slot ocupado,
 * agenda fuera de vigencia/horario, transicion de estado invalida) caen al
 * manejador por defecto de Spring Boot y responden 500 en vez de 400/409.
 * Se agrega aqui (no en la libreria compartida) para no afectar el
 * comportamiento de los demas microservicios.
 */
@RestControllerAdvice
public class CitasExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message != null && !message.isBlank() ? message : status.getReasonPhrase());
        return new ResponseEntity<>(body, status);
    }
}
