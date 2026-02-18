package com.banco.cuenta_movimientos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface MovementMapper {

    @Mapping(source = "account.accountNumber", target = "accountNumber")
    com.banco.cuenta_movimientos.model.Movement toDto(com.banco.cuenta_movimientos.entity.Movement movementEntity);

    @Mapping(target = "account", ignore = true)
    com.banco.cuenta_movimientos.entity.Movement toEntity(com.banco.cuenta_movimientos.model.Movement movementDto);

    default OffsetDateTime map(LocalDateTime value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime value) {
        return value == null ? null : value.toLocalDateTime();
    }
}