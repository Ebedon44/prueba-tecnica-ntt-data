package com.banco.persona_cliente.mapper;

import com.banco.persona_cliente.model.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toDto(com.banco.persona_cliente.entity.Customer entity);

    com.banco.persona_cliente.entity.Customer toEntity(Customer dto);
}
