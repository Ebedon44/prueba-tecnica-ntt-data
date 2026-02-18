package com.banco.cuenta_movimientos.exception;

import com.banco.cuenta_movimientos.model.ErrorResponse;
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

    //Atrapa el error de "Saldo no disponible"
    @ExceptionHandler(InsufficientBalanceException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInsufficientBalance(InsufficientBalanceException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(ex.getMessage());
        error.setTimestamp(OffsetDateTime.now());
        error.setCode(HttpStatus.BAD_REQUEST.value());

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    // Atrapa errores de validación de datos de entrada
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(WebExchangeBindException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String fieldName = fieldError != null ? fieldError.getField() : "campo";
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "valor inválido";

        ErrorResponse error = new ErrorResponse();
        error.setMessage("Datos inválidos en el campo '" + fieldName + "': " + errorMessage);
        error.setTimestamp(OffsetDateTime.now());
        error.setCode(HttpStatus.BAD_REQUEST.value());

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    // Atrapa errores cuando no se encuentra un recurso
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResponseStatusException(ResponseStatusException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(ex.getReason());
        error.setTimestamp(OffsetDateTime.now());
        error.setCode(ex.getStatusCode().value());

        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(error));
    }

    // Atrapa errores de base de datos (Ej. Numero de cuenta duplicado)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDataIntegrity(DataIntegrityViolationException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage("Error de validación: ya existe un numero de cuenta guardado previamente.");
        error.setTimestamp(OffsetDateTime.now());
        error.setCode(HttpStatus.CONFLICT.value());

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(error));
    }

    // Atrapa cualquier otro error genérico no controlado
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneralException(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage("Error inesperado: " + ex.getMessage());
        error.setTimestamp(OffsetDateTime.now());
        error.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }
}