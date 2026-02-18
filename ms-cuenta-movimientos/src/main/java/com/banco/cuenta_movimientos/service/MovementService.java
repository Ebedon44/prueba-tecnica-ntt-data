package com.banco.cuenta_movimientos.service;

import com.banco.cuenta_movimientos.entity.Account;
import com.banco.cuenta_movimientos.entity.Movement;
import com.banco.cuenta_movimientos.exception.InsufficientBalanceException;
import com.banco.cuenta_movimientos.repository.AccountRepository;
import com.banco.cuenta_movimientos.repository.MovementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovementService {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Mono<Movement> createMovement(String accountNumber, Movement movement) {

        // El valor de un movimiento debe ser mayor que cero.
        if (movement.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "El valor del movimiento debe ser mayor a cero"));
        }

        return Mono.fromCallable(() -> {
                    Account account = accountRepository.findByAccountNumber(accountNumber)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));

                    BigDecimal currentBalance = account.getInitialBalance();
                    BigDecimal transactionAmount = movement.getAmount();

                    //Verifica la transacción
                    String type = movement.getMovementType().toUpperCase();
                    if (type.contains("RETIRO") || type.contains("DEBITO")) {
                        transactionAmount = transactionAmount.negate();
                    } else if (!type.contains("DEPOSITO") && !type.contains("CREDITO")) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de movimiento inválido. Use 'Retiro' o 'Deposito'");
                    }

                    BigDecimal newBalance = currentBalance.add(transactionAmount);

                    // Alerta de "Saldo no disponible"
                    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                        log.error("Intento de retiro fallido por falta de fondos. Cuenta: {}", accountNumber);
                        throw new InsufficientBalanceException("Saldo no disponible");
                    }

                    account.setInitialBalance(newBalance);
                    movement.setAmount(transactionAmount);
                    movement.setBalance(newBalance);
                    movement.setDate(LocalDateTime.now());
                    movement.setAccount(account);

                    accountRepository.save(account);
                    return movementRepository.save(movement);

                }).subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(m -> log.info("Movimiento registrado con éxito. Nuevo saldo: {}", m.getBalance()));
    }


    public Flux<Movement> findAllMovements() {
        return Flux.defer(() -> Flux.fromIterable(movementRepository.findAllWithAccount()))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(subscription -> log.info("Obteniendo todos los movimientos con sus cuentas"));
    }



    public Flux<Movement> findMovementsByAccount(String accountNumber) {
        log.info("Buscando cuenta {} para listar sus movimientos...", accountNumber);

        return Mono.fromCallable(() -> accountRepository.findByAccountNumber(accountNumber))
                .subscribeOn(Schedulers.boundedElastic())
                // Usamos flatMapMany porque pasamos de buscar 1 cuenta (Mono) a listar N movimientos (Flux)
                .flatMapMany(optionalAccount -> {
                    if (optionalAccount.isEmpty()) {
                        return Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "La cuenta " + accountNumber + " no existe en el sistema"));
                    }
                    return Flux.fromIterable(movementRepository.findByAccountNumber(accountNumber));
                });
    }
}
