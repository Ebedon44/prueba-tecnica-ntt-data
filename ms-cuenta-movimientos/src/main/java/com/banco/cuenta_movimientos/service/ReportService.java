package com.banco.cuenta_movimientos.service;

import com.banco.cuenta_movimientos.entity.Movement;
import com.banco.cuenta_movimientos.repository.AccountRepository;
import com.banco.cuenta_movimientos.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;

    private final WebClient webClient;

    public Flux<Movement> generateReport(String clientId, LocalDate startDate, LocalDate endDate) {
        log.info("Validando si el cliente {} existe", clientId);
        return webClient.get()
                .uri("/api/v1/customers/identification/{identification}", clientId)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND),
                        response -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Error: El cliente con identificación " + clientId + " no existe.")))
                .bodyToMono(Object.class)
                .flatMapMany(clientExists -> {

                    return Mono.fromCallable(() -> accountRepository.existsByCustomerIdentification(clientId))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMapMany(hasAccounts -> {

                                if (!hasAccounts) {
                                    log.warn("El cliente {} existe, pero no tiene cuentas.", clientId);
                                    return Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                            "El cliente existe en el banco, pero aún no tiene cuentas registradas."));
                                }

                                log.info("Cliente válido y con cuentas. Buscando movimientos.");
                                LocalDateTime start = startDate.atStartOfDay();
                                LocalDateTime end = endDate.atTime(23, 59, 59);

                                return Flux.defer(() -> Flux.fromIterable(
                                        movementRepository.findReportByClientAndDateRange(clientId, start, end)
                                )).subscribeOn(Schedulers.boundedElastic());
                            });
                });
    }
}
