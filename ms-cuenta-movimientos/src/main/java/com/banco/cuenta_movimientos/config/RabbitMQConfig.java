package com.banco.cuenta_movimientos.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String CUSTOMER_QUEUE = "customer.queue";
    public static final String CUSTOMER_EXCHANGE = "customer.exchange";
    public static final String CUSTOMER_ROUTING_KEY = "customer.routingKey";
    @Bean
    public Queue clienteEliminadoQueue() {
        return new Queue(CUSTOMER_QUEUE, true);
    }
    @Bean
    public DirectExchange clientesExchange() {
        return new DirectExchange(CUSTOMER_EXCHANGE);
    }

    @Bean
    public Binding bindingClienteEliminado(Queue clienteEliminadoQueue, DirectExchange clientesExchange) {
        return BindingBuilder.bind(clienteEliminadoQueue).to(clientesExchange).with(CUSTOMER_ROUTING_KEY);
    }
}
