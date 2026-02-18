package com.banco.persona_cliente.controller;

import com.banco.persona_cliente.api.CustomersApi;
import com.banco.persona_cliente.mapper.CustomerMapper;
import com.banco.persona_cliente.model.Customer;
import com.banco.persona_cliente.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @Override
    public Mono<ResponseEntity<Flux<Customer>>> getAllCustomers(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
                customerService.findAll().map(customerMapper::toDto)
        ));
    }

    @Override
    public Mono<ResponseEntity<Customer>> customersPost(Mono<Customer> customer, ServerWebExchange exchange) {
        return customer
                .map(customerMapper::toEntity)
                .flatMap(customerService::save)
                .map(customerMapper::toDto)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @Override
    public Mono<ResponseEntity<Customer>> getCustomerById(Long id, ServerWebExchange exchange) {
        return customerService.findById(id)
                .map(customerMapper::toDto)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Customer>> updateCustomer(Long id, Mono<Customer> customer, ServerWebExchange exchange) {
        return customer
                .map(customerMapper::toEntity)
                .flatMap(details -> customerService.update(id, details))
                .map(customerMapper::toDto)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteCustomer(Long id, ServerWebExchange exchange) {
        return customerService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    //IDENTIFICACION
    @Override
    public Mono<ResponseEntity<Customer>> getCustomerByIdentification(String identification, ServerWebExchange exchange) {
        return customerService.findByIdentification(identification)
                .map(customerMapper::toDto)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Customer>> updateCustomerByIdentification(String identification, Mono<Customer> customer, ServerWebExchange exchange) {
        return customer
                .map(customerMapper::toEntity)
                .flatMap(details -> customerService.update(identification, details))
                .map(customerMapper::toDto)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteCustomerByIdentification(String identification, ServerWebExchange exchange) {
        return customerService.delete(identification)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

}
