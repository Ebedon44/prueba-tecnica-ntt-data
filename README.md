Prueba Técnica: Ecosistema de Microservicios Bancarios
Este repositorio contiene la solución a la prueba técnica para NTT DATA, la cual consiste en un ecosistema de microservicios para la gestión de clientes, cuentas bancarias y movimientos financieros.

Arquitectura del Sistema
El sistema utiliza una arquitectura de microservicios reactivos, implementados con Spring WebFlux, asegurando alta escalabilidad y manejo eficiente de recursos.

Componentes Principales:
Microservicio Persona-Cliente (MS1): Gestiona la información de personas y clientes. Se comunica de forma asíncrona mediante RabbitMQ para notificar la eliminación de registros.

Microservicio Cuenta-Movimientos (MS2): Gestiona la lógica de cuentas bancarias y transacciones. Realiza validaciones síncronas contra el MS1 vía WebClient y escucha eventos de mensajería para mantener la integridad de los datos.

Infraestructura: Orquestada totalmente con Docker Compose, incluyendo una base de datos PostgreSQL y un servidor de mensajería RabbitMQ.

Guía de Inicio Rápido
Requisitos Previos:
Docker y Docker Desktop instalados.

Maven 3.8+ (opcional, para compilación manual).

Despliegue con Docker:
Para levantar todo el ecosistema (Base de datos, Mensajería y Microservicios), ejecuta el siguiente comando en la raíz del proyecto:

Bash
docker compose up -d --build
Puertos Expuestos:
MS Persona-Cliente: http://localhost:8080

MS Cuenta-Movimientos: http://localhost:8081

RabbitMQ Management: http://localhost:15672 (user/pass: guest)

📑 Documentación de la API (Contratos)
La solución ha sido desarrollada bajo un enfoque API-First. Los contratos OpenAPI (Swagger) y las pruebas se encuentran en la carpeta /Documentacion:

Especificaciones YAML: Archivos compatibles con Swagger Editor que definen los contratos de cada microservicio.

Colección de Postman: Se incluye el archivo .json con todas las peticiones configuradas para probar el flujo completo (Creación de cliente -> Creación de cuenta -> Movimientos -> Reportes).

🛠️ Stack Tecnológico
Java 21 / Spring Boot 3

Spring WebFlux (Programación Reactiva)

Spring Data R2DBC (Acceso a datos no bloqueante)

PostgreSQL

RabbitMQ

Docker & Docker Compose

JUnit 5 & Mockito (Pruebas Unitarias)
