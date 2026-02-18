package com.banco.persona_cliente.service;

import com.banco.persona_cliente.entity.Customer;
import com.banco.persona_cliente.repository.CustomerRespository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRespository customerRepository;
    @InjectMocks
    private CustomerService customerService;
    private Customer customer;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Erick Bedón");
        customer.setIdentification("1234567");
        customer.setStatus(true);

    }

    @Test
    void findById_WhenCustomerExists_ReturnsCustomer()
    {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        Mono<Customer> result = customerService.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(customer ->
                        customer.getName().equals("Erick Bedón") &&
                                customer.getId().equals(1L))
                .verifyComplete();

    }

    @Test
    void findById_WhenCustomerDoesNotExist_ReturnsNotFound() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());
        Mono<Customer> result = customerService.findById(99L);
        StepVerifier.create(result)
                .expectErrorMessage("Cliente con ID 99 no existe")
                .verify();
    }

    @Test
    void updateCustomer_WhenCustomerExists_ReturnsUpdatedCustomer()
    {
        Customer newData = new Customer();
        newData.setName("Erick Editado");
        newData.setPhone("0999999999");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mono<Customer> result = customerService.update(1L, newData);

        StepVerifier.create(result)
                .expectNextMatches(updatedCustomer ->
                        updatedCustomer.getId().equals(1L) &&
                                updatedCustomer.getName().equals("Erick Editado") &&
                                updatedCustomer.getPhone().equals("0999999999"))
                .verifyComplete();

    }
}
