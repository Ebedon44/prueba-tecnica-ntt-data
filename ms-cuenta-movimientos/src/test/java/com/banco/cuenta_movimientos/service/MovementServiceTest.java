package com.banco.cuenta_movimientos.service;

import com.banco.cuenta_movimientos.entity.Account;
import com.banco.cuenta_movimientos.entity.Movement;
import com.banco.cuenta_movimientos.exception.InsufficientBalanceException;
import com.banco.cuenta_movimientos.repository.AccountRepository;
import com.banco.cuenta_movimientos.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovementServiceTest {

    @Mock
    private MovementRepository movementRepository;
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private MovementService movementService;

    private Account account;
    private Movement movement;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setAccountNumber("12345");
        account.setAccountType("Ahorros");
        account.setInitialBalance(new BigDecimal("100.00"));
        account.setStatus(true);

        movement = new Movement();
        movement.setAmount(new BigDecimal("100.00"));
    }

    @Test
    void createMovement_Deposit() {
        movement.setMovementType("Deposito");

        when(accountRepository.findByAccountNumber("12345")).thenReturn(Optional.of(account));
        when(movementRepository.save(any(Movement.class))).thenAnswer(i -> i.getArguments()[0]);

        Mono<Movement> result = movementService.createMovement("12345", movement);

        StepVerifier.create(result)
                .expectNextMatches(movement ->
                        movement.getBalance().compareTo(new BigDecimal("200")) == 0
                )
                .verifyComplete();
    }

    @Test
    void createMovement_Withdrawal() {
        movement.setMovementType("Retiro");

        when(accountRepository.findByAccountNumber("12345")).thenReturn(Optional.of(account));
        when(movementRepository.save(any(Movement.class))).thenAnswer(i -> i.getArguments()[0]);

        Mono<Movement> result = movementService.createMovement("12345", movement);

        StepVerifier.create(result)
                .expectNextMatches(movement ->
                        movement.getBalance().compareTo(new BigDecimal("0")) == 0
                )
                .verifyComplete();
    }


    @Test
    void createMovement_ThrowsInsufficientBalanceException() {
        movement.setMovementType("Retiro");
        movement.setAmount(new BigDecimal("5000"));

        when(accountRepository.findByAccountNumber("12345")).thenReturn(Optional.of(account));


        Mono<Movement> result = movementService.createMovement("12345", movement);

        StepVerifier.create(result)
                .expectError(InsufficientBalanceException.class)
                .verify();
    }

}
