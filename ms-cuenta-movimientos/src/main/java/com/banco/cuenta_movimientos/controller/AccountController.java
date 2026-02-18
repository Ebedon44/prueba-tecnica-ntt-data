package com.banco.cuenta_movimientos.controller;

import com.banco.cuenta_movimientos.AccountsApi;
import com.banco.cuenta_movimientos.mapper.AccountMapper;
import com.banco.cuenta_movimientos.model.Account;
import com.banco.cuenta_movimientos.service.AccountService;
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
public class AccountController implements AccountsApi {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @Override
    public Mono<ResponseEntity<Flux<Account>>> getAllAccounts(ServerWebExchange exchange) {
        log.info("Inicio de obtención de todas las cuentas");
        Flux<Account> accountsFlux = accountService.findAllAccounts()
                .map(accountMapper::toDto);
        return Mono.just(ResponseEntity.ok(accountsFlux));
    }


    @Override
    public Mono<ResponseEntity<Account>> getAccountByNumber(String accountNumber, ServerWebExchange exchange) {
        log.info("Inicio de obtención de la cuenta: {}", accountNumber);
        return accountService.findByNumber(accountNumber)
                .map(accountMapper::toDto)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Account>> createAccount(Mono<Account> account, ServerWebExchange exchange) {
        log.info("Inicio de creación de cuenta");
        return account
                .map(accountMapper::toEntity)
                .flatMap(accountService::createAccount)
                .map(accountMapper::toDto)
                .map(savedDto -> new ResponseEntity<>(savedDto, HttpStatus.CREATED));
    }

    @Override
    public Mono<ResponseEntity<Account>> updateAccount(String accountNumber, Mono<Account> account, ServerWebExchange exchange) {
        log.info("Inicio de actualización de la cuenta: {}", accountNumber);
        return account
                .map(accountMapper::toEntity)
                .flatMap(entityDetails -> accountService.updateAccount(accountNumber, entityDetails)) // Llamamos a tu lógica de servicio
                .map(accountMapper::toDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAccount(String accountNumber, ServerWebExchange exchange) {
        log.info("Inicio de eliminación de cuenta: {}", accountNumber);
        return accountService.deleteAccount(accountNumber)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
    }
}
