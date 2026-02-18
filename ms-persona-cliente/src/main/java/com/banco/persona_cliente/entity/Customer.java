package com.banco.persona_cliente.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "customer")
@PrimaryKeyJoinColumn(name = "customer_id")
public class Customer extends Person {
    private String password;
    private Boolean status;
}
