package com.banco.cuenta_movimientos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "movements",ignore = true)
    com.banco.cuenta_movimientos.entity.Account toEntity(com.banco.cuenta_movimientos.model.Account accountDto);

    com.banco.cuenta_movimientos.model.Account toDto(com.banco.cuenta_movimientos.entity.Account accountEntity);
}