package com.banco.cuenta_movimientos.controller;

import com.banco.cuenta_movimientos.entity.Account;
import com.banco.cuenta_movimientos.model.Movement;
import com.banco.cuenta_movimientos.repository.AccountRepository;
import com.banco.cuenta_movimientos.repository.MovementRepository;

import org.junit.jupiter.api.BeforeEach; // <-- NUEVO
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort; // <-- NUEVO
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MoventControllerTest {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private MovementRepository movementRepository;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void createMovementReturnUpdateBalance() {
        Account mockAccount = new Account();
        mockAccount.setAccountNumber("478758");
        mockAccount.setInitialBalance(new BigDecimal("2000"));

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(mockAccount));

        when(movementRepository.save(any(com.banco.cuenta_movimientos.entity.Movement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Movement movement = new Movement();
        movement.setAccountNumber("478758");
        movement.setMovementType("Retiro");
        movement.setAmount(500.0);

        webTestClient.post()
                .uri("/api/v1/movements")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(movement)
                .exchange()

                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.balance").isEqualTo(1500.0)
                .jsonPath("$.movementType").isEqualTo("Retiro");
    }
}