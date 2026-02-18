package com.banco.persona_cliente.repository;

import com.banco.persona_cliente.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRespository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByIdentification(String identification);
}
