package com.banco.persona_cliente.config;

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
    public Queue customerQueue() {
        return new Queue(CUSTOMER_QUEUE);
    }

    @Bean
    public DirectExchange bancoExchange(){
        return new DirectExchange(CUSTOMER_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue clienteQueue, DirectExchange bancoExchange) {
        return BindingBuilder.bind(clienteQueue).to(bancoExchange).with(CUSTOMER_ROUTING_KEY);
    }


}
