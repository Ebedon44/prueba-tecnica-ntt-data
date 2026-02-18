package com.banco.persona_cliente.service;

import com.banco.persona_cliente.config.RabbitMQConfig;
import com.banco.persona_cliente.entity.Customer;
import com.banco.persona_cliente.repository.CustomerRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRespository customerRepository;
    private final RabbitTemplate rabbitTemplate;

    public Flux<Customer> findAll() {
        log.info("Inicio de listado de clientes");
        return Flux.defer(() -> Flux.fromIterable(customerRepository.findAll()))
                .doOnComplete(() -> log.info("Listado de clientes completado exitosamente"))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Customer> findById(Long id) {
        log.info("Inicio de búsqueda de cliente con ID: {}", id);
        return Mono.fromCallable(() -> customerRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente con ID " + id + " no existe")))
                .doOnSuccess(customer -> log.info("Cliente encontrado: {}", customer.getName()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Customer> save(Customer customer) {
        log.info("Inicio de guardado de cliente con identificacion: {}", customer.getIdentification());
        return Mono.fromCallable(() -> customerRepository.save(customer))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(saved -> {
                    log.info("Cliente guardado exitosamente en BD con ID: {}", saved.getId());
                    String mensaje = "CLIENTE_CREADO," + saved.getId();
                    rabbitTemplate.convertAndSend(RabbitMQConfig.CUSTOMER_EXCHANGE, RabbitMQConfig.CUSTOMER_ROUTING_KEY, mensaje);
                    log.info("Evento asincrono enviado a RabbitMQ: {}", mensaje);
                })
                .doOnError(error -> log.error("Error al intentar guardar el cliente: {}", error.getMessage()));
    }
    public Mono<Customer> update(Long id, Customer details) {
        log.info("Inicio de actualización del cliente con ID: {}", id);
        return findById(id).flatMap(existing -> {
            log.info("Cliente con ID: {} encontrado, aplicando nuevos datos.", id);

            existing.setName(details.getName());
            existing.setAddress(details.getAddress());
            existing.setPhone(details.getPhone());
            existing.setPassword(details.getPassword());
            existing.setStatus(details.getStatus());

            return Mono.fromCallable(() -> customerRepository.save(existing))
                    .subscribeOn(Schedulers.boundedElastic())
                    .doOnSuccess(savedCustomer -> {
                        log.info("Cliente con ID: {} actualizado exitosamente en la BD", savedCustomer.getId());

                        String mensaje = "CLIENTE_ACTUALIZADO," + savedCustomer.getId() + "," + savedCustomer.getStatus();
                        rabbitTemplate.convertAndSend(RabbitMQConfig.CUSTOMER_EXCHANGE, RabbitMQConfig.CUSTOMER_ROUTING_KEY, mensaje);
                        log.info("Evento asincrono enviado a RabbitMQ: {}", mensaje);
                    })
                    .doOnError(error -> log.error("Error al intentar actualizar el cliente con ID: {} - Detalle: {}", id, error.getMessage()));
        });
    }

    public Mono<Void> delete(Long id) {
        log.info("Iniciando eliminación del cliente con ID: {}", id);
        return findById(id).flatMap(customer ->
                Mono.<Void>fromRunnable(() -> {
                            customerRepository.delete(customer);
                            String mensaje = "CLIENTE_ELIMINADO," + id;
                            rabbitTemplate.convertAndSend(RabbitMQConfig.CUSTOMER_EXCHANGE, RabbitMQConfig.CUSTOMER_ROUTING_KEY, mensaje);
                            log.info("Evento asincrono enviado a RabbitMQ: {}", mensaje);
                        })
                        .subscribeOn(Schedulers.boundedElastic())
                        .doOnSuccess(unused -> log.info("Cliente con ID: {} eliminado exitosamente de la base de datos", id))
                        .doOnError(error -> log.error("Error al intentar eliminar el cliente con ID: {} - Detalle: {}", id, error.getMessage()))
        );
    }

    public Mono<Customer> findByIdentification(String identification) {
        log.info("Buscando cliente con identificacion: {}", identification);

        return Mono.fromCallable(() -> customerRepository.findByIdentification(identification))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalCustomer -> {
                    if (optionalCustomer.isEmpty()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Cliente con identificacion " + identification + " no existe"));
                    }
                    return Mono.just(optionalCustomer.get());
                })
                .doOnSuccess(customer -> log.info("Cliente encontrado: {}", customer.getName()))
                .doOnError(error -> log.error("Error al buscar cliente: {}", error.getMessage()));
    }


    public Mono<Void> delete(String identification) {
        log.info("Iniciando eliminación del cliente con identificacion: {}", identification);
        return findByIdentification(identification).flatMap(customer ->
                Mono.<Void>fromRunnable(() -> {
                            customerRepository.delete(customer);
                            String mensaje = "CLIENTE_ELIMINADO," + identification;
                            rabbitTemplate.convertAndSend(RabbitMQConfig.CUSTOMER_EXCHANGE, RabbitMQConfig.CUSTOMER_ROUTING_KEY, mensaje);
                            log.info("Evento asincrono enviado a RabbitMQ: {}", mensaje);
                        })
                        .subscribeOn(Schedulers.boundedElastic())
                        .doOnSuccess(unused -> log.info("Cliente con ID: {} eliminado exitosamente de la base de datos", identification))
                        .doOnError(error -> log.error("Error al intentar eliminar el cliente con ID: {} - Detalle: {}", identification, error.getMessage()))
        );
    }

    public Mono<Customer> update(String identification, Customer details) {
        log.info("Iniciando actualización del cliente con identificacion: {}", identification);
        return findByIdentification(identification).flatMap(existing -> {
            log.info("Cliente con identificacion: {} encontrado, aplicando nuevos datos.", identification);

            existing.setName(details.getName());
            existing.setAddress(details.getAddress());
            existing.setPhone(details.getPhone());
            existing.setPassword(details.getPassword());
            existing.setStatus(details.getStatus());
            existing.setIdentification(details.getIdentification());

            return Mono.fromCallable(() -> customerRepository.save(existing))
                    .subscribeOn(Schedulers.boundedElastic())
                    .doOnSuccess(savedCustomer -> {
                        log.info("Cliente con identificacion: {} actualizado exitosamente en la BD", savedCustomer.getId());

                        String mensaje = "CLIENTE_ACTUALIZADO," + savedCustomer.getId() + "," + savedCustomer.getStatus();
                        rabbitTemplate.convertAndSend(RabbitMQConfig.CUSTOMER_EXCHANGE, RabbitMQConfig.CUSTOMER_ROUTING_KEY, mensaje);
                        log.info("Evento asincrono enviado a RabbitMQ: {}", mensaje);
                    })
                    .doOnError(error -> log.error("Error al intentar actualizar el cliente con ID: {} - Detalle: {}", identification, error.getMessage()));
        });
    }


}
