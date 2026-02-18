package com.banco.cuenta_movimientos.controller;

import com.banco.cuenta_movimientos.ReportsApi;
import com.banco.cuenta_movimientos.mapper.MovementMapper;
import com.banco.cuenta_movimientos.model.Movement;
import com.banco.cuenta_movimientos.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReportController implements ReportsApi {

    private final ReportService reportService;
    private final MovementMapper movementMapper;

    @Override
    public Mono<ResponseEntity<Flux<Movement>>> generateReport(
            String clientId,
            LocalDate startDate,
            LocalDate endDate,
            ServerWebExchange exchange) {

        log.info("Inicio de generación de reporte para el cliente: {}", clientId);
        Flux<Movement> reportFlux = reportService.generateReport(clientId, startDate, endDate)
                .map(movementMapper::toDto);

        return Mono.just(ResponseEntity.ok(reportFlux));
    }
}
