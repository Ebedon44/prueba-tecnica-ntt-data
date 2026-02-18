package com.banco.cuenta_movimientos.listener;

import com.banco.cuenta_movimientos.config.RabbitMQConfig;
import com.banco.cuenta_movimientos.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientesListener {

    private final AccountService accountService;

    @RabbitListener(queues = RabbitMQConfig.CUSTOMER_QUEUE)
    public void handleClienteEliminado(String mensaje) {
        log.info("Mensaje recibido en RabbitMQ: {}", mensaje);

        String[] partes = mensaje.split(",");

        if (partes.length == 2 && "CLIENTE_ELIMINADO".equals(partes[0])) {
            String identificacionReal = partes[1];

            accountService.deleteAccountsByCustomer(identificacionReal)
                    .doOnSuccess(Void -> log.info("Limpieza de cuentas completada para el cliente {}", identificacionReal))
                    .doOnError(error -> log.error("Error al limpiar cuentas: {}", error.getMessage()))
                    .subscribe();
        }
    }
}
