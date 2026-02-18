package com.banco.cuenta_movimientos.controller;

import com.banco.cuenta_movimientos.MovementsApi;
import com.banco.cuenta_movimientos.mapper.MovementMapper;
import com.banco.cuenta_movimientos.model.Movement;
import com.banco.cuenta_movimientos.service.MovementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MovementController implements MovementsApi {

    private final MovementService movementService;
    private final MovementMapper movementMapper;

    @Override
    public Mono<ResponseEntity<Movement>> createMovement(Mono<Movement> movementDtoMono, ServerWebExchange exchange) {
        log.info("Inicio de creación de movimiento");
        return movementDtoMono
                .flatMap(dto -> {
                    com.banco.cuenta_movimientos.entity.Movement entity = movementMapper.toEntity(dto);
                    return movementService.createMovement(dto.getAccountNumber(), entity);
                })
                .map(movementMapper::toDto)
                .map(savedDto -> new ResponseEntity<>(savedDto, HttpStatus.CREATED));
    }

    @Override
    public Mono<ResponseEntity<Flux<Movement>>> getAllMovements(ServerWebExchange exchange) {
        log.info("Inicio de obtención de todos los movimientos");
        Flux<Movement> movementsFlux = movementService.findAllMovements()
                .map(movementMapper::toDto);
        return Mono.just(ResponseEntity.ok(movementsFlux));
    }

    public Mono<ResponseEntity<Flux<Movement>>> getMovementsByAccount(String accountNumber, ServerWebExchange exchange) {
        log.info("Inicio de obtención de movimientos de la cuenta {}", accountNumber);
        Flux<Movement> movementsFlux = movementService.findMovementsByAccount(accountNumber)
                .map(movementMapper::toDto);
        return Mono.just(ResponseEntity.ok(movementsFlux));
    }
}
