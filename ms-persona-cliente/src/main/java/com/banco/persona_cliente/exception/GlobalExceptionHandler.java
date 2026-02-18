package com.banco.persona_cliente.exception;

import com.banco.persona_cliente.model.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Atrapa específicamente errores de estado (como el 404 que lanzamos en el Service)
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResponseStatusException(ResponseStatusException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(ex.getReason());
        error.setTimestamp(OffsetDateTime.now());
        error.setCode(ex.getStatusCode().value());

        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(error));
    }

    // Atrapa errores generales del servidor
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneralException(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage("Error interno en el servidor: " + ex.getMessage());
        error.setTimestamp(OffsetDateTime.now());
        error.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }

    // Validación de datos de entrada (Bean Validation)
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(WebExchangeBindException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String fieldName = fieldError != null ? fieldError.getField() : "desconocido";
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "valor inválido";

        if (errorMessage != null && errorMessage.contains("coincidir con")) {
            errorMessage = "contiene caracteres no permitidos o el formato es incorrecto.";
        }

        ErrorResponse error = new ErrorResponse();
        error.setMessage("Datos inválidos en el campo '" + fieldName + "': " + errorMessage);
        error.setTimestamp(OffsetDateTime.now());
        error.setCode(HttpStatus.BAD_REQUEST.value());

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    // Validación de datos duplicados
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage("Error de validación: ya existe la identificacion guardada previamente.");
        error.setTimestamp(OffsetDateTime.now());
        error.setCode(HttpStatus.CONFLICT.value());
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(error));
    }
}