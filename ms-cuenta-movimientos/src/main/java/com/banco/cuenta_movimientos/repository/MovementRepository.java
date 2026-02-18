package com.banco.cuenta_movimientos.repository;
import com.banco.cuenta_movimientos.entity.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovementRepository extends JpaRepository<Movement,Integer> {

    @Query("SELECT m FROM Movement m JOIN FETCH m.account")
    List<Movement> findAllWithAccount();

    // Trae los movimientos de una cuenta específica ordenados por fecha
    @Query("SELECT m FROM Movement m JOIN FETCH m.account a WHERE a.accountNumber = :accountNumber ORDER BY m.date DESC")
    List<Movement> findByAccountNumber(@Param("accountNumber") String accountNumber);

    // Reporte de movimientos por cliente y rango de fechas
    @Query("SELECT m FROM Movement m JOIN FETCH m.account a WHERE a.customerIdentification = :clientId AND m.date BETWEEN :startDate AND :endDate ORDER BY m.date DESC")
    List<Movement> findReportByClientAndDateRange(
            @Param("clientId") String clientId,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);
}
