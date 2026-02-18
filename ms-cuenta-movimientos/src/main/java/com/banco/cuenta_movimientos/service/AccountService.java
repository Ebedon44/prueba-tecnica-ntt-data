package com.banco.cuenta_movimientos.service;

import com.banco.cuenta_movimientos.entity.Account;
import com.banco.cuenta_movimientos.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final WebClient webClient;

    public Flux<Account> findAllAccounts() {
        return Flux.defer(() -> Flux.fromIterable(accountRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(subscription -> log.info("Obteniendo todas las cuentas"));
    }

    public Mono<Account> createAccount(Account account) {
        log.info("Iniciando creación de cuenta");
        return webClient.get()
                .uri("/api/v1/customers/identification/{identification}", account.getCustomerIdentification())
                .retrieve()
                .onStatus(status->status.equals(HttpStatus.NOT_FOUND),response -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,"El cliente no existe")))
                .bodyToMono(Object.class)
                .flatMap(clientExist -> {
                    return Mono.fromCallable(() -> accountRepository.save(account)).subscribeOn(Schedulers.boundedElastic());
                }).doOnSuccess(saved -> log.info("Cuenta registrado exitosamente: {}",saved.getAccountNumber()));
    }

    public Mono<Account> findByNumber(String accountNumber) {
        return Mono.fromCallable(() -> accountRepository.findByAccountNumber(accountNumber))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(Mono::just)
                        .orElseGet(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"))));
    }

    public Mono<Account> updateAccount(String accountNumber, Account accountDetails) {
        return findByNumber(accountNumber) // Reutilizamos el método de búsqueda
                .flatMap(existingAccount -> {
                    log.info("Actualizando cuenta: {}", accountNumber);

                    existingAccount.setAccountType(accountDetails.getAccountType());
                    existingAccount.setStatus(accountDetails.getStatus());

                    return Mono.fromCallable(() -> accountRepository.save(existingAccount))
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .doOnSuccess(updated -> log.info("Cuenta actualizada con éxito: {}", updated.getAccountNumber()));
    }

    public Mono<Void> deleteAccount(String accountNumber) {
        return findByNumber(accountNumber)
                .flatMap(existingAccount -> {
                    log.info("Eliminando cuenta: {}", accountNumber);
                    return Mono.fromRunnable(() -> accountRepository.delete(existingAccount))
                            .subscribeOn(Schedulers.boundedElastic())
                            .then();
                });
    }

    public Mono<Void> deleteAccountsByCustomer(String identificacion) {
        return Flux.defer(() -> Flux.fromIterable(accountRepository.findByCustomerIdentification(identificacion)))
                .flatMap(account -> {
                    account.setStatus(false);
                    return Mono.fromCallable(() -> accountRepository.save(account));
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
