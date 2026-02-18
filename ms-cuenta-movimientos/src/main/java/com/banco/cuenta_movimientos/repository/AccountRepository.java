package com.banco.cuenta_movimientos.repository;

import com.banco.cuenta_movimientos.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Integer> {

    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByCustomerIdentification(String customerIdentification);
    boolean existsByCustomerIdentification(String customerIdentification);
}
