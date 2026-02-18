package com.banco.cuenta_movimientos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cuenta", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "tipo_cuenta", nullable = false)
    private String accountType;

    @Column(name = "saldo_inicial", nullable = false)
    private BigDecimal initialBalance;

    @Column(name = "estado", nullable = false)
    private Boolean status;

    @Column(name = "identificacion_cliente", nullable = false)
    private String customerIdentification;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Movement> movements;

}