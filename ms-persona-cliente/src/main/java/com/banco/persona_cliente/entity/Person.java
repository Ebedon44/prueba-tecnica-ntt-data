package com.banco.persona_cliente.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String name;

    @Column(name = "edad", nullable = false)
    private Integer age;

    @Column(unique = true, nullable = false)
    private String identification;

    @Column(name = "direccion", nullable = false)
    private String address;

    @Column(name = "telefono", nullable = false)
    private String phone;

}
